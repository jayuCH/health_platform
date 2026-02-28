package com.healthydiet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 健康饮食管理系统启动类
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
@MapperScan("com.healthydiet.mapper")
public class HealthyDietApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthyDietApplication.class, args);
    }
}
