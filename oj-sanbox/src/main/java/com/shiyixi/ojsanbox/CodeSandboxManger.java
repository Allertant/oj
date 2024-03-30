package com.shiyixi.ojsanbox;

import com.shiyixi.ojsanbox.common.BusinessException;
import com.shiyixi.ojsanbox.model.ExecuteCodeRequest;
import com.shiyixi.ojsanbox.model.ExecuteCodeResponse;
import com.shiyixi.ojsanbox.model.LanguageEnum;
import com.shiyixi.ojsanbox.model.SandboxResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CodeSandboxManger implements CodeSandbox {

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;
//    private JavaDockerCodeSandbox javaDockerCodeSandbox;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        String language = executeCodeRequest.getLanguage();

        if (language.equals(LanguageEnum.JAVA.getValue())) {
            return javaNativeCodeSandbox.executeCode(executeCodeRequest);
        }
        throw new BusinessException(SandboxResponse.PARAM_ERROR, "没有该种编程语言");
    }
}
