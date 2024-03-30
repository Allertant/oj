package com.shiyixi.ojbackendjudgeservice.judge.codesandbox;


import com.shiyixi.ojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.shiyixi.ojbackendmodel.codesandbox.ExecuteCodeResponse;

public interface CodeSandbox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
