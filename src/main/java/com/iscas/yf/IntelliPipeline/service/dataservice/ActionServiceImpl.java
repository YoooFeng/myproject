package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dao.ActionDAO;
import com.iscas.yf.IntelliPipeline.dao.ActionParamDAO;
import com.iscas.yf.IntelliPipeline.dataview.ActionParamView;
import com.iscas.yf.IntelliPipeline.dataview.ActionView;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Action;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.ActionParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActionServiceImpl implements ActionService{
    private Logger logger = Logger.getLogger(ActionServiceImpl.class);

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private ActionParamDAO actionParamDAO;

    public List<Action> getAllActions(){
        return (List<Action>)actionDAO.findAll();
    }

    public Action getAction(Long actionId) {
        Action action = actionDAO.findOne(actionId);
        return action;
    }

    // 须修改, 带上必须的参数进行构造
    public Action createAction(ActionView.DetailedAction view){
        logger.info(view);

        // 去掉多余的双引号
        view.stepName = view.stepName.replace("\"", "");

        logger.info("creating action for " + view.stepName);

        Action action = new Action(view.stepName);

        List<ActionParamView.Item> params = view.params;

        logger.info("action params: " + params);

        // 两边互相添加
        for(ActionParamView.Item param : params){
            ActionParam curParam = new ActionParam(param.paramsKey, param.optional, action);
            curParam.setAction(action);
            action.getParams().add(curParam);
        }

        actionDAO.save(action);

        return action;
    }

    public boolean deleteAction(Long actionId){
        actionDAO.delete(actionId);
        return true;
    }

    public Action modifyAction(Long actionId, ActionView.DetailedAction view){
        logger.info("modify action");
        Action action = actionDAO.findOne(actionId);
        action.setStepName(view.stepName);

        List<ActionParam> params = new ArrayList<>();
        for(ActionParamView.Item param : view.params){
            ActionParam curParam = new ActionParam(param.paramsKey, param.optional, action);
            curParam.setAction(action);
            params.add(curParam);
        }

        action.setParams(params);
        return action;
    }

}
