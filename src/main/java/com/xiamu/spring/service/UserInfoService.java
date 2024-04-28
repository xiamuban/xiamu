package com.xiamu.spring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiamu.spring.dao.UserInfo;

public interface UserInfoService extends IService<UserInfo> {

    UserInfo getUserInfoByUserName(String userName);

}
