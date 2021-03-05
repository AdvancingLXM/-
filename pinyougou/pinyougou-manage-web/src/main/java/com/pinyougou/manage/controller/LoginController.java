package com.pinyougou.manage.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/login")
@RestController
public class LoginController {

    /**
     * 获取当前登录用户的名称
     * @return 用户信息
     */
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername(){
        Map<String, Object> result = new HashMap<String, Object>();

        //获取在security中存在的当前登录成功的用户信息
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        result.put("username", username);

        return result;
    }
}
