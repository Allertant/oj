package com.shiyixi.oj.judge;


import cn.hutool.json.JSONUtil;
import com.shiyixi.oj.common.ErrorCode;
import com.shiyixi.oj.exception.BusinessException;
import com.shiyixi.oj.judge.codesandbox.CodeSandbox;
import com.shiyixi.oj.judge.codesandbox.CodeSandboxFactory;
import com.shiyixi.oj.judge.codesandbox.CodeSandboxProxy;
import com.shiyixi.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.shiyixi.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.shiyixi.oj.judge.strategy.JudgeContext;
import com.shiyixi.oj.model.dto.question.JudgeCase;
import com.shiyixi.oj.judge.codesandbox.model.JudgeInfo;
import com.shiyixi.oj.model.entity.Question;
import com.shiyixi.oj.model.entity.QuestionSubmit;
import com.shiyixi.oj.model.enums.JudgeInfoMessageEnum;
import com.shiyixi.oj.model.enums.QuestionSubmitLanguageEnum;
import com.shiyixi.oj.model.enums.QuestionSubmitStatusEnum;
import com.shiyixi.oj.service.QuestionService;
import com.shiyixi.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;
    @Value("${codesandbox.type:example}")
    private String type;
    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(Long questionSubmitId) {
        // 1、校验数据是否合法
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
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
        boolean b = questionSubmitService.updateById(questionSubmitUpdate);
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
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox); // 获取代理对象
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(code).language(language).inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();

        // 5 根据代码沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(judgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setQuestion(question);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo responseJudgeInfo = judgeManager.doJudge(judgeContext);

        // 6、修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        // 是否成功
        boolean equals = JudgeInfoMessageEnum.ACCEPTED.getValue().equals(responseJudgeInfo.getMessage());
        questionSubmitUpdate.setStatus(equals ? QuestionSubmitStatusEnum.SUCCEED.getValue() : QuestionSubmitStatusEnum.FAILED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(responseJudgeInfo)); // 判题信息
        b = questionSubmitService.updateById(questionSubmitUpdate);
        if (!b) throw new BusinessException(ErrorCode.SYSTEM_ERROR);

        return questionSubmitService.getById(questionSubmitId);
    }
}
