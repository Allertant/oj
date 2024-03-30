package com.shiyixi.ojbackendemailservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTypeEnum {
    VERIFYCODE("验证码"),
    LOG("日志");

    private final String emailType;
}
