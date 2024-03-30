package com.shiyixi.oj.model.dto.questionsubmit;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子点赞请求
 *
 * @author shiyixi

 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;
    /**
     * 题目 id
     */
    private Long questionId;


    private static final long serialVersionUID = 1L;
}