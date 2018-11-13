package com.iscas.yf.IntelliPipeline.dataview;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Relation;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;

import java.util.List;

public class BuildView {
    // 要展示的Item
    public static class Item {
        public String projectName;
        public Long id;
        public Long projectId;
        public String consoleOutputFilePath;
        public Long createAt;
        public String durationTime;
        public String commitId;
        // 内部类
        public Build.Status status;
    }

    // TODO: 显示构建的详情, 如pipeline执行具体状况, 每个stage的控制台输出等
    public static class DetailedItem extends Item{
        public List<StepView.DetailedItem> steps;

        // 考虑如何将steps中的relation转移到build中
        public List<RelationView.Item> relations;
    }

    private static Function<Build, Item> ITEM_VIEW_TRANSFORMER = new Function<Build, Item>() {
        @Override
        public Item apply(Build input) {
            if(input == null) return null;
            Item view = new Item();
            view.id = input.getId();
            view.createAt = input.getCreateTime() == null ? null : input.getCreateTime().getTime();
            view.projectName = input.getProject().getProjectName();
            view.projectId = input.getProject().getId();
            view.consoleOutputFilePath = input.getConsoleOutputFilePath();
            view.status = input.getStatus();
            view.durationTime = input.getDurationTime();
            view.commitId = input.getLatestCommitId();
            return view;
        }
    };

    public static Item viewOf(Build input){
        return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<Item> viewListOf(List<Build> input){
        return input == null ? ImmutableList.<Item> of() : Lists.transform(
                input, ITEM_VIEW_TRANSFORMER);
    }

    private static Function<Build, DetailedItem> DETAILED_ITEM_VIEW_TRANSFORMER = new Function<Build, DetailedItem>() {
        @Override
        public DetailedItem apply(Build input) {
            if(input == null)return null;
            DetailedItem view = new DetailedItem();
            view.id = input.getId();
            view.createAt = input.getCreateTime() == null ? null : input.getCreateTime().getTime();
            view.projectName = input.getProject().getProjectName();
            view.projectId = input.getProject().getId();
            view.consoleOutputFilePath = input.getConsoleOutputFilePath();
            view.status = input.getStatus();
            view.steps = StepView.detailedViewListOf(input.getSteps());

            return view;
        }
    };

    public static DetailedItem detailedViewOf(Build input){
        return input == null ? null : DETAILED_ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<DetailedItem> detailedViewListOf(List<Build> input){
        return input == null ? ImmutableList.<DetailedItem> of() : Lists.transform(
                input, DETAILED_ITEM_VIEW_TRANSFORMER);
    }
}
