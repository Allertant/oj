package com.shiyixi.oj.judge.codesandbox.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目用例
 */
@Data
public class JudgeInfo implements Serializable {
    /**
     * 内存消耗 kB
     */
    private Long memory;
    /**
     * 校验信息 kB
     */
    private String message;
    /**
     * 时间消耗 ms
     */
    private Long time;
    private static final long serialVersionUID = 1L;
}
