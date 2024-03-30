package com.shiyixi.ojsanbox;

import cn.hutool.core.io.FileUtil;
import com.shiyixi.ojsanbox.common.BusinessException;
import com.shiyixi.ojsanbox.model.*;
import com.shiyixi.ojsanbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * java 代码沙箱模板
 * - 抽象具体流程
 */
@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandbox {

    /**
     * 用户代码暂存目录
     */
    private static final String GLOBAL_CODE_DIR = "tempCode";

    /**
     * 用户代码文件名称
     */
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main";

    /**
     * 用户代码文件后缀名
     */
    private static final String GLOBAL_FILE_EXTRA = ".java";

    /**
     * 执行器执行的最长时间，这里包括 Java 源文件的编译和执行
     */
    private static final long TIME_OUT = 5 * 1000L;

    /**
     * 代码执行的最大堆内存限制
     */
    private static final String MEMORY_LIMIT = "256m";


    /**
     * 保存代码到文件
     *
     * @param code 用户代码
     * @return 编译后的代码文件
     */
    public File saveCodeToFile(String code) {
        if (StringUtils.isAnyBlank(code)) {
            throw new BusinessException(SandboxResponse.PARAM_ERROR, "代码为空");
        }

        // 将用户的代码保存为文件
        String userDir = System.getProperty("user.dir"); // 用户目录
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR; // 代码总目录
        // 不存在目录则创建目录
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 分级把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME + GLOBAL_FILE_EXTRA;
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }

    /**
     * 编译代码
     *
     * @param userCodeFile 用户代码文件
     */
    public void compileFile(File userCodeFile) throws Exception {
        // 2. 编译代码，得到 class 文件
        String compileCmd = String.format("javac -encoding utf-8 %s",
                userCodeFile.getAbsolutePath());
        ExecuteMessage executeMessage = ProcessUtils.runProcess(compileCmd, TIME_OUT, "编译", "");
        Integer exitValue = executeMessage.getExitValue();
        if (exitValue != 0) {
            /* 编译失败
             *  - 抛出异常
             */
            throw new RuntimeException(executeMessage.getErrorMessage());
        }
    }

    /**
     * 运行代码
     *
     * @param userCodeFile 用户代码文件
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runCode(File userCodeFile, List<String> inputList) {
        // 用户代码父级目录
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        // 执行代码的输出结果组
        List<ExecuteMessage> executeMessageList = new ArrayList<>();

        // 针对每一个示例执行一次代码
        for (String inputArgs : inputList) {
            // 设置了堆内存限制，但无法绝对限制
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s %s",
                    userCodeParentPath, // 用户代码父级目录
                    GLOBAL_JAVA_CLASS_NAME); // class 文件的名称

            // 执行器返回结果对象
            ExecuteMessage executeMessage = null;
            try {
                executeMessage = ProcessUtils.runProcess(runCmd, TIME_OUT, "运行", inputArgs);

                if (executeMessage.getExitValue() != 0) {
                    /*
                     * 如果执行有问题，则停止下面的案例运行
                     * 并且放弃其输出结果
                     */
                    throw new RuntimeException(executeMessage.getErrorMessage());
                }
            } catch (Exception e) {
                /*
                 *  执行器执行出现了运行时异常，直接捕获并处理掉，不向外部抛出
                 *  并且该运行案例也不能丢弃，也要加入到要返回的执行数组中
                 *  并且，所有在执行代码的过程中出现的错误，都归结于执行代码异常，具体出错信息放入到 errorMessage 中进行记录
                 */
                // 针对该次示例数据的执行结果
                executeMessage = new ExecuteMessage();
                executeMessage.setExitValue(1);
                executeMessage.setErrorMessage(e.getMessage());

                executeMessageList.add(executeMessage);
                return executeMessageList;
            }

            executeMessageList.add(executeMessage);
        }

        return executeMessageList;
    }

    /**
     * 获取代码执行结果
     * 这里有两种情况，一是运行代码全部通过了，即所有的 executeMessage.exitValue = 0
     * 另一种情况是，有个别案例出错了，其退出码为 1，但是前面的正确输出内容也需要返回啊
     *
     * @param executeMessageList 执行信息数组
     * @return 执行代码响应对象
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        // 执行代码的响应
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        // 默认的响应状态
        int status = SandboxResponse.SUCCESS.getStatus();
        String value = SandboxResponse.SUCCESS.getValue();

        // 设置输出数组
        ArrayList<String> outputList = new ArrayList<>();

        /*
         * 使用所有运行案例中的最大时间作为 JudgeInfo 中的运行时间
         */
        long maxTime = 0L;
        long maxMemory = 0L;
        for (ExecuteMessage executeMessage : executeMessageList) {

            Long time = executeMessage.getTime();
            Long memory = executeMessage.getMemory();
            Integer exitValue = executeMessage.getExitValue();
            String errorMessage = executeMessage.getErrorMessage();
            String message = executeMessage.getMessage();

            if(exitValue == 0) {
                maxTime = maxTime > time ? maxTime : time;
                maxMemory = maxMemory > memory ? maxMemory : memory;

                outputList.add(message);
            }else {
                // 设置 message 信息（错误信息），如果全部正确，则为空
                executeCodeResponse.setMessage(errorMessage);

                value = SandboxResponse.RUN_CODE_ERROR.getValue();
                status = SandboxResponse.RUN_CODE_ERROR.getStatus();

                break;
            }
        }
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemory(maxMemory);
        judgeInfo.setTime(maxTime);
        judgeInfo.setMessage(value); // 具体执行状态

        executeCodeResponse.setStatus(status);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        executeCodeResponse.setOutputList(outputList);

        return executeCodeResponse;
    }

    /**
     * 删除文件
     *
     * @param userCodeFile
     * @return
     */
    public boolean deleteFie(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            return FileUtil.del(userCodeParentPath);
        }
        return true;
    }

    /**
     * 执行并获取结果
     *
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String code = executeCodeRequest.getCode();
        List<String> inputList = executeCodeRequest.getInputList();

        File userCodeFile = null;
        ExecuteCodeResponse outputResponse;

        try {

            // 1. 把用户的代码保存为文件
            try {
                userCodeFile = saveCodeToFile(code);
            } catch (Exception e) {
                /*
                 * 保存文件出现问题，归结为系统异常，避免暴露于外部
                 */
                throw new BusinessException(SandboxResponse.SYSTEM_ERROR, e.getMessage());
            }

            // 2. 编译代码，得到 class 文件
            try {
                compileFile(userCodeFile);
            } catch (Exception e) {
                throw new BusinessException(SandboxResponse.COMPILE_ERROR, e.getMessage());
            }

            // 3. 执行代码，得到输出结果
            List<ExecuteMessage> executeMessageList = runCode(userCodeFile, inputList);

            // 4. 收集整理输出结果
            outputResponse = getOutputResponse(executeMessageList);

        } finally {
            // 5. 文件清理，前面任何报错，都要清理文件
            if (userCodeFile != null) {
                boolean b = deleteFie(userCodeFile);
                if (!b) {
                    // todo: 删除文件错误，具体还需要做其他事情来解决
                    log.error("deleteFile error, userCodeFilePath = {}", userCodeFile);
                }
            }
        }

        // 正常则返回响应信息
        return outputResponse;
    }
}
