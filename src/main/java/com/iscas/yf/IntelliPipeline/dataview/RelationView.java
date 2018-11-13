package com.iscas.yf.IntelliPipeline.dataview;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Relation;

import java.util.List;

public class RelationView {
    public static class Item {
        public String fromId;
        public String toId;
    }

    public static Function<Relation, Item> ITEM_VIEW_TRANSFORMER = new Function<Relation, Item>() {
        @Override
        public Item apply(Relation input) {
            if(input == null) return null;
            Item view = new Item();
            view.fromId = input.getFromId();
            view.toId = input.getToId();

            return view;
        }
    };

    public static Item viewOf(Relation input) {
        return input == null? null : ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<Item> viewListOf(List<Relation> input){
        return input == null ? ImmutableList.<Item> of() : Lists.transform(input, ITEM_VIEW_TRANSFORMER);
    }
}
