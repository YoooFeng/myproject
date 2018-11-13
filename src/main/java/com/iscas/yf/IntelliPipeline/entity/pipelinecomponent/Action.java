package com.iscas.yf.IntelliPipeline.entity.pipelinecomponent;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 预设的执行shell脚本的Step
 * 从这个基础上可以衍生出很多不同的预设step
 *
 * */

@Entity
@Table(name = "d_action")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action extends IdEntity {

    // TODO: 应对step嵌套子step的情况
    // private ArrayList<Integer, Step> childStep;

    // Action不需要一个具体的displayName

    // step的名字，对应Jenkins step的方法名
    @Column
    private String stepName;

    // cascade设定级联保存和删除
    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ActionParam> params;

    public Action(String stepName){
        this.stepName = stepName;
        this.params = new ArrayList<>();
    }

    public Action(Action step){
        this.createTime = step.createTime;
        this.stepName = step.stepName;
        this.id = step.id;
        this.updateTime = step.updateTime;
        this.params = step.params;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public List<ActionParam> getParams() {
        return params;
    }

    public void setParams(List<ActionParam> params) {
        this.params = params;
    }
}
