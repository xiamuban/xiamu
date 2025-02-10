package com.xiamu.spring.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.xiamu.spring.annotation.AccessLimit;
import com.xiamu.spring.config.sentinel.handler.BlockHandler;
import com.xiamu.spring.config.sentinel.handler.FallbackHandler;
import com.xiamu.spring.dao.UserInfo;
import com.xiamu.spring.enumeration.LimitKeyTypeEnum;
import com.xiamu.spring.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.xiamu.spring.annotation.AccessLimit.CacheType.EXCLUSIVE_TYPE;
import static com.xiamu.spring.annotation.AccessLimit.CacheType.SECONDS_TYPE;

@RestController
@RequestMapping("/user")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping(value = "/getUserInfo")
    //@AccessLimit(name = "getUserInfo", cacheType = SECONDS_TYPE, limit = 5, limitKeyType = LimitKeyTypeEnum.IPADDR, tipMsg = "访问过于频繁，请稍后再试！")
    @SentinelResource("getUserInfo")
    public String getUserInfo(@RequestParam(value = "userName") String userName) {
        UserInfo userInfo = userInfoService.getUserInfoByUserName(userName);
        if (Optional.ofNullable(userInfo).isPresent()) {
            return userInfo.toString();
        } else {
            return "用户不存在！";
        }
    }

    @GetMapping(value = "/getUser")
    //@AccessLimit(name = "getUserInfo", cacheType = SECONDS_TYPE, limit = 5, limitKeyType = LimitKeyTypeEnum.IPADDR, tipMsg = "访问过于频繁，请稍后再试！")
    @SentinelResource(value = "getUser", blockHandler = "blockHandlerDefault",blockHandlerClass = BlockHandler.class, fallback = "fallbackDefault", fallbackClass = FallbackHandler.class)
    /**
     * value：指定资源名称，必须配置。
     * blockHandler：(限流降级处理)指定流控降级处理方法的名称，可选。该方法必须和原方法在同一个类中，并且参数类型和返回类型要与原方法一致，最后增加一个 BlockException 类型的参数。 当资源被 Sentinel 流控或降级时，会调用这个方法来处理被限制的请求。
     * blockHandlerClass: 指定限流处理类的 Class 对象。
     * fallback：(熔断降级处理)指定当原方法出现异常时的处理方法，可选。参数类型和返回类型要与原方法一致，可以用于处理业务异常。
     * fallbackClass: 指定熔断处理类的 Class 对象。
     */
    public String getUser(@RequestParam(value = "userName") String userName) {

        /**
         * SphU.entry 和 @SentinelResource 都是用于定义 Sentinel 中的资源，并允许你进行流量控制和熔断。
         * SphU.entry 是编程式的方式，需要你在代码中显式调用，并处理可能的异常。它提供了更细粒度的控制，但可能会增加代码的复杂性。
         * @SentinelResource 是声明式的方式，你只需在方法上添加注解，Sentinel 会自动处理流量控制和熔断。它简化了代码，但可能不如 SphU.entry 灵活
         */

        //try {
        //    SphU.entry("getUserInfo");
        //} catch (Exception e) {
        //    return "访问过于频繁，请稍后再试！";
        //}
        int i = 10 / 0;
        return "用户不存在！";
    }

    /**
     * 方法签名和返回类型需要与限流方法一致且必须加上BlockException参数
     * @param userName
     * @param exception
     * @return
     */
    public String handleBlock(String userName, BlockException exception){
        //return "这是第一个限流降级方法";
        return "访问过于频繁，请稍后再试！";
    }

    public String fallback(String userName, Throwable throwable) {
        //return "这是第一个熔断降级方法";
        return "访问异常，请稍后再试！";
    }


    @GetMapping(value = "/deleteUser")
    @AccessLimit(name = "deleteUser", cacheType = EXCLUSIVE_TYPE, limit = 1, tipMsg = "当前接口资源正在被调用中,或本次请求不在有效访问期,有效请求时间16:30~19:00", startTime = "16:30", endTime = "19:00")
    public String deleteUser(@RequestParam(value = "userName") String userName) {
        int res = userInfoService.deleteUserByUserName(userName);
        return String.valueOf(res);
    }
}
