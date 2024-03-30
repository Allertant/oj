package com.shiyixi.ojbackendjudgeservice.judge;


import com.shiyixi.ojbackendmodel.entity.QuestionSubmit;

public interface JudgeService {
    /**
     * 判题服务
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(Long questionSubmitId);
}
