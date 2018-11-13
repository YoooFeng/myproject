package com.iscas.yf.IntelliPipeline.entity.pipelinecomponent;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import com.iscas.yf.IntelliPipeline.entity.Build;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="d_step")
public class Step extends IdEntity{

    // TODO: 应对step嵌套子step的情况
    // private ArrayList<Integer, Step> childStep;

    // 该step的显示名称，用来描述该step是用来做什么的
    @Column
    private String displayName;

    // step的名字，对应Jenkins step的方法名
    @Column
    private String stepName;

    @Column
    private String stageName;

    @Column
    private String nodeId;

    // step所需要的参数。Value设计为Object类型，目前已知类型有boolean、String
    // Jenkins workflow DSL.java执行invokeMethod方法时，
    // 应该会将Map中参数对应的value进行强制类型转换(boolean)\(String)
    // 类初始化时把参数Map也初始化
    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StepParam> stepParams;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Relation> relations;

    // 记录在持续集成流程图中的位置
    @Column(columnDefinition = "INT(11) DEFAULT 0")
    private Integer xPos;
    @Column(columnDefinition = "INT(11) DEFAULT 0")
    private Integer yPos;

    @ManyToOne(targetEntity = Build.class)
    @JoinColumn(referencedColumnName = "id", name = "build_id")
    private Build build;

    @Enumerated(EnumType.STRING)
    private Status status;

    // step执行时状态
    public enum Status{
        SUCCESS, FAIL, SUSPENDED, CREATED, EDITED, SKIPPED, RUNNING
    }

    // 只带有一个参数的构造函数
    public Step(String stepName){
        this.stepName = stepName;
        this.stepParams = new ArrayList<>();

        this.relations = new ArrayList<>();
    }

    // 不带参数的构造方法, 新建的一个step
    public Step(){

        this.stepParams = new ArrayList<>();
        this.status = Status.SUSPENDED;

        this.relations = new ArrayList<>();

    }

    // 带参数的构造函数, 所属于一个stage
    public Step(String displayName, String stepName, String stageName, int xPos, int yPos){
        reset(displayName, stepName, stageName, xPos, yPos);
        this.stepParams = new ArrayList<>();
        this.status = Status.SUSPENDED;

        this.relations = new ArrayList<>();
    }
    public void reset(String displayName, String stepName, String stageName, int xPos, int yPos){
        this.displayName = displayName;
        this.stepName = stepName;
        this.xPos = xPos;
        this.yPos = yPos;
        this.stageName = stageName;
    }

    // 通过Action和StepItemInfo新建Step.
    public Step(Action preStep, StepItemInfo stepItemInfo){
        this.stepName = preStep.getStepName();
        this.stepParams = transform(preStep.getParams());
        this.displayName = stepItemInfo.displayName;
        this.stageName = stepItemInfo.stageName;
        this.xPos = stepItemInfo.xPos;
        this.yPos = stepItemInfo.yPos;

        this.relations = new ArrayList<>();
        this.status = Status.SUSPENDED;
    }

    // 只通过Action新建Step
    public Step(Action preStep){
        this.stepName = preStep.getStepName();
        this.stepParams = transform(preStep.getParams());
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public List<StepParam> getStepParams() {
        return stepParams;
    }

    public void setStepParams(List<StepParam> stepParams) {
        this.stepParams = stepParams;
    }

    public Integer getxPos() {
        return xPos;
    }

    public void setxPos(Integer xPos) {
        this.xPos = xPos;
    }

    public Integer getyPos() {
        return yPos;
    }

    public void setyPos(Integer yPos) {
        this.yPos = yPos;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    public List<Relation> getInRelations(){
        List<Relation> inRelations = new ArrayList<>();
        for(Relation re : this.relations){
            if(re.isIn()) inRelations.add(re);
        }
        return inRelations;
    }

    public List<Relation> getOutRelations(){
        List<Relation> outRelations = new ArrayList<>();
        for(Relation re : this.relations){
            if(!re.isIn()) outRelations.add(re);
        }
        return outRelations;
    }

    public List<StepParam> transform(List<ActionParam> actionParam){
        List<StepParam> stepParams = new ArrayList<>();
        for(ActionParam param : actionParam){
            StepParam sParam = new StepParam(param);
            stepParams.add(sParam);
        }
        return stepParams;
    }

    @Override
    public Step clone(){
        Step newStep = new Step();

        newStep.setDisplayName(this.displayName);
        newStep.setNodeId(this.getNodeId());
        newStep.setxPos(this.getxPos());
        newStep.setyPos(this.getyPos());
        newStep.setStageName(this.stageName);
        newStep.setStepName(this.stepName);

        // 深拷贝关系
        List<Relation> nRelations = new ArrayList<>();
        for(Relation oRelation : this.getRelations()){
            Relation nRelation = oRelation.clone();
            nRelations.add(nRelation);
        }
        newStep.setRelations(nRelations);

        // 深拷贝参数
        List<StepParam> nStepParams = new ArrayList<>();
        for(StepParam oStepParam : this.getStepParams()){
            StepParam nStepParam = oStepParam.clone();
            nStepParams.add(nStepParam);
        }
        newStep.setStepParams(nStepParams);

        return newStep;
    }
}
