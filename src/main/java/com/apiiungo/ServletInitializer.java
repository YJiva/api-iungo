package com.apiiungo;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// 这个类是 War 包部署必需的，核心是指定正确的启动类名
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // 关键：这里的类名必须和你的启动类完全一致（ApiiungoApplication）
        return application.sources(ApiiungoApplication.class);
    }

}
