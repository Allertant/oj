package com.shiyixi.ojsanbox.utils;

import com.shiyixi.ojsanbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 获取进行执行后的信息
 */
@Slf4j
public class ProcessUtils {
    /**
     * 执行代码
     * @param compileCmd 命令
     * @param opName 操作名称
     * @param args 命令执行需要再次输入的参数
     * @return ExecuteMessage
     * @throws Exception 异常信息，出现任何问题，都有调用者进行捕获处理
     */
    public static ExecuteMessage runProcess(String compileCmd,Long timeout, String opName, String args) throws Exception {
        // 计时功能
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        /* 由于这里的超时是由另一个线程进行打断的
         * 该处理器被打断后，并不会产生错误信息，因此需要自己额外多进行一次判断
         */
        AtomicBoolean isTimeout = new AtomicBoolean(false);

        // 执行器执行处理
        Process process = Runtime.getRuntime().exec(compileCmd);
        // 返回的对象
        ExecuteMessage executeMessage = new ExecuteMessage();

        /*
         * 设置超时控制器
         * 先睡眠一段时间，看进行是否超时了
         * 如果睡醒仍旧超时，则结束其运行
         */
        new Thread(()->{
            try {

                Thread.sleep(timeout);
                if(process.isAlive()) {
                    log.info("运行超时了。。。");
                    isTimeout.set(true);
                    process.destroy();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        // 是否需要自动传参
        if (!args.isEmpty()) {
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(args + "\n");
            outputStreamWriter.flush(); // 相当于 enter 键

            // 关闭流
            outputStreamWriter.close();
            outputStream.close();
        }

        // 输入流和错误流，便于获取信息
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();

        // 退出码
        int exitValue = process.waitFor();
        // 停止计时
        stopWatch.stop();
        long timeMillis = stopWatch.getLastTaskTimeMillis();


        /*
         * 如果程序是因为超时而被打断的，需要额外处理
         * 直接返回信息
         */
        if(isTimeout.get()) {
            executeMessage.setExitValue(1);
            executeMessage.setErrorMessage("执行超时");
            executeMessage.setTime(timeMillis);
            return executeMessage;
        }

        // 设置退出码
        executeMessage.setExitValue(exitValue);

        // 命令行输出信息
        String compileLine; // temp 字符串，获取一行输出
        BufferedReader bufferedReader;
        StringBuilder compileOutputStringBuilder = new StringBuilder(); // 返回总的信息
        if (exitValue == 0) {
            // 正常退出，获取输出信息——正常信息
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((compileLine = bufferedReader.readLine()) != null) {
                compileOutputStringBuilder.append(compileLine).append("\n");
            }

            // 去掉最后一个换行符
            int length = compileOutputStringBuilder.length();
            if(length >= 1) compileOutputStringBuilder.delete(length-1, length);

            // 打印日志
            log.info(opName + "成功。" + compileOutputStringBuilder);

            // 设置正常信息
            executeMessage.setMessage(compileOutputStringBuilder.toString());
        } else {
            // 异常退出，获取输出信息——异常信息
            bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
            while ((compileLine = bufferedReader.readLine()) != null) {
                compileOutputStringBuilder.append(compileLine).append("\n");
            }
            // 去掉最后一个换行符
            int length = compileOutputStringBuilder.length();
            if(length >= 1) compileOutputStringBuilder.delete(length-1, length);

            // 打印日志
            log.info(opName + "失败。" + compileOutputStringBuilder);

            // 设置异常信息
            executeMessage.setErrorMessage(compileOutputStringBuilder.toString());
        }

        // 流对象关闭
        inputStream.close();
        errorStream.close();

        // 销毁读取器
        bufferedReader.close();

        // 销毁处理器
        process.destroy();



        // 设置运行时间
        executeMessage.setTime(timeMillis);

        // 设置内存消耗
        executeMessage.setMemory(0L);

        // 打印信息
        log.info("executeMessage: {}", executeMessage);

        return executeMessage;
    }
}
