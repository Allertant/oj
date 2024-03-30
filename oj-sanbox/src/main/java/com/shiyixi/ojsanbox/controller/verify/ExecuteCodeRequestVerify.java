package com.shiyixi.ojsanbox.controller.verify;

import com.shiyixi.ojsanbox.common.BusinessException;
import com.shiyixi.ojsanbox.model.ExecuteCodeRequest;
import com.shiyixi.ojsanbox.model.SandboxResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ExecuteCodeRequestVerify {
    public static void verify(ExecuteCodeRequest executeCodeRequest) {
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        List<String> inputList = executeCodeRequest.getInputList();
        if (StringUtils.isAnyBlank(code, language) || inputList == null ) {
            throw new BusinessException(SandboxResponse.PARAM_ERROR);
        }
    }
}
