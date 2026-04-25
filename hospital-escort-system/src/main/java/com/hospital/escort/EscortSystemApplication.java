package com.hospital.escort;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 社区安心陪诊小程序 - 后端系统启动类
 * @author System
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.hospital.escort.mapper")
public class EscortSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EscortSystemApplication.class, args);
        System.out.println("===================================");
        System.out.println("社区安心陪诊小程序后端系统启动成功！");
        System.out.println("===================================");
    }
}