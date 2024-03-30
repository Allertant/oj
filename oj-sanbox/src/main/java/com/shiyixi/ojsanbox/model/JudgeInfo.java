package com.shiyixi.ojsanbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 题目用例
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeInfo implements Serializable {
    /**
     * 内存消耗 kB
     */
    private Long memory;
    /**
     * 执行的大致信息，比如编译错误，运行错误等
     */
    private String message;
    /**
     * 时间消耗 ms
     */
    private Long time;

    private static final long serialVersionUID = 1L;
}
