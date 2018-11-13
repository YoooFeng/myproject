package com.iscas.yf.IntelliPipeline.deprecated;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.iscas.yf.IntelliPipeline.dataview.StepView;
import com.iscas.yf.IntelliPipeline.deprecated.Stage;

import java.util.List;

@Deprecated
public class StageView {
    public static class Item{
        public String stageName;
        public List<StepView.Item> steps;
    }

    public static Function<Stage, Item> ITEM_VIEW_TRANSFORMER = new Function<Stage, Item>() {
        @Override
        public Item apply(Stage input) {
            if(input == null) return null;
            Item view = new Item();
            view.stageName = input.getStageName();
            return view;
        }
    };

    public static Item viewOf(Stage input){
        return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<Item> viewListOf(List<Stage> input){
        return input == null ? ImmutableList.<Item> of() : Lists.transform(input, ITEM_VIEW_TRANSFORMER);
    }

}
