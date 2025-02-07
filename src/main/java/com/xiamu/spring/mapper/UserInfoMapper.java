package com.xiamu.spring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiamu.spring.dao.UserInfo;

public interface UserInfoMapper extends BaseMapper<UserInfo> {

    int deleteByUserName(String userName);

    void backUserInfo(String userName);

}