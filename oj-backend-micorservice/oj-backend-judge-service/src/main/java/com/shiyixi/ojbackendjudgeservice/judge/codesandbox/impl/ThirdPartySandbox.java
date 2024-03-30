package com.shiyixi.ojbackendjudgeservice.judge.codesandbox.impl;

import com.shiyixi.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.shiyixi.ojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.shiyixi.ojbackendmodel.codesandbox.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

@Component
public class ThirdPartySandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
