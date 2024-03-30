package com.shiyixi.oj.judge.codesandbox.impl;

import com.shiyixi.oj.judge.codesandbox.CodeSandbox;
import com.shiyixi.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.shiyixi.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.shiyixi.oj.judge.codesandbox.model.JudgeInfo;
import com.shiyixi.oj.model.enums.JudgeInfoMessageEnum;
import com.shiyixi.oj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemory(100L);
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setTime(100L);

        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        executeCodeResponse.setJudgeInfo(judgeInfo);
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("执行测试成功");

        return executeCodeResponse;
    }
}
