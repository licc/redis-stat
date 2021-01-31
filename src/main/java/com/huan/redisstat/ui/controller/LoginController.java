package com.huan.redisstat.ui.controller;

import com.huan.redisstat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author lihuan on 2021/01/18.
 */
@Slf4j
@Controller
@ConditionalOnProperty(prefix = "security", name = "cas-login", havingValue = "false")
public class LoginController {


    @Autowired
    UserService userService;

    @GetMapping(value = "login")
    public String index() {
        return "login";
    }


}
