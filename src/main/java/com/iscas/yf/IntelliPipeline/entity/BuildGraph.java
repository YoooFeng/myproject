package com.iscas.yf.IntelliPipeline.entity;

import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Action;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Relation;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepParam;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class BuildGraph {
    public String name;
    public List<StepNode> nodes;
    public List<StepRelation> relations;
    public Long id;

    // 构造函数
    public BuildGraph(){
        this.nodes = new ArrayList<BuildGraph.StepNode>();
        this.relations = new ArrayList<StepRelation>();
    }

    public static class StepNode {
        public String componentId;
        public String nodeId;
        public List<NodeParam> params;
        public String stepName;
        public String displayName;
        public String stageName;
        public Step.Status stepStatus;
        public String xPos;
        public String yPos;
        public String actionId;
        public Action action;

        // 将自身转化为step的方法
        public Step transformNodeToStep() {
            Step step  = new Step(this.stepName);
            step.setDisplayName(this.displayName);
            step.setStageName(this.stageName);
            step.setStatus(this.stepStatus);

            // 浏览器坐标的配适
            if(this.xPos.length() >= 7){
                step.setxPos(Integer.parseInt(this.xPos.substring(0, 6)));
            } else {
                step.setxPos(Integer.parseInt(this.xPos));
            }

            if(this.yPos.length() >= 7){
                step.setxPos(Integer.parseInt(this.yPos.substring(0, 6)));
            } else {
                step.setyPos(Integer.parseInt(this.yPos));
            }

            step.setNodeId(this.nodeId);

            for(NodeParam param : params){
                // 创建stepParam对象, 加入step对象中
                StepParam stepParam = new StepParam(param.paramsKey, param.paramValue);
                step.getStepParams().add(stepParam);
            }
            return step;
        }
    }

    // 必须先要实例化BuildGraph才能转化.
    public List<Step> transformGraphToBuild(){
        List<Step> steps = new ArrayList<>();
        for(StepNode stepNode : this.nodes){
            steps.add(stepNode.transformNodeToStep());
        }

        // TODO: 重构step的顺序. 如果有没连线的step会报错!
        for(StepRelation relation : this.relations) {
            System.out.println("Relation: " + relation.toString());
            Step fromStep = null;
            Step toStep = null;
            // 遍历有问题, 会导致fromStep和toStep成为同一个?
            for(Step step : steps){
                if(step.getNodeId().equals(relation.from)){
                    fromStep = step;
                }
                if(step.getNodeId().equals(relation.to)){
                    toStep = step;
                }
                if(fromStep != null && toStep != null) break;
            }
            System.out.println("fromStep: " + fromStep.getNodeId());
            System.out.println("toStep: " + toStep.getNodeId());

            if(fromStep.getRelations() == null) {
                fromStep.setRelations(new ArrayList<Relation>());
                Relation re = new Relation(fromStep.getNodeId(), toStep.getNodeId(), false);
                re.setStep(fromStep);
                fromStep.getRelations().add(re);
            }
            else {
                Relation re = new Relation(fromStep.getNodeId(), toStep.getNodeId(), false);
                re.setStep(fromStep);
                fromStep.getRelations().add(re);
            }

            if(toStep.getRelations() == null) {
                toStep.setRelations(new ArrayList<Relation>());
                Relation re = new Relation(fromStep.getNodeId(), toStep.getNodeId(), true);
                re.setStep(toStep);
                toStep.getRelations().add(re);
            }
            else {
                Relation re = new Relation(fromStep.getNodeId(), toStep.getNodeId(), true);
                re.setStep(toStep);
                toStep.getRelations().add(re);
            }

        }
        return steps;
    }


    // 只需要所有的参数以及值即可, 不需要optional等额外属性
    public static class NodeParam {
        public String paramsKey;
        public String paramValue;

        public NodeParam(){

        }

        public NodeParam(String key, String value){
            this.paramsKey = key;
            this.paramValue = value;
        }
    }

    public static class StepRelation{
        public String from;
        public String to;

        public StepRelation(){

        }

        public StepRelation(String from, String to){
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "StepRelation{" +
                    "from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StepRelation)) return false;
            StepRelation that = (StepRelation) o;
            return Objects.equals(from, that.from) &&
                    Objects.equals(to, that.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

}
