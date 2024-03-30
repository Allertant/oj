package com.shiyixi.ojsanbox;

import com.shiyixi.ojsanbox.model.ExecuteCodeRequest;
import com.shiyixi.ojsanbox.model.ExecuteCodeResponse;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {
    @Override
    public File saveCodeToFile(String code) {
        return super.saveCodeToFile(code);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
