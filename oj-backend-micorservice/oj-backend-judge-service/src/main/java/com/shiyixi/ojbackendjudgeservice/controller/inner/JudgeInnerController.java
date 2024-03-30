package com.shiyixi.ojbackendjudgeservice.controller.inner;

import com.shiyixi.ojbackendjudgeservice.judge.JudgeService;
import com.shiyixi.ojbackendmodel.entity.QuestionSubmit;
import com.shiyixi.ojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/inner")
@RestController
public class JudgeInnerController implements JudgeFeignClient {
    @Resource
    private JudgeService judgeService;

    /**
     * 判题
     * @param questionSubmitId 题目提交 id
     * @return 题目提交
     */
    @Override
    @PostMapping("/do")
    public QuestionSubmit doJudge(Long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }
}
