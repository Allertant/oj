package com.shiyixi.ojbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.shiyixi.ojbackendcommon.common.ErrorCode;
import com.shiyixi.ojbackendcommon.exception.BusinessException;
import com.shiyixi.ojbackendmodel.codesandbox.JudgeInfo;
import com.shiyixi.ojbackendmodel.dto.question.JudgeCase;
import com.shiyixi.ojbackendmodel.dto.question.JudgeConfig;
import com.shiyixi.ojbackendmodel.entity.Question;
import com.shiyixi.ojbackendmodel.enums.JudgeInfoMessageEnum;


import java.nio.BufferOverflowException;
import java.util.List;
import java.util.Optional;

/**
 * Java 程序的判题策略
 * 1. 接收
 * 2. 答案错误
 *  - 数目不符
 *  - 答案内容对不上
 * 3. 空间越界
 * 4. 时间越界
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {

    private static final Long JAVA_PROGRAM_TIME_COST = 1000L;

    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        // 1. 获取上下文对象
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Integer status = judgeContext.getStatus(); // 远程服务的状态

        // 题目判断限制
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);

        // 2. 返回对象
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);
        // 默认为成功
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());

        if (status != 0) {
            /*
             * 远程相应为不成功
             */

            if (status == 1 || status == 2) {
                // 系统错误
                judgeInfoResponse.setMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
            }else if (status == 3) {
                // 编译错误
                judgeInfoResponse.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            }else if (status == 4) {
                /*
                 * 运行中发生的异常
                 */
                judgeInfoResponse.setMessage(judgeInfo.getMessage());
            }else {
                judgeInfoResponse.setMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
            }
            return judgeInfoResponse;
        }

        // 3. 输入输出进行检验
        // 输入输出数目不符
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 输入输出内容不符
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }

        // 4. 运行限制信息和判断逻辑
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long memoryLimit = judgeConfig.getMemoryLimit();
        Long timeLimit = judgeConfig.getTimeLimit();
        // 空间超限
        if (memory > memoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        // 时间超限
        if ((time - JAVA_PROGRAM_TIME_COST) > timeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 5. 返回校验结果
        return judgeInfoResponse;
    }
}
