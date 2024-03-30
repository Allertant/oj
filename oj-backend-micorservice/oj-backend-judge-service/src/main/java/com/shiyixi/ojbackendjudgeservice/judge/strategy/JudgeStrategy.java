package com.shiyixi.ojbackendjudgeservice.judge.strategy;


import com.shiyixi.ojbackendmodel.codesandbox.JudgeInfo;

public interface JudgeStrategy {
    JudgeInfo doJudge(JudgeContext judgeContext);
}
