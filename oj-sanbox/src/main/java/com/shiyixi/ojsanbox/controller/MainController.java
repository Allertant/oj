package com.shiyixi.ojsanbox.controller;

import com.shiyixi.ojsanbox.JavaDockerCodeSandbox;
import com.shiyixi.ojsanbox.JavaNativeCodeSandbox;
import com.shiyixi.ojsanbox.common.BusinessException;
import com.shiyixi.ojsanbox.controller.verify.ExecuteCodeRequestVerify;
import com.shiyixi.ojsanbox.model.ExecuteCodeRequest;
import com.shiyixi.ojsanbox.model.ExecuteCodeResponse;
import com.shiyixi.ojsanbox.model.SandboxResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.Semaphore;

@RestController
@Slf4j
@RequestMapping("/")
public class MainController {

    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;

    @Resource
    private JavaDockerCodeSandbox javaDockerCodeSandbox;

    @GetMapping("/health")
    public String healthCheck() {
        return "ok";
    }

    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request,
                                           HttpServletResponse response) {
        // 基本认证
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        System.out.println(authHeader);
        if (!AUTH_REQUEST_SECRET.equals(authHeader)) {
            throw new BusinessException(SandboxResponse.AUTH_FAILED);
        }
        // 参数校验
        ExecuteCodeRequestVerify.verify(executeCodeRequest);

        log.info("执行请求：{}", executeCodeRequest);

        // 调用方法执行
        ExecuteCodeResponse executeCodeResponse = javaDockerCodeSandbox.executeCode(executeCodeRequest);
        return executeCodeResponse;

    }
}
