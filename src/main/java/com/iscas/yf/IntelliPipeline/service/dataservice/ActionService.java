package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dataview.ActionView;
import com.iscas.yf.IntelliPipeline.dataview.ProjectView;
import com.iscas.yf.IntelliPipeline.entity.Project;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Action;

import java.util.List;

public interface ActionService {


    public Action createAction(ActionView.DetailedAction view);

    public List<Action> getAllActions();

    public Action getAction(Long actionId);

    public boolean deleteAction(Long actionId);

    public Action modifyAction(Long actionId, ActionView.DetailedAction view);
}
