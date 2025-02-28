package com.shiyixi.oj.judge.codesandbox.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.shiyixi.oj.common.ErrorCode;
import com.shiyixi.oj.exception.BusinessException;
import com.shiyixi.oj.judge.codesandbox.CodeSandbox;
import com.shiyixi.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.shiyixi.oj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;

/**
 * 远程代码沙箱
 */
public class RemoteCodeSandbox implements CodeSandbox {

    // 简单鉴权字符串
    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secretKey";
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://192.168.159.132:8082/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if(StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
