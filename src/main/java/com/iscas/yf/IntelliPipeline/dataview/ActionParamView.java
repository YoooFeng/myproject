package com.iscas.yf.IntelliPipeline.dataview;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.ActionParam;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepParam;

import java.util.List;

public class ActionParamView {
    public static class Item {
        public String paramsKey;
        public boolean optional;
    }

    public static Function<ActionParam, ActionParamView.Item> ITEM_VIEW_TRANSFORMER = new Function<ActionParam, ActionParamView.Item>() {
        @Override
        public ActionParamView.Item apply(ActionParam input) {
            if(input == null) return null;
            ActionParamView.Item view = new ActionParamView.Item();
            view.paramsKey = input.getParamsKey();
            view.optional = input.isOptional();
            return view;
        }
    };

    public static ActionParamView.Item viewOf(ActionParam input) {
        return input == null? null : ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<ActionParamView.Item> viewListOf(List<ActionParam> input){
        return input == null ? ImmutableList.<ActionParamView.Item> of() : Lists.transform(input, ITEM_VIEW_TRANSFORMER);
    }

}
