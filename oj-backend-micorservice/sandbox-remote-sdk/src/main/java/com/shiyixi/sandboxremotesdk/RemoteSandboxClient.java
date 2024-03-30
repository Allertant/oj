package com.shiyixi.sandboxremotesdk;

import com.shiyixi.sandboxremotesdk.config.SandboxProperties;
import com.shiyixi.sandboxremotesdk.model.SandboxExecuteRequest;
import com.shiyixi.sandboxremotesdk.model.SandboxExecuteResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "remote-service", url = "http://119.91.61.248:8090")
@EnableConfigurationProperties({SandboxProperties.class})
public interface RemoteSandboxClient {
    @PostMapping("/executeCode")
    SandboxExecuteResponse executeCode(
            @RequestBody SandboxExecuteRequest executeCodeRequest,
            @RequestHeader(value = "auth", defaultValue = "${sandbox.secretKey}") String secret
    );
}
