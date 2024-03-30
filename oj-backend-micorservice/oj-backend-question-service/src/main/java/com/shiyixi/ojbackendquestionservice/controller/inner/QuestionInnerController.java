package com.shiyixi.ojbackendquestionservice.controller.inner;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.shiyixi.ojbackendmodel.entity.Question;
import com.shiyixi.ojbackendmodel.entity.QuestionSubmit;
import com.shiyixi.ojbackendquestionservice.service.QuestionService;
import com.shiyixi.ojbackendquestionservice.service.QuestionSubmitService;
import com.shiyixi.ojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 该服务仅内部调用
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    /**
     * 根据题目 id 获取题目
     *
     * @param questionId 题目 id
     * @return 题目
     */
    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    /**
     * 根据题目提交 id 获取题目提交信息
     *
     * @param questionSubmitId 题目提交 id
     * @return 题目提交信息
     */
    @Override
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    /**
     * 根据题目提交 id 更新题目提交
     *
     * @param questionSubmit 题目提交 id
     * @return boolean
     */
    @Override
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

    /**
     * 根据题目提交 id 更新题目成功数目
     * @param questionId 题目 id
     */
    @Override
    @PostMapping("/question/addacceptednum")
    public void updateQuestionAcceptedNumById(@RequestParam("questionId") Long questionId) {
        questionService.addAcceptedNum(questionId);
    }
}
