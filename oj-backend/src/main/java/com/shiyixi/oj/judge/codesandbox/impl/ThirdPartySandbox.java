package com.shiyixi.oj.judge.codesandbox.impl;

import com.shiyixi.oj.judge.codesandbox.CodeSandbox;
import com.shiyixi.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.shiyixi.oj.judge.codesandbox.model.ExecuteCodeResponse;

public class ThirdPartySandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
