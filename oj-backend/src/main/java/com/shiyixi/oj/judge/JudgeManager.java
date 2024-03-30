package com.shiyixi.oj.judge;

import com.shiyixi.oj.judge.strategy.DefaultJudgeStrategy;
import com.shiyixi.oj.judge.strategy.JavaLanguageJudgeStrategy;
import com.shiyixi.oj.judge.strategy.JudgeContext;
import com.shiyixi.oj.judge.strategy.JudgeStrategy;
import com.shiyixi.oj.judge.codesandbox.model.JudgeInfo;
import com.shiyixi.oj.model.entity.QuestionSubmit;
import com.shiyixi.oj.model.enums.QuestionSubmitLanguageEnum;
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
