package com.shiyixi.ojsanbox.old;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.shiyixi.ojsanbox.CodeSandbox;
import com.shiyixi.ojsanbox.model.ExecuteCodeRequest;
import com.shiyixi.ojsanbox.model.ExecuteCodeResponse;
import com.shiyixi.ojsanbox.model.ExecuteMessage;
import com.shiyixi.ojsanbox.model.JudgeInfo;
import com.shiyixi.ojsanbox.utils.ProcessUtils;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *
 * 加扩展点：1-2个扩展点
 * 上线
 *
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
public class JavaDockerCodeSandboxOld implements CodeSandbox {
    private static final String GLOBAL_CODE_DIR = "tempCode";
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main";
    private static final String GLOBAL_FILE_EXTRA = ".java";
    private static final long TIME_OUT = 3 * 1000L;
    private static Boolean FIRST_INIT = false;

    public static void main(String[] args) throws InterruptedException {
        JavaDockerCodeSandboxOld javaNativeCodeSandbox = new JavaDockerCodeSandboxOld();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        // 读取 resource 目录下的文件
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/" + GLOBAL_JAVA_CLASS_NAME + GLOBAL_FILE_EXTRA, StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3", "2 4"));
        executeCodeRequest.setLanguage("java");

        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String code = executeCodeRequest.getCode();
        List<String> inputList = executeCodeRequest.getInputList();
        String language = executeCodeRequest.getLanguage();

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

        // 3. 创建容器，把代码复制到容器内
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        String image = "openjdk:8-alpine";

        // 3.1 拉取镜像
        if (FIRST_INIT) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载状态：" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
            System.out.println("下载完成");
            FIRST_INIT = false;
        }


        // 3.2 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        String seccompConfig = ResourceUtil.readUtf8Str("seccomp.json"); // linux 安全管理配置
        HostConfig hostConfig = new HostConfig();
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app"))); // 虚拟目录映射：本地-docker容器
        hostConfig.withMemory(100 * 1000 * 1000L); // 100M
        hostConfig.withCpuCount(1L); // 核心数1
        hostConfig.withMemorySwap(0L); // 内存交换区为0；组织硬盘写入、网络传输
        hostConfig.withSecurityOpts(Arrays.asList("seccomp=" + seccompConfig));
        CreateContainerResponse createContainerResponse = containerCmd
                .withAttachStdin(true) // 获取容器与终端的连接，进行输入输出
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true) // 后台开了一个守护进程，相当于开了一个 bash，让容器保活一段时间
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true) // 禁用网络
                .withReadonlyRootfs(true) // 限制用户向根目录写文件
                .exec();
        // 容器 id
        String containerId = createContainerResponse.getId();
        System.out.println("创建容器结果：" + createContainerResponse);

        // 3.3. 启动容器
        StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(containerId);
        startContainerCmd.exec();

        // 3.4 执行命令并获取结果 docker exec peaceful_lewin java -cp /app Main 1 4
        ArrayList<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" "); // 参数分开传递，否则可能会被当作一个参数
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId).withCmd(cmdArray).withAttachStdin(true).withAttachStdout(true).withAttachStderr(true).exec();
            System.out.println("创建了执行命令：" + execCreateCmdResponse);
            ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {""}; // 使用引用类型
            final String[] errorMessage = {""};
            long time = 0L;
            // 判断是否超时
            final boolean[] timeout = {true};

            String execCreateCmdResponseId = execCreateCmdResponse.getId();
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    // 如果正常执行完成了，则表示未超时
                    timeout[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    String string = new String(frame.getPayload());
                    if (streamType.STDERR.equals(streamType)) {
                        errorMessage[0] += string;
                        System.out.println("输出错误结果：" + string);
                    } else {
                        message[0] += string;
                        System.out.println("输出结果：" + string);
                    }
                    super.onNext(frame);
                }
            };

            final long[] maxMemory = {0L};

            // 获取执行内存最大消耗
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onNext(Statistics statistics) {
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                    System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() throws IOException {

                }
            });

            // 程序执行
            try {
                stopWatch.start();
                dockerClient
                        .execStartCmd(execCreateCmdResponseId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS); // 超过五秒后往下走
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
            } catch (InterruptedException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }

            // 超时判断
            if (timeout[0]) {
                System.out.println("超时了");
            }

            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessage.setMessage(message[0]);
            executeMessage.setErrorMessage(errorMessage[0]);
            executeMessageList.add(executeMessage);
        }


        System.out.println(Arrays.toString(executeMessageList.toArray()));

        // 6. 删除容器
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();

        // 7. 删除镜像
//        dockerClient.removeImageCmd(image).withForce(true).exec();

        // 4. 收集整理输出结果


        return new ExecuteCodeResponse();
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
