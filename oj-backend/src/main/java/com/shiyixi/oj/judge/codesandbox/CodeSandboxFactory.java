package com.shiyixi.oj.judge.codesandbox;

import com.shiyixi.oj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.shiyixi.oj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.shiyixi.oj.judge.codesandbox.impl.ThirdPartySandbox;

/**
 * 静态工厂模式
 * 根据传入的字符串创建指定的代码沙箱示例
 */
public class CodeSandboxFactory {
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartySandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
