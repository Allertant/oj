package com.shiyixi.ojsanbox.old;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.WordTree;
import com.shiyixi.ojsanbox.CodeSandbox;
import com.shiyixi.ojsanbox.model.ExecuteCodeRequest;
import com.shiyixi.ojsanbox.model.ExecuteCodeResponse;
import com.shiyixi.ojsanbox.model.ExecuteMessage;
import com.shiyixi.ojsanbox.model.JudgeInfo;
import com.shiyixi.ojsanbox.utils.ProcessUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 用程序代替人工操纵 javac java 命令执行代码
 * Process 进程执行管理类
 * 1. 把用户的代码保存为文件
 * 2. 编译代码得到 class 文件
 * java 执行程序
 * java 获取执行信息
 * 3. 执行代码，得到输出结果
 * 4. 收集整理输出结果
 * 5. 文件清理
 * 6. 错误处理，提升程序鲁棒性
 * <p>
 * 如何提高程序的安全性
 * 1. 用户提交恶意代码
 * - 占用时间 Thread.sleep() —— 超时控制 —— 单独开一个线程控制
 * - 占用空间 —— 限制内存资源分配 —— 简单地添加 jvm 参数
 * - 读取文件
 * - 写文件
 * - 执行命令 （高危命令）
 * || 代码扫描、限制用户的操作权限、环境隔离
 * <p>
 * 2. 黑白名单方法
 * - 无法遍历所有的黑名单
 * - 性能消耗
 * - 不同的编程语言对应的领域关键词都不一样，搜集成本很大
 * <p>
 * 3. java 安全管理器
 * - 限制用户对文件、内存、cpu、网络等资源的访问，实现更严格的限制
 * - security menager，java 提供的保护 jvm java 程序的安全机制，实现更严格的资源和权限操作限制
 */
@Deprecated
public class JavaNativeCodeSandboxOld implements CodeSandbox {
    private static final String GLOBAL_CODE_DIR = "tempCode";
    private static final String GLOBAL_JAVA_CLASS_NAME = "WriteFileError";
    private static final String GLOBAL_FILE_EXTRA = ".java";
    private static final long TIME_OUT = 3 * 1000L;
    private static final List<String> blackList = Arrays.asList("Files", "exec");
    private static final WordTree WORD_TREE;
    private static final String SECURITY_MANAGER_PATH = "D:\\code\\003_oj\\oj-sanbox\\src\\main\\resources\\security";
    private static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager";

    // 加载资源文件
    static {
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackList);
    }

    public static void main(String[] args) {
        JavaNativeCodeSandboxOld javaNativeCodeSandbox = new JavaNativeCodeSandboxOld();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        // 读取 resource 目录下的文件
        String code = ResourceUtil.readStr("testCode/unsafeCode/" + GLOBAL_JAVA_CLASS_NAME + GLOBAL_FILE_EXTRA, StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3", "234234234234234 234234234234234234234"));
        executeCodeRequest.setLanguage("java");

        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String code = executeCodeRequest.getCode();
        List<String> inputList = executeCodeRequest.getInputList();
        String language = executeCodeRequest.getLanguage();

        // 0. 校验代码，是否含有非法字符
//        FoundWord foundWord = WORD_TREE.matchWord(code);
//        if (foundWord != null && StrUtil.isNotBlank(foundWord.getFoundWord())) {
//            // 包含敏感词
//            System.out.println(foundWord.getFoundWord());
//            return null;
//        }


        // 1. 将用户的代码保存为文件
        String userDir = System.getProperty("user.dir"); // 用户目录
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR; // 全局代码文件夹
        if (!FileUtil.exist(globalCodePathName)) { // 判断全局代码目录是否存在
            FileUtil.mkdir(globalCodePathName);
        }
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID(); // 分级把用户的代码隔离存放
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME + GLOBAL_FILE_EXTRA;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8); // 保存到目标文件夹下

        // 2. 编译代码，得到 class 文件
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            ExecuteMessage executeMessage = ProcessUtils.runProcess(compileCmd, TIME_OUT, "编译", "");
            // 如果编译错误，则直接结束运行
            if (StrUtil.isNotBlank(executeMessage.getErrorMessage())) {
                throw new RuntimeException(executeMessage.getErrorMessage());
            }
        } catch (Exception e) {
            return getResponse(e);
        }

        // 3. 执行代码，得到输出结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            // 设置了堆内存限制，但无法绝对限制
            // 在运行java 程序时，指定安全管理器类；用分号指定多个类路径
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s " + GLOBAL_JAVA_CLASS_NAME, userCodeParentPath);
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s " + GLOBAL_JAVA_CLASS_NAME , userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            try {
                ExecuteMessage executeMessage = ProcessUtils.runProcess(runCmd, TIME_OUT, "运行", inputArgs);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                return getResponse(e);
            }
        }

        // 4. 收集整理输出结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setStatus(1); // 执行成功
        ArrayList<String> outputList = new ArrayList<>(); // 成功的情况
        long maxTime = 0L; // 使用最大值作为判题服务的统计时间
        for (ExecuteMessage executeMessage : executeMessageList) {
            maxTime = maxTime > executeMessage.getTime() ? maxTime : executeMessage.getTime();
            String errorMessage = executeMessage.getErrorMessage();
            // 针对某个输入的执行出错，直接停止运行
            if (StrUtil.isNotBlank(errorMessage)) {
                executeCodeResponse.setMessage(errorMessage);
                // 用户代码执行错误
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
        }
        JudgeInfo judgeInfo = new JudgeInfo();
//        judgeInfo.setMemory();
        judgeInfo.setTime(maxTime);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        executeCodeResponse.setOutputList(outputList);

        // 5. 文件清理
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
        }

        return executeCodeResponse;
    }

    /**
     * 针对代码沙箱处理运行错误的响应
     *
     * @param e
     * @return
     */
    private ExecuteCodeResponse getResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        // 表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        return executeCodeResponse;
    }

}
