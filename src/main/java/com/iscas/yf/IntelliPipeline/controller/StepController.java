package com.iscas.yf.IntelliPipeline.controller;

import com.iscas.yf.IntelliPipeline.dao.ActionDAO;
import com.iscas.yf.IntelliPipeline.dao.ProjectDAO;
import com.iscas.yf.IntelliPipeline.dao.StepDAO;
import com.iscas.yf.IntelliPipeline.dataview.StepView;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import com.iscas.yf.IntelliPipeline.service.dataservice.ProjectService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/step_data")
public class StepController {
    private static final Logger logger = Logger.getLogger(StepController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private StepDAO stepDAO;



    /**
     * 通过rest API的方式获取一个新的step对象
     * */
    @RequestMapping(value = "/actionToStep", method = RequestMethod.POST)
    @ResponseBody
    public Step actionToStep(@RequestBody StepView.Item view){
        Step step = new Step(view.stepName);
        return step;
    }
    /**
     * 在数据库中新建一个step
     * */
    // @RequestMapping(value = "/new", method = RequestMethod.POST)
    // @ResponseBody
    // public StepView.DetailedItem createStep(StepView.DetailedItem view){
    //     return StepView.detailedViewOf(stepService)
    // }


}
