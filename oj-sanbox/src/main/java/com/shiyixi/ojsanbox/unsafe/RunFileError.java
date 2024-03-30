package com.shiyixi.ojsanbox.unsafe;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 运行其他程序（比如危险木马）
 */
public class RunFileError {
    public static void main(String[] args) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src/main/resources/木马程序.bat";
        Process process = Runtime.getRuntime().exec(filePath);
        process.waitFor();
        String compileLine = "";
        StringBuilder compileOutputStringBuilder = new StringBuilder();
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while ((compileLine = bufferedReader.readLine()) != null) {
            compileOutputStringBuilder.append(compileLine).append("\n");
        }
        System.out.println("木马执行成功，结果为：" + compileOutputStringBuilder);
    }
}
