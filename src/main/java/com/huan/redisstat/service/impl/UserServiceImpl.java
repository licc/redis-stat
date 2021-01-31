package com.huan.redisstat.service.impl;

import com.huan.redisstat.persistence.entities.User;
import com.huan.redisstat.persistence.mapper.UserMapper;
import com.huan.redisstat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User getByUserId(String userId) {
        return null;
    }

    @Override
    public UserDetails getAccessableResource(String name, String ldapNo) {

        return new User().setUsername(name).setLdapNo(ldapNo);
    }

}
