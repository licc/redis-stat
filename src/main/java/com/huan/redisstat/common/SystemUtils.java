package com.huan.redisstat.common;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : lihuan
 * @date : 2021-02-01 13:00
 */
public class SystemUtils {

    public static UserDetails getCurrLoginUser() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal;
    }

}
