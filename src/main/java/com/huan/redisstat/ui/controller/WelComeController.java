package com.huan.redisstat.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: lihuan
 * @Date: 2021/1/31 13:33
 * @Description:
 */
@Controller
public class WelComeController {

    @RequestMapping("/")
    public String loginSuccess(HttpServletRequest request) {
        return "redirect:clusters";
    }
}
