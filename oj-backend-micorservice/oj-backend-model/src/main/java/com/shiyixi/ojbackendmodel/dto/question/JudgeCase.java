package com.shiyixi.ojbackendmodel.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目用例
 */
@Data
public class JudgeCase implements Serializable {
    private String input;
    private String output;

    private static final long serialVersionUID = 1L;
}
