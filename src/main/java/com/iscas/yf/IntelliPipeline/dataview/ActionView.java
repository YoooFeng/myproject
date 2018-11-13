package com.iscas.yf.IntelliPipeline.dataview;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Action;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.ActionParam;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepParam;


import java.util.List;

public class ActionView {
    // 需要展示的信息
    public static class DetailedAction {
        public Long id;
        public String stepName;
        public Long createAt;
        public Long updatedAt;
        public List<ActionParamView.Item> params;
    }

    private static Function<Action, DetailedAction> DETAILED_ITEM_VIEW_TRANSFORMER = new Function<Action, DetailedAction>(){
        @Override
        public DetailedAction apply(Action action){
            if(action == null) return null;
            DetailedAction detailedAction = new DetailedAction();
            detailedAction.id = action.getId();
            detailedAction.stepName = action.getStepName();
            detailedAction.params = ActionParamView.viewListOf(action.getParams());
            detailedAction.createAt = action.getCreateTime() == null ? null : action.getCreateTime().getTime();
            detailedAction.updatedAt = action.getUpdateTime() == null ? null : action.getUpdateTime().getTime();
            return detailedAction;

        }
    };

    public static DetailedAction detailedViewOf(Action input){
        return input == null ? null : DETAILED_ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<DetailedAction> detailedViewListOf(List<Action> input){
        return input == null ? ImmutableList.<DetailedAction> of()
                : Lists.transform(input, DETAILED_ITEM_VIEW_TRANSFORMER);
    }
}
