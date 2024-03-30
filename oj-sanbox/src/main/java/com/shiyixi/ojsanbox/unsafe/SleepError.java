package com.shiyixi.ojsanbox.unsafe;

/**
 * 无限睡眠
 * 执行阻塞，占用资源不释放
 */
public class SleepError {
    public static void main(String[] args) {
        long ONE_HOUR = 5 * 1000L;
        try {
            System.out.println("进入睡眠");
            Thread.sleep(ONE_HOUR);
            System.out.println("睡完了");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
