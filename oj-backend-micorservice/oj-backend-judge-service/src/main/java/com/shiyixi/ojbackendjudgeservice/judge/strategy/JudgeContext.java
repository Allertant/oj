package com.shiyixi.ojbackendjudgeservice.judge.strategy;


import com.shiyixi.ojbackendmodel.codesandbox.JudgeInfo;
import com.shiyixi.ojbackendmodel.dto.question.JudgeCase;
import com.shiyixi.ojbackendmodel.entity.QuestionSubmit;
import com.shiyixi.ojbackendmodel.entity.Question;
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
    private Integer status;
}
