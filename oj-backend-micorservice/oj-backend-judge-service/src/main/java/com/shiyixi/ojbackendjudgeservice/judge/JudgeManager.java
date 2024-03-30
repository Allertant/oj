package com.shiyixi.ojbackendjudgeservice.judge;


import com.shiyixi.ojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.shiyixi.ojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.shiyixi.ojbackendjudgeservice.judge.strategy.JudgeContext;
import com.shiyixi.ojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.shiyixi.ojbackendmodel.codesandbox.JudgeInfo;
import com.shiyixi.ojbackendmodel.entity.QuestionSubmit;
import com.shiyixi.ojbackendmodel.enums.QuestionSubmitLanguageEnum;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy(); // 非 java
        if (language.equals(QuestionSubmitLanguageEnum.JAVA.getValue())) {  // java
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
