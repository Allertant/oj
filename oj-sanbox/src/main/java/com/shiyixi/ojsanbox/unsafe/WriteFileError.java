package com.shiyixi.ojsanbox.unsafe;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * 向服务器写文件（插入危险木马）
 */
public class WriteFileError {
    public static void main(String[] args) throws IOException {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src/main/resources/木马程序.bat";
        String errProgram = "java -version 2>&1";
        Files.write(Paths.get(filePath), Arrays.asList(errProgram));
        System.out.println("写木马成功，你完了哈哈哈");
    }
}
