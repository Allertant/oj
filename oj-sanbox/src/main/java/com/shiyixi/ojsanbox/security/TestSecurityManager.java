package com.shiyixi.ojsanbox.security;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestSecurityManager {
    public static void main(String[] args) {
        System.setSecurityManager(new MySecurityManager());

//        List<String> strings = FileUtil.readLines("D:\\code\\003_oj\\oj-sanbox\\src\\main\\resources\\application.yml", StandardCharsets.UTF_8);
        File file = FileUtil.writeString("aa", "aaa", Charset.defaultCharset());
        System.out.println(file);
    }
}
