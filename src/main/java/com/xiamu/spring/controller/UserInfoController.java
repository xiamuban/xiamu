package com.xiamu.spring.controller;

import com.xiamu.spring.annotation.AccessLimit;
import com.xiamu.spring.dao.UserInfo;
import com.xiamu.spring.enumeration.LimitKeyTypeEnum;
import com.xiamu.spring.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.xiamu.spring.annotation.AccessLimit.CacheType.EXCLUSIVE_TYPE;
import static com.xiamu.spring.annotation.AccessLimit.CacheType.SECONDS_TYPE;

@RestController
@RequestMapping("/user")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping(value = "/getUserInfo")
    @AccessLimit(name = "getUserInfo", cacheType = SECONDS_TYPE, limit = 5, limitKeyType = LimitKeyTypeEnum.IPADDR, tipMsg = "访问过于频繁，请稍后再试！")
    public String getUserInfo(@RequestParam(value = "userName") String userName) {
        UserInfo userInfo = userInfoService.getUserInfoByUserName(userName);
        if (Optional.ofNullable(userInfo).isPresent()) {
            return userInfo.toString();
        } else {
            return "用户不存在！";
        }
    }

    @GetMapping(value = "/deleteUser")
    @AccessLimit(name = "deleteUser", cacheType = EXCLUSIVE_TYPE, limit = 1, tipMsg = "当前接口资源正在被调用中,或本次请求不在有效访问期,有效请求时间16:30~19:00", startTime = "16:30", endTime = "19:00")
    public String deleteUser(@RequestParam(value = "userName") String userName) {
        int res = userInfoService.deleteUserByUserName(userName);
        return String.valueOf(res);
    }
}
