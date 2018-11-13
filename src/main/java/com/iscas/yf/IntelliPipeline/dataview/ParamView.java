package com.iscas.yf.IntelliPipeline.dataview;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepParam;

import java.util.List;

public class ParamView {

    public static class Item {
        public String paramsKey;
        public Object paramValue;
        public boolean optional;
    }

    public static Function<StepParam, Item> ITEM_VIEW_TRANSFORMER = new Function<StepParam, Item>() {
        @Override
        public Item apply(StepParam input) {
            if(input == null) return null;
            Item view = new Item();
            view.paramsKey = input.getParamsKey();
            view.paramValue = input.getParamsValue();
            view.optional = input.isOptional();
            return view;
        }
    };

    public static Item viewOf(StepParam input) {
        return input == null? null : ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<Item> viewListOf(List<StepParam> input){
        return input == null ? ImmutableList.<Item> of() : Lists.transform(input, ITEM_VIEW_TRANSFORMER);
    }
}
