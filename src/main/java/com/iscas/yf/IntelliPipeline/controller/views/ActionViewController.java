package com.iscas.yf.IntelliPipeline.controller.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "views/action")
public class ActionViewController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String actionPage(){
        return "action/list";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newAction(){
        return "action/new";
    }
}
