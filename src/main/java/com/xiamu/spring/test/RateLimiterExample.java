package com.xiamu.spring.test;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

public class RateLimiterExample {
    private static RateLimiter rateLimiter = RateLimiter.create(5.0); // 每秒生成 5 个许可

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            long startTime = System.currentTimeMillis();
            //rateLimiter.acquire();
            boolean acquired = rateLimiter.tryAcquire(200, TimeUnit.MILLISECONDS);
            long endTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + " acquired permit: " + acquired + " (attempted at " + startTime + ", finished at " + endTime + ")");
            //System.out.println(Thread.currentThread().getName() + " acquired permit (attempted at " + startTime + ", finished at " + endTime + ")");
        };

        // 创建多个线程同时尝试获取许可
        Thread thread1 = new Thread(task, "Thread-1");
        Thread thread2 = new Thread(task, "Thread-2");
        Thread thread3 = new Thread(task, "Thread-3");
        Thread thread4 = new Thread(task, "Thread-4");
        Thread thread5 = new Thread(task, "Thread-5");
        Thread thread6 = new Thread(task, "Thread-6");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
    }
}
