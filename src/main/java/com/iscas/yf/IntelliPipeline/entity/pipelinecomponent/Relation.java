package com.iscas.yf.IntelliPipeline.entity.pipelinecomponent;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import com.iscas.yf.IntelliPipeline.entity.Project;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

/**
 * 描述两个step先后关系的实体, 目前暂时只支持一对一的step间关系
 * */
@Entity
@Table(name = "d_step_relation")
public class Relation extends IdEntity implements Cloneable{

    @Column
    private String fromId;

    @Column
    private String toId;

    @Column
    private boolean inRe;

    @ManyToOne(targetEntity = Step.class)
    @JoinColumn(referencedColumnName = "id", name = "relation_id")
    Step step;

    @SuppressWarnings("unused")
    public Relation() {

    }

    // 构造函数
    public Relation(String from, String to, boolean inRe){
        this.fromId = from;
        this.toId = to;
        this.inRe = inRe;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public boolean isIn() {
        return inRe;
    }

    public void setIn(boolean in) {
        this.inRe = in;
    }

    @Override
    public Relation clone(){

        Relation n = new Relation();
        n.setFromId(this.getFromId());
        n.setToId(this.toId);
        n.setIn(this.isIn());

        return n;
    }
}
