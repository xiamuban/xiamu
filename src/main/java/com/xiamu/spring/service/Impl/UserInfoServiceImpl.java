package com.xiamu.spring.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiamu.spring.dao.UserInfo;
import com.xiamu.spring.mapper.UserInfoMapper;
import com.xiamu.spring.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)//异常事务回滚
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo getUserInfoByUserName(String userName) {
        log.info("getUserInfoByUserName:{}", userName);
        LambdaUpdateWrapper<UserInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(UserInfo::getUserName, userName);
        return getOne(lambdaUpdateWrapper);
    }

    @Override
    public boolean deleteUserInfoByUserName(String userName) {
        LambdaUpdateWrapper<UserInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(UserInfo::getUserName, userName);
        return remove(lambdaUpdateWrapper);
    }

    @Override
    public int deleteUserByUserName(String userName) {
        userInfoMapper.backUserInfo(userName);
        return userInfoMapper.deleteByUserName(userName);
    }
}
