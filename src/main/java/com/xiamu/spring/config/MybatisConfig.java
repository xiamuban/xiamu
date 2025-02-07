package com.xiamu.spring.config;

import com.xiamu.spring.Interceptor.FullTableOperationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

    @Bean
    public FullTableOperationInterceptor fullTableOperationInterceptor() {
        return new FullTableOperationInterceptor();
    }
}