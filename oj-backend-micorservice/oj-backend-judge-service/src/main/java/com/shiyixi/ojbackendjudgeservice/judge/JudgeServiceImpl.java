package com.shiyixi.ojbackendjudgeservice.judge;


import cn.hutool.json.JSONUtil;
import com.shiyixi.ojbackendcommon.common.ErrorCode;
import com.shiyixi.ojbackendcommon.exception.BusinessException;
import com.shiyixi.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.shiyixi.ojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.shiyixi.ojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.shiyixi.ojbackendjudgeservice.judge.strategy.JudgeContext;
import com.shiyixi.ojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.shiyixi.ojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.shiyixi.ojbackendmodel.codesandbox.JudgeInfo;
import com.shiyixi.ojbackendmodel.dto.question.JudgeCase;
import com.shiyixi.ojbackendmodel.entity.Question;
import com.shiyixi.ojbackendmodel.entity.QuestionSubmit;
import com.shiyixi.ojbackendmodel.enums.JudgeInfoMessageEnum;
import com.shiyixi.ojbackendmodel.enums.QuestionSubmitStatusEnum;
import com.shiyixi.ojbackendserviceclient.service.QuestionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionFeignClient questionFeignClient;

    @Value("${codesandbox.type:example}")
    private String type;
    @Resource
    private JudgeManager judgeManager;
    @Resource
    private CodeSandboxFactory codeSandboxFactory;

    @Override
    public QuestionSubmit doJudge(Long questionSubmitId) {
        // 1、校验数据是否合法
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }

        // 2、加锁，保证幂等性
        Integer status = questionSubmit.getStatus();
        if (!status.equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean b = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        // 3、准备沙箱参数
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        // 4、调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = codeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox); // 获取代理对象
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(code).language(language).inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        // 如果未能执行成功，则要将判题状态修改回等待中
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);


        List<String> outputList = executeCodeResponse.getOutputList();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        Integer codeResponseStatus = executeCodeResponse.getStatus();

        // 5 根据代码沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(judgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setQuestion(question);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestionSubmit(questionSubmit);
        judgeContext.setStatus(codeResponseStatus);
        JudgeInfo responseJudgeInfo = judgeManager.doJudge(judgeContext);

        // 6、修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        // 是否成功
        Integer succeedValue = QuestionSubmitStatusEnum.SUCCEED.getValue();
        Integer failedValue = QuestionSubmitStatusEnum.FAILED.getValue();
        boolean equals = JudgeInfoMessageEnum.ACCEPTED.getValue().equals(responseJudgeInfo.getMessage());

        // 修改题目模块的成功数
        if (equals) {
            log.info("题目成功，题目 id 为：{}", questionSubmitId);
            questionFeignClient.updateQuestionAcceptedNumById(questionId);
        }

        // 修改提交的结果
        questionSubmitUpdate.setStatus(equals ? succeedValue : failedValue);
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(responseJudgeInfo)); // 判题信息
        // 更新数据库
        b = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!b) throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        // 返回数据
        return questionFeignClient.getQuestionSubmitById(questionSubmitId);
    }
}
