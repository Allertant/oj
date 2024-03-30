package com.shiyixi.ojsanbox.common;

import com.shiyixi.ojsanbox.model.JudgeInfo;
import com.shiyixi.ojsanbox.model.SandboxResponse;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义异常类
 *
 * @author shiyixi
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;
    private final JudgeInfo judgeInfo;
    private final List<String> outputList;

    public BusinessException(SandboxResponse sandboxResponse, JudgeInfo judgeInfo, List<String> outputList) {
        super(sandboxResponse.getValue());
        this.code = sandboxResponse.getStatus();
        this.judgeInfo = judgeInfo;
        this.outputList = outputList;
    }

    /**
     * 应对于那些没有运行的错误
     * @param sandboxResponse 沙箱响应
     */
    public BusinessException(SandboxResponse sandboxResponse) {
        super(sandboxResponse.getValue());
        this.code = sandboxResponse.getStatus();
        this.judgeInfo = new JudgeInfo(0L, sandboxResponse.getValue(), 0L);
        this.outputList = new ArrayList<>();
    }

    /**
     * 应对于那些自定义的错误信息
     * @param sandboxResponse 沙箱响应
     * @param message 自定义消息
     */
    public BusinessException(SandboxResponse sandboxResponse, String message) {
        super(message);
        this.code = sandboxResponse.getStatus();
        this.judgeInfo = new JudgeInfo(0L, sandboxResponse.getValue(), 0L);
        this.outputList = new ArrayList<>();
    }

}

