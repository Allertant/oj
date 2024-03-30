package com.shiyixi.oj.judge;

import com.shiyixi.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.shiyixi.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.shiyixi.oj.model.entity.QuestionSubmit;
import com.shiyixi.oj.model.vo.QuestionSubmitVO;

public interface JudgeService {
    /**
     * 判题服务
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(Long questionSubmitId);
}
