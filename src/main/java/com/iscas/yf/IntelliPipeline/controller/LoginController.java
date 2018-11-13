package com.iscas.yf.IntelliPipeline.controller;


import com.iscas.yf.IntelliPipeline.dataview.UserView;
import com.iscas.yf.IntelliPipeline.service.dataservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class LoginController {

    @Autowired
    UserService userService;

    /**
     * 新建用户
     * */
    @RequestMapping(value = {"/register"}, method = RequestMethod.POST)
    public UserView.Item createUser(@RequestBody UserView.Item userItem){
        return UserView.detailedViewOf(userService.createUser(userItem));
    }

    // @RequestMapping(value = {"/login"}, method = RequestMethod.POST)
    // public boolean
}
