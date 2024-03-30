package com.shiyixi.ojsanbox.common;

import com.shiyixi.ojsanbox.model.ExecuteCodeResponse;
import com.shiyixi.ojsanbox.model.JudgeInfo;
import com.shiyixi.ojsanbox.model.SandboxResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理器
 *
 * @author shiyixi
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     * @param e 异常信息
     * @return response
     */
    @ExceptionHandler(BusinessException.class)
    public ExecuteCodeResponse businessExceptionHandler(BusinessException e) {
        log.error("BusinessException:", e);

        String message = e.getMessage();
        int code = e.getCode();
        JudgeInfo judgeInfo = e.getJudgeInfo();
        List<String> outputList = e.getOutputList();

        return new ExecuteCodeResponse(code, judgeInfo, outputList, message);
    }

    /**
     * 处理运行时异常，全部都归结于系统异常
     * @param e 异常信息
     * @return response
     */
    @ExceptionHandler(RuntimeException.class)
    public ExecuteCodeResponse runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException:", e);

        String message = e.getMessage();

        SandboxResponse systemError = SandboxResponse.SYSTEM_ERROR;
        String messageInfo = systemError.getValue();
        int code = systemError.getStatus();

        JudgeInfo judgeInfo = new JudgeInfo(0L, messageInfo, 0L);
        List<String> outputList = new ArrayList<>();

        return new ExecuteCodeResponse(code, judgeInfo, outputList, message);
    }

}