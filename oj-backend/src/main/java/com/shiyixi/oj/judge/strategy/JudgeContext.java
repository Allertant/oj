package com.shiyixi.oj.judge.strategy;

import com.shiyixi.oj.model.dto.question.JudgeCase;
import com.shiyixi.oj.judge.codesandbox.model.JudgeInfo;
import com.shiyixi.oj.model.entity.Question;
import com.shiyixi.oj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 用于定义用户在上下文中传递的参数
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;
    private List<String> inputList;
    private List<String> outputList;
    private Question question;
    private List<JudgeCase> judgeCaseList;
    private QuestionSubmit questionSubmit;
}
