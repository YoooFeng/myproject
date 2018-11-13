package com.iscas.yf.IntelliPipeline.dataview;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.iscas.yf.IntelliPipeline.entity.BuildStrategy;

import java.util.List;

public class BuildStrategyView {
    public static class Item{

        public long time_interval;
        public int modified_lines;
        public boolean model_modified;
        public String committers_mail;

        // separated by ,
        public String key_paths;
        public String skip_paths;
    }

    public static Function<BuildStrategy, Item> ITEM_VIEW_TRANSFORMER = new Function<BuildStrategy, Item>() {
        @Override
        public Item apply(BuildStrategy input) {
            if(input == null) return null;
            Item view = new Item();
            view.time_interval = input.getTime_interval();
            view.modified_lines = input.getModified_lines();
            view.model_modified = input.isModel_modified();
            view.committers_mail = input.getCommitters_mail();
            view.key_paths = input.getKey_paths();
            view.skip_paths = input.getSkip_paths();
            return view;
        }
    };

    public static Item viewOf(BuildStrategy input){
        return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<Item> viewListOf(List<BuildStrategy> input){
        return input == null ? ImmutableList.<Item> of() : Lists.transform(input, ITEM_VIEW_TRANSFORMER);
    }
}
