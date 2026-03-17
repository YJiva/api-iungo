package com.apiiungo; // 包名必须和目录结构一致

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// 扫描 MyBatis 的 Mapper 包
@MapperScan("com.apiiungo.mapper")
@SpringBootApplication
// 类名 = 文件名 = ApiiungoApplication（全小写/大写严格一致）
public class ApiiungoApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApiiungoApplication.class, args);
    }
}
