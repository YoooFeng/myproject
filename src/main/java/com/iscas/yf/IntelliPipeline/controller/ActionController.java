package com.iscas.yf.IntelliPipeline.controller;

import com.iscas.yf.IntelliPipeline.dao.ActionDAO;
import com.iscas.yf.IntelliPipeline.dao.ProjectDAO;
import com.iscas.yf.IntelliPipeline.dao.StepDAO;
import com.iscas.yf.IntelliPipeline.dataview.ActionView;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Action;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepItemInfo;
import com.iscas.yf.IntelliPipeline.service.dataservice.ActionService;
import com.iscas.yf.IntelliPipeline.service.dataservice.ProjectService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "action_data")
public class ActionController {
    private static final Logger logger = Logger.getLogger(ActionController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private StepDAO stepDAO;

    @Autowired
    private ActionService actionService;

    /**
     * 返回所有action的列表
     * */
    @RequestMapping(value = {"/listDetailedActions"}, method = RequestMethod.GET)
    @ResponseBody
    public List<ActionView.DetailedAction> listDetailedActions(){
        // logger.info("Get all actions");
        List<Action> actions = actionService.getAllActions();
        return ActionView.detailedViewListOf(actions);
    }

    /**
     * 返回List<ActionView.Item>这个对象
     * */
    @RequestMapping(value = {"/list"})
    @ResponseBody
    public List<ActionView.DetailedAction> listActions(){
        List<Action> actions = new ArrayList<>();
        for(Action action : actionDAO.findAll()) actions.add(action);

        return ActionView.detailedViewListOf(actions);
    }

    /**
     * 一键将一个Action实例化成一个Step. 点击加号?
     * @Param StepItemInfo中有将一个Action转换成Step的所有信息
     * @return
     * */
    @RequestMapping(value = {"/apply"})
    @ResponseBody
    public Step applyAction(@RequestBody StepItemInfo stepItemInfo){
        // 首先Step的信息中指定了这个step是基于哪个action的, 因此先获取这个action对象
        Action action = actionDAO.findOne(stepItemInfo.id);
        // 通过Action和StepItemInfo两个对象新建出一个step, 存储并将之返回
        Step step = new Step(action, stepItemInfo);
        stepDAO.save(step);

        return step;
    }

    /**
     * 获取某个Action的详细信息
     * @Param id
     * @return
     * */
    @RequestMapping(value = {"/get/{actionId}"}, method = RequestMethod.GET)
    @ResponseBody
    public ActionView.DetailedAction getAction(@PathVariable("actionId") Long id){
        Action action = actionDAO.findOne(id);
        return ActionView.detailedViewOf(action);
    }

    /**
     * 删除Action
     * @Param id
     * @return
     * */
    @RequestMapping(value = {"/delete/{actionId}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public Iterable<Action> deleteAction(@PathVariable("actionId") Long id){
        Action action = actionDAO.findOne(id);
        final String actionName = action.getStepName();
        logger.info("Delete the action: " + actionName);
        actionService.deleteAction(id);
        return actionService.getAllActions();
    }

    /**
     * 新建Action对象
     * @Param
     * @return
     * */
    @RequestMapping(value = {"/new"}, method = RequestMethod.POST)
    @ResponseBody
    public ActionView.DetailedAction createAction(@RequestBody ActionView.DetailedAction view){
        return ActionView.detailedViewOf(actionService.createAction(view));
    }
}
