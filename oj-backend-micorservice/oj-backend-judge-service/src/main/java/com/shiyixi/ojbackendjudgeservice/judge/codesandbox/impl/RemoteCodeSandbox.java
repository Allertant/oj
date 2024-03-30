package com.shiyixi.ojbackendjudgeservice.judge.codesandbox.impl;

import com.shiyixi.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.shiyixi.ojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.shiyixi.ojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.shiyixi.sandboxremotesdk.RemoteSandboxSender;
import com.shiyixi.sandboxremotesdk.model.JudgeInfo;
import com.shiyixi.sandboxremotesdk.model.SandboxExecuteRequest;
import com.shiyixi.sandboxremotesdk.model.SandboxExecuteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 远程代码沙箱
 */
@Slf4j
@Component
public class RemoteCodeSandbox implements CodeSandbox {

    @Resource
    private RemoteSandboxSender remoteSandboxSender;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("正在向远程发送请求，题目信息为：{}", executeCodeRequest);
        SandboxExecuteRequest sandboxExecuteRequest = new SandboxExecuteRequest();
        BeanUtils.copyProperties(executeCodeRequest, sandboxExecuteRequest);

        SandboxExecuteResponse sandboxExecuteResponse = remoteSandboxSender.doSend(sandboxExecuteRequest);

        JudgeInfo judgeInfo = sandboxExecuteResponse.getJudgeInfo();
        com.shiyixi.ojbackendmodel.codesandbox.JudgeInfo judgeInfo1 = new com.shiyixi.ojbackendmodel.codesandbox.JudgeInfo();
        BeanUtils.copyProperties(judgeInfo, judgeInfo1);

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        BeanUtils.copyProperties(sandboxExecuteResponse, executeCodeResponse);
        executeCodeResponse.setJudgeInfo(judgeInfo1);
        log.info("接收到远程发来的响应，响应信息为：{}", executeCodeResponse);
        return executeCodeResponse;
    }
}
