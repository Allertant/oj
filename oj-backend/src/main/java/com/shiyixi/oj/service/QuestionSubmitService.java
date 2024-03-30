package com.shiyixi.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shiyixi.oj.model.dto.question.QuestionQueryRequest;
import com.shiyixi.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.shiyixi.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.shiyixi.oj.model.entity.Question;
import com.shiyixi.oj.model.entity.QuestionSubmit;
import com.shiyixi.oj.model.entity.User;
import com.shiyixi.oj.model.vo.QuestionSubmitVO;
import com.shiyixi.oj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author shiyixi
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);
    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param request
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param request
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);
}
