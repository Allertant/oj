package com.shiyixi.sandboxremotesdk;

import com.shiyixi.sandboxremotesdk.model.SandboxExecuteRequest;
import com.shiyixi.sandboxremotesdk.model.SandboxExecuteResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class RemoteSandboxSender {
    private RemoteSandboxClient remoteSandboxClient;
    private String secretKey;

    public SandboxExecuteResponse doSend(SandboxExecuteRequest sandboxExecuteRequest) {
        return remoteSandboxClient.executeCode(sandboxExecuteRequest, secretKey);
    }
}
