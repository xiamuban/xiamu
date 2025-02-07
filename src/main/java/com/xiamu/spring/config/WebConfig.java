package com.xiamu.spring.config;

import com.xiamu.spring.Interceptor.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author xianghui.luo
 * @Date 2025/2/7 14:06
 * @Description web请求拦截配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**");          // 拦截所有请求
                //.excludePathPatterns("/login", "/static/**"); // 排除特定路径
    }
}