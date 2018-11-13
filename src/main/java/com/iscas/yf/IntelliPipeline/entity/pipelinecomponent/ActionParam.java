package com.iscas.yf.IntelliPipeline.entity.pipelinecomponent;


import com.iscas.yf.IntelliPipeline.common.entity.OnlyIdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Data
@Entity
// @NoArgsConstructor
// @AllArgsConstructor
@Table(name="d_action_param")
public class ActionParam extends OnlyIdEntity{

    @Column
    private String paramsKey;

    // true - 可选, false - 必选
    @Column
    private boolean optional;

    @ManyToOne(targetEntity = Action.class)
    @JoinColumn(referencedColumnName = "id", name="action_id")
    private Action action;

    public ActionParam(){

    }

    // 构造函数?
    public ActionParam(String paramsKey, boolean optional, Action action){
        this.paramsKey = paramsKey;
        this.optional = optional;
        this.action = action;
    }

    public String getParamsKey() {
        return paramsKey;
    }

    public void setParamsKey(String paramsKey) {
        this.paramsKey = paramsKey;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
