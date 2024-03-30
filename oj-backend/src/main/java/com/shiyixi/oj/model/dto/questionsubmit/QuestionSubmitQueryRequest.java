package com.shiyixi.oj.model.dto.questionsubmit;

import com.shiyixi.oj.common.PageRequest;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子点赞请求
 *
 * @author shiyixi

 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest  extends PageRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 题目状态
     */
    private Integer status;
    /**
     * 题目 id
     */
    private Long questionId;
    /**
     * 创建用户 id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;
}