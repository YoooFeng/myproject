package com.iscas.yf.IntelliPipeline.controller;

import com.iscas.yf.IntelliPipeline.dataview.BuildStrategyView;
import com.iscas.yf.IntelliPipeline.entity.BuildStrategy;
import com.iscas.yf.IntelliPipeline.entity.Project;
import com.iscas.yf.IntelliPipeline.service.dataservice.BuildStrategyService;
import com.iscas.yf.IntelliPipeline.service.dataservice.ProjectService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "strategy_data")
public class StrategyController {
    private static final Logger logger = Logger.getLogger(StrategyController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BuildStrategyService strategyService;

    /**
     * 获取某个project strategy的详细信息
     * @Param id
     * @return
     * */
    @RequestMapping(value = {"/get/{projectId}"}, method = RequestMethod.GET)
    @ResponseBody
    public BuildStrategyView.Item getStrategy(@PathVariable("projectId") Long id){
        Project project = projectService.getProject(id);

        return BuildStrategyView.viewOf(project.getStrategy());
    }

    /**
     * new这个API可以是新建, 也可以是更新
     * @Param projectId - 项目在数据库中对应的ID, 策略是跟项目绑定的
     * */
    @RequestMapping(value = {"/new/{projectId}"}, method = RequestMethod.POST)
    @ResponseBody
    public BuildStrategyView.Item createStrategy(@PathVariable("projectId") Long id, @RequestBody BuildStrategyView.Item view){
        Project project = projectService.getProject(id);

        if(project.getStrategy() != null && project.getStrategy().getId() != null){
            // 已经有strategy对象, 进行更新
            BuildStrategy strategy = strategyService.createStrategy(project.getStrategy().getId(), view);
            // 没有必要再存project一遍
            // project.setStrategy(strategy);
            // projectService.saveProject(project);
            return BuildStrategyView.viewOf(strategy);
        } else {
            // 新建strategy对象, 同时与project建立关联
            BuildStrategy strategy = strategyService.createStrategy(project, view);
            project.setStrategy(strategy);
            // 同时更新project对象
            projectService.saveProject(project);
            return BuildStrategyView.viewOf(strategy);
        }
    }
}
