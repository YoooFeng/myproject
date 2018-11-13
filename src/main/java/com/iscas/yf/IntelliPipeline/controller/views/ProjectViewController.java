package com.iscas.yf.IntelliPipeline.controller.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="views/project")
public class ProjectViewController {
    /**
     * 默认的主界面
     * @return
     * */
    @RequestMapping(method = RequestMethod.GET)
    public String mainPage(){
        return "project/main";
    }

    /**
     * 项目列表界面
     * http://localhost:8180/IntelliPipeline/views/project/list
     * @return
     * */
    @RequestMapping(value = {"/list"}, method = RequestMethod.GET)
    public String listPage(){
        return "project/list";
    }

    /**
     * 新建项目的页面, 使用Ajax
     * http://localhost:8180/IntelliPipeline/views/project/new
     * */
    @RequestMapping(value = {"/new"}, method = RequestMethod.GET)
    public String newProject(){
        return "project/new";
    }

    /**
     * 持续集成流程编排界面
     * http://localhost:8180/IntelliPipeline/views/project/panel
     * @return
     * */
    @RequestMapping(value={"/panel"}, method = RequestMethod.GET)
    public String panelPage(){
        return "project/panel";
    }
}
