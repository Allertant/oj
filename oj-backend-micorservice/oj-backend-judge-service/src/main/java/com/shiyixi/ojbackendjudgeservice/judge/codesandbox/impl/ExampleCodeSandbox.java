package com.shiyixi.ojbackendjudgeservice.judge.codesandbox.impl;


import com.shiyixi.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.shiyixi.ojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.shiyixi.ojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.shiyixi.ojbackendmodel.codesandbox.JudgeInfo;
import com.shiyixi.ojbackendmodel.enums.JudgeInfoMessageEnum;
import com.shiyixi.ojbackendmodel.enums.QuestionSubmitStatusEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
