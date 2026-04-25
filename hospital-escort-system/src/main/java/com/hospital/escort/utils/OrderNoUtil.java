package com.hospital.escort.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订单号生成工具类
 */
public class OrderNoUtil {

    private static final AtomicInteger SEQUENCE = new AtomicInteger(1000);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成订单号
     * 格式：ES + yyyyMMddHHmmss + 4位序列号
     */
    public static String generate() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        int seq = SEQUENCE.getAndIncrement();
        if (seq > 9999) {
            SEQUENCE.set(1000);
            seq = SEQUENCE.getAndIncrement();
        }
        return "ES" + timestamp + seq;
    }
}