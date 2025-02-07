package com.xiamu.spring.config;

import com.xiamu.spring.Interceptor.FullTableOperationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author xianghui.luo
 * @Date 2025/2/7 14:06
 * @Description Mybatis sql拦截配置类
 */
@Configuration
public class MybatisConfig {

    @Bean
    public FullTableOperationInterceptor fullTableOperationInterceptor() {
        return new FullTableOperationInterceptor();
    }
}