package com.iscas.yf.IntelliPipeline.entity;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Relation;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepParam;
import com.iscas.yf.IntelliPipeline.entity.record.BuildRecord;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


@Entity
@Table(name = "d_build")
public class Build extends IdEntity {

    // buildId这个主键是自增长的, 但是想要的效果应该是在不同的projectName下自增长!

    // 多次build可以属于一个project, Cascade设置级联保存
    @ManyToOne(targetEntity = Project.class)
    @JoinColumn(referencedColumnName="id", name="project_id")
    private Project project;

    @Column(name="latest_commit")
    private String latestCommitId;

    @Column(name="console_out")
    private String consoleOutputFilePath;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="duration_time")
    private String durationTime;

    // OneToMany不指定作为外键的列, 统一在ManyToOne中指定
    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Step> steps;

    @OneToOne(targetEntity = BuildRecord.class, mappedBy = "build", cascade = CascadeType.ALL)
    private BuildRecord record;

    public enum Status {
        RUNNING, SUCCEED, FAIL, ABORTED, SKIPPED
    }

    @SuppressWarnings("unuse")
    public Build(){

    }

    // 构造函数，对build对象进行初始化
    public Build(Project project, String latestCommitId, String consoleOutputFilePath){
        this.project = project;
        this.latestCommitId = latestCommitId;
        this.consoleOutputFilePath = consoleOutputFilePath;
    }

    public Build(Project project, Build oldBuild){
        this.project = project;
        this.status = Status.RUNNING;

        // 设置新建Build的日期
        this.setCreateTime(new Date());
        this.setUpdateTime(new Date());

        List<Step> steps = oldBuild.cloneSteps();

        // 每一个step都恢复未执行的状态
        int count = -1, flag = -1;
        for(Step step : steps){
            count++;
            // 跳过上一次构建的mail任务
            if(step.getStepName().equals("mail")){
                step.setStatus(Step.Status.SKIPPED);
                flag = count;
            }
            step.setStatus(Step.Status.SUSPENDED);
            step.setBuild(this);
        }

        // 移除mail任务
        if(flag != -1) steps.remove(flag);

        // 新的构建具有的steps
        this.setSteps(steps);

    }

    public BuildRecord getRecord() {
        return record;
    }

    public void setRecord(BuildRecord record) {
        this.record = record;
    }

    public String getLatestCommitId() {
        return latestCommitId;
    }

    public String getConsoleOutputFilePath() {
        return consoleOutputFilePath;
    }

    public void setLatestCommitId(String latestCommitId) {
        this.latestCommitId = latestCommitId;
    }

    public void setConsoleOutputFilePath(String consoleOutputFilePath) {
        this.consoleOutputFilePath = consoleOutputFilePath;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Status getStatus() {
        return status;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // 改变build的状态
    public void changeStatusToRunning(){
        this.status = Status.RUNNING;
    }

    public void changeStatusToAborted(){
        this.status = Status.ABORTED;
    }

    public void changeStatusToSucceed(){
        this.status = Status.SUCCEED;
    }

    public void changeStatusToFail(){
        this.status = Status.FAIL;
        for(Step step : this.steps){
            if(!step.getStepName().equals("mail") && step.getStatus().equals(Step.Status.RUNNING)) step.setStatus(Step.Status.FAIL);
        }
    }

    // 将build状态变为跳过, 同时将所有step(除去mail)的状态也改为跳过
    public void changeStatusToSkipped(){
        this.status = Status.SKIPPED;
        for(Step step : this.steps){
            if(!step.getStepName().equals("mail")) step.setStatus(Step.Status.SKIPPED);
        }
    }

    public String getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(String durationTime) {
        this.durationTime = durationTime;
    }

    public List<Step> cloneSteps(){
        List<Step> newSteps = new ArrayList<>();
        List<Step> oldSteps = this.getSteps();
        for(Step step : oldSteps){
            Step newStep = step.clone();

            // 重新建立关联关系
            for(Relation relation : newStep.getRelations()){
                relation.setStep(newStep);
            }
            for(StepParam stepParam : newStep.getStepParams()){
                stepParam.setStep(newStep);
            }

            newSteps.add(newStep);
        }
        return newSteps;
    }

    public BuildGraph transformBuildToGraph(){
        BuildGraph graph = new BuildGraph();
        graph.name = this.getProject().getProjectName();
        graph.relations = new ArrayList<>();
        graph.nodes = new ArrayList<>();
        graph.id = this.getId();
        graph.record = this.getRecord() == null ? "" : this.getRecord().toPredictionString();

        for(Step step : this.steps){
            BuildGraph.StepNode curNode = new BuildGraph.StepNode();

            curNode.xPos = String.valueOf(step.getxPos());
            curNode.yPos = String.valueOf(step.getyPos());
            curNode.displayName = step.getDisplayName();
            curNode.stepName = step.getStepName();
            curNode.nodeId = step.getNodeId();
            curNode.stepStatus = step.getStatus();
            curNode.params = new ArrayList<>();
            curNode.stageName = step.getStageName();

            // 处理参数, 不一定用的上, 可以提供查看
            for(StepParam param : step.getStepParams()){
                curNode.params.add(new BuildGraph.NodeParam(param.getParamsKey(), param.getParamsValue()));
            }

            // 先处理outRelations
            if(step.getRelations() != null && step.getOutRelations() != null){
                for(Relation re : step.getOutRelations()){
                    graph.relations.add(new BuildGraph.StepRelation(
                            re.getFromId(),
                            re.getToId()));
                }
            }

            if(step.getRelations() != null && step.getInRelations() != null) {
                for(Relation re : step.getInRelations()){
                    graph.relations.add(new BuildGraph.StepRelation(re.getFromId(), re.getToId()));
                }
            }

            graph.nodes.add(curNode);
        }

        // 去除重复的边, 只传输最小集
        HashSet<BuildGraph.StepRelation> h = new HashSet<>(graph.relations);
        graph.relations.clear();
        graph.relations.addAll(h);

        return graph;
    }
}
