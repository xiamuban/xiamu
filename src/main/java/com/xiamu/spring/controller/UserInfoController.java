package com.xiamu.spring.controller;

import com.xiamu.spring.dao.UserInfo;
import com.xiamu.spring.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping(value = "/getUserInfo")
    public String getUserInfo(@RequestParam(value = "userName") String userName) {
        UserInfo userInfo = userInfoService.getUserInfoByUserName(userName);
        if(Optional.ofNullable(userInfo).isPresent()){
            return userInfo.toString();
        }else{
            return "用户不存在！";
        }
    }
}
