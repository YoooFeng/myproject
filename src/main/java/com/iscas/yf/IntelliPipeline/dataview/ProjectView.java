package com.iscas.yf.IntelliPipeline.dataview;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.Project;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;

import java.util.List;

public class ProjectView {

    // Items will be shown on page
    public static class Item {
        public Long id;
        public Long createdAt;
        public Long updatedAt;
        public Project.Status status;

        public String name;
        public String gitUrl;
    }

    // 详情页面, 显示该项目的所有构建情况
    public static class DetailedItem extends Item{

        public List<BuildView.Item> builds;

        public BuildView.DetailedItem template;

        public BuildStrategyView.Item strategy;
    }

    // Project简略信息, 不显示所有构建的情况
    private static Function<Project, Item> ITEM_VIEW_TRANSFORMER = new Function<Project, Item>() {
        @Override
        public Item apply(Project input) {
            if(input == null) return null;
            Item view = new Item();
            view.id = input.getId();
            view.name = input.getProjectName();
            view.gitUrl = input.getGitURL();
            view.status = input.getStatus();
            view.createdAt = input.getCreateTime() == null ? null : input.getCreateTime().getTime();
            view.updatedAt = input.getUpdateTime() == null ? null : input.getUpdateTime().getTime();
            return view;
        }
    };

    public static Item viewOf(Project input){
        return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<Item> viewListOf(List<Project> input){
        return input == null ? ImmutableList.<Item> of() : Lists.transform(input, ITEM_VIEW_TRANSFORMER);
    }

    // Project详细信息, 显示所属的所有构建
    private static Function<Project, DetailedItem> DETAILED_ITEM_TRANSFORMER = new Function<Project, DetailedItem>() {
        @Override
        public DetailedItem apply(Project input) {
            if(input == null) return null;
            DetailedItem view = new DetailedItem();
            view.id = input.getId();
            view.name = input.getProjectName();
            view.gitUrl = input.getGitURL();
            view.status = input.getStatus();
            view.createdAt = input.getCreateTime() == null ? null : input.getCreateTime().getTime();
            view.updatedAt = input.getUpdateTime() == null ? null : input.getUpdateTime().getTime();
            view.builds = BuildView.viewListOf(input.getBuilds());

            // 把第一次构建的流程作为Project的template, 如果没有构建记录, 返回空
            if(input.getBuilds().size() == 0) view.template = null;
            else view.template = BuildView.detailedViewOf(input.getBuilds().get(input.getBuilds().size() - 1));

            // build所属的strategy对象
            view.strategy = BuildStrategyView.viewOf(input.getStrategy());

            return view;
        }
    };

    public static DetailedItem detailedViewOf(Project input){
        return input == null ? null : DETAILED_ITEM_TRANSFORMER.apply(input);
    }

    public static List<DetailedItem> detailedViewListOf(List<Project> input){
        return input == null ? ImmutableList.<DetailedItem> of() : Lists.transform(input, DETAILED_ITEM_TRANSFORMER);
    }
}
