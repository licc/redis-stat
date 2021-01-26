package com.huan.redisstat.configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: lihuan
 * @Date: 2021/1/18 13:07
 * @Description:
 */

public class MyPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence RawPassword) {

        return RawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence RawPassword, String EncodePassword) {

        return EncodePassword.equals(RawPassword.toString());
    }



}