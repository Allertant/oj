package com.shiyixi.ojsanbox;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.shiyixi.ojsanbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 加扩展点：1-2个扩展点
 * 上线
 * <p>
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
@Component
@Slf4j
public class JavaDockerCodeSandbox extends JavaCodeSandboxTemplate {
    private static final long TIME_OUT = 3 * 1000L;

    private final DockerClient dockerClient;
    private static final String IMAGE = "openjdk:8-alpine";

    // 初始化工作
    {
        dockerClient = DockerClientBuilder.getInstance().build();
        // 拉取镜像
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(IMAGE);
        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                log.info("下载状态： {}", item.getStatus());
                super.onNext(item);
            }
        };
        try {
            pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
        } catch (InterruptedException e) {
            log.info("拉取镜像异常");
            throw new RuntimeException(e.getMessage());
        }
        log.info("下载完成");
    }

    @Override
    public List<ExecuteMessage> runCode(File userCodeFIle, List<String> inputList) {
        String userCodeParentPath = userCodeFIle.getParentFile().getAbsolutePath();

        // 3.2 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(IMAGE);
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
        log.info("创建容器成功，容器id：{}", containerId);

        // 3.3. 启动容器
        StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(containerId);
        startContainerCmd.exec();
        log.info("启动容器成功，容器id：{}", containerId);

        // 3.4 执行命令并获取结果 docker exec peaceful_lewin java -cp /app Main 1 4
        ArrayList<ExecuteMessage> executeMessageList = new ArrayList<>();
        // 根据输入列表展示数据
        for (String inputArgs : inputList) {
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" "); // 参数分开传递，否则可能会被当作一个参数
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            // 执行命令
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();
            log.info("创建了执行命令：{}", execCreateCmdResponse);

            // 执行信息对象
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setExitValue(0); // 默认为执行成功
            final String[] message = {""}; // 正常结果
            final String[] errorMessage = {""}; // 错误结果
            long time = 0L;

            /*
             * 检测是否超时
             */
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
                        executeMessage.setExitValue(1); // 执行出错了
                        errorMessage[0] += string;
                        log.info("输出错误结果：{}", string);
                    } else {
                        message[0] += string;
                        log.info("输出结果：{}", string);
                    }
                    super.onNext(frame);
                }
            };

            /*
             * 空间耗费
             */
            final long[] maxMemory = {0L};
            // 获取执行内存最大消耗 —— 执行查询状态命令
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onNext(Statistics statistics) {
                    Long memoryUsage = statistics.getMemoryStats().getUsage();
                    if (memoryUsage != null) {
                        maxMemory[0] = Math.max(memoryUsage, maxMemory[0]);
                        log.info("内存占用：{}", memoryUsage);
                    }
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
                stopWatch.start(); // 开始计时
                dockerClient
                        .execStartCmd(execCreateCmdResponseId) // 指定执行命令 id
                        .exec(execStartResultCallback) // 指定执行回调
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS); // 超过五秒后往下走
                stopWatch.stop(); // 结束计时
                time = stopWatch.getLastTaskTimeMillis();
            } catch (Exception e) {
                /*
                 * 包括打断异常、执行异常在内的所有异常信息
                 * 出现了异常，则直接停止运行
                 */
                log.info("程序执行异常，停止之后的案例运行");
                executeMessage.setExitValue(1);
                errorMessage[0] = e.getMessage();
            }

            // 默认为成功
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessage.setMessage(message[0]);

            // 超时异常
            if (timeout[0]) {
                log.info("超时了");
                executeMessage.setExitValue(1);
                executeMessage.setErrorMessage("超时了");
            } else if (executeMessage.getExitValue() != 0) {
                // 其他异常
                executeMessage.setErrorMessage(errorMessage[0]);
            }

            executeMessageList.add(executeMessage);

            // 如果本次执行出现了异常，则停止运行
            if (executeMessage.getExitValue() != 0) {
                break;
            }
        }

        // 6. 删除容器
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();

        return executeMessageList;
    }

//    public static void main(String[] args) throws InterruptedException {
//        JavaDockerCodeSandbox javaNativeCodeSandbox = new JavaDockerCodeSandbox();
//        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
//        // 读取 resource 目录下的文件
//        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/" + GLOBAL_JAVA_CLASS_NAME + GLOBAL_FILE_EXTRA, StandardCharsets.UTF_8);
//        executeCodeRequest.setCode(code);
//        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3", "2 4"));
//        executeCodeRequest.setLanguage("java");
//
//        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
//    }


}
