package com.iscas.yf.IntelliPipeline.controller.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginViewController {
    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String loginPage(){
        return "login";
    }
}
