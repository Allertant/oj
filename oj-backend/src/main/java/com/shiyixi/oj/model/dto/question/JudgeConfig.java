package com.shiyixi.oj.model.dto.question;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 题目用例
 */
@Data
public class JudgeConfig implements Serializable {
    /**
     * 内存限制 kB
     */
    private Long memoryLimit;
    /**
     * 堆栈限制 kB
     */
    private Long stackLimit;
    /**
     * 时间限制 ms
     */
    private Long timeLimit;

    private static final long serialVersionUID = 1L;
}
