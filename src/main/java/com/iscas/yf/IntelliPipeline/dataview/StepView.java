package com.iscas.yf.IntelliPipeline.dataview;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Relation;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;

import java.util.List;

public class StepView {
    public static class Item {
        public String displayName;
        public String stepName;
        public String stageName;
        public int xPos;
        public int yPos;
    }

    public static class DetailedItem extends Item{
        public List<ParamView.Item> params;
        public List<RelationView.Item> relations;
    }

    public static Function<Step, Item> ITEM_VIEW_TRANSFORMER = new Function<Step, Item>() {
        @Override
        public Item apply(Step input) {
            if(input == null) return null;
            Item view = new Item();
            view.displayName = input.getDisplayName();
            view.stepName = input.getStepName();
            view.stageName = input.getStageName();
            view.xPos = input.getxPos();
            view.yPos = input.getyPos();
            return view;
        }
    };

    public static Item viewOf(Step input){
        return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<Item> viewListOf(List<Step> input){
        return input == null ? ImmutableList.<Item> of() : Lists.transform(input, ITEM_VIEW_TRANSFORMER);
    }

    public static Function<Step, DetailedItem> DETAILED_ITEM_VIEW_TRANSFORMER = new Function<Step, DetailedItem>() {
        @Override
        public DetailedItem apply(Step input) {
            if(input == null) return null;
            DetailedItem view = new DetailedItem();
            view.displayName = input.getDisplayName();
            view.stepName = input.getStepName();
            view.stageName = input.getStageName();
            view.xPos = input.getxPos();
            view.yPos = input.getyPos();

            view.params = ParamView.viewListOf(input.getStepParams());
            view.relations = RelationView.viewListOf(input.getInRelations());
            return view;
        }
    };

    public static DetailedItem detailedViewOf(Step input){
        return input == null ? null : DETAILED_ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<DetailedItem> detailedViewListOf(List<Step> input){
        return input == null ? ImmutableList.<DetailedItem> of()
                : Lists.transform(input, DETAILED_ITEM_VIEW_TRANSFORMER);
    }

}
