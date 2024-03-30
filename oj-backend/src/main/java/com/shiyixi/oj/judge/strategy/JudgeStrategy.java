package com.shiyixi.oj.judge.strategy;

import com.shiyixi.oj.judge.codesandbox.model.JudgeInfo;

public interface JudgeStrategy {
    JudgeInfo doJudge(JudgeContext judgeContext);
}
