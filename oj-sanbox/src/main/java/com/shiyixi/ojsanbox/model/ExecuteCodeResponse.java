package com.shiyixi.ojsanbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码沙箱响应接口
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecuteCodeResponse {
    /**
     * 执行状态
     */
    private Integer status;
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
    /**
     * 输出列表
     */
    private List<String> outputList;
    /**
     * 如果出错，则为具体出错信息；如果没错，则为空
     */
    private String message;
}
