package com.shiyixi.ojsanbox.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 判题信息消息枚举
 *
 * @author shiyixi
 */
@Getter
@AllArgsConstructor
public enum SandboxResponse {

    SUCCESS(0, "成功"),
    AUTH_FAILED(1, "身份验证错误"),
    PARAM_ERROR(2, "参数错误"),
    COMPILE_ERROR(3, "编译错误"),
    RUN_CODE_ERROR(4, "运行错误"),
    SYSTEM_ERROR(5, "系统错误");
    ;

    private final int status;
    private final String value;
}