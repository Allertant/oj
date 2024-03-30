package com.shiyixi.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyixi.oj.annotation.AuthCheck;
import com.shiyixi.oj.common.BaseResponse;
import com.shiyixi.oj.common.ErrorCode;
import com.shiyixi.oj.common.ResultUtils;
import com.shiyixi.oj.constant.UserConstant;
import com.shiyixi.oj.exception.BusinessException;
import com.shiyixi.oj.judge.JudgeService;
import com.shiyixi.oj.model.dto.question.QuestionQueryRequest;
import com.shiyixi.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.shiyixi.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.shiyixi.oj.model.entity.Question;
import com.shiyixi.oj.model.entity.QuestionSubmit;
import com.shiyixi.oj.model.entity.User;
import com.shiyixi.oj.model.vo.QuestionSubmitVO;
import com.shiyixi.oj.service.QuestionSubmitService;
import com.shiyixi.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Request;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞接口
 *
 * @author shiyixi
 */
@RestController
@RequestMapping("/quest_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;
    @Resource
    private JudgeService judgeService;

    /**
     * 点赞 / 取消点赞
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        Long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        // 执行判题服务
        judgeService.doJudge(questionSubmitId);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param questionSubmitQueryRequest
     * @return BaseResponse<Page < QuestionSubmitVO>>
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                   HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        Page<QuestionSubmit> questionPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionPage, loginUser));
    }
}
