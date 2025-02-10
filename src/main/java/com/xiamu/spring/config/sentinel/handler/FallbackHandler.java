package com.xiamu.spring.config.sentinel.handler;

/**
 * @Author xianghui.luo
 * @Date 2025/2/10 14:09
 * @Description sentinel熔断降级处理类
 * 方法签名和返回类型需要与限流方法一致
 */
public class FallbackHandler {
    public static String fallbackDefault(String userName){
        //return "这是第一个熔断降级方法";
        return "访问异常，请稍后再试！";
    }
}
