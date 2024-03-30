package com.shiyixi.ojbackendjudgeservice.judge.codesandbox;

import com.shiyixi.ojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.shiyixi.ojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.shiyixi.ojbackendjudgeservice.judge.codesandbox.impl.ThirdPartySandbox;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 静态工厂模式
 * 根据传入的字符串创建指定的代码沙箱示例
 */
@Component
public class CodeSandboxFactory {
    @Resource
    private ExampleCodeSandbox exampleCodeSandbox;
    @Resource
    private RemoteCodeSandbox remoteCodeSandbox;

    @Resource
    private ThirdPartySandbox thirdPartySandbox;

    private CodeSandboxFactory() {
    }

    public CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return exampleCodeSandbox;
            case "remote":
                return remoteCodeSandbox;
            case "thirdParty":
                return thirdPartySandbox;
            default:
                return exampleCodeSandbox;
        }
    }
}
