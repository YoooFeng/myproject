package com.iscas.yf.IntelliPipeline.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/")
public class IndexController {
    /**
     * 程序的入口
     * */
    @RequestMapping(method = RequestMethod.GET)
    public String getIndex(){
        return "index";
    }
}
