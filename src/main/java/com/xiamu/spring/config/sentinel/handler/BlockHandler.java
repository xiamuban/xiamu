package com.xiamu.spring.config.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @Author xianghui.luo
 * @Date 2025/2/10 14:08
 * @Description sentinel限流降级处理类
 * 方法签名和返回类型需要与限流方法一致且必须加上BlockException参数
 * 所有的方法均为static静态方法，参数必须带上BlockException exception，否则找不到相应方法
 */
public class BlockHandler {
    public static String blockHandlerDefault(String userName, BlockException exception){
        //return "这是第一个限流降级方法";
        return "访问过于频繁，请稍后再试！";
    }
}
