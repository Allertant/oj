package com.shiyixi.oj.model.dto.question;

import com.baomidou.mybatisplus.annotation.TableField;
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
