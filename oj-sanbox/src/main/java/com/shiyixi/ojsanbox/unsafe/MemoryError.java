package com.shiyixi.ojsanbox.unsafe;

import cn.hutool.core.collection.AvgPartition;

import java.util.ArrayList;
import java.util.List;

/**
 * 无限睡眠
 * 执行阻塞，占用资源不释放
 */
public class MemoryError {
    public static void main(String[] args) {
        List<byte[]> bytes = new ArrayList<>();
        while(true) {
            bytes.add(new byte[1000]);
        }
    }
}
