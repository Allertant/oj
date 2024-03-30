package com.shiyixi.ojsanbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码沙箱使用请求接口
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {
    /**
     * 题目代码
     */
    private String code;
    /**
     * 输入用例
     */
    private List<String> inputList;
    /**
     * 编程语言
     */
    private String language;
}
