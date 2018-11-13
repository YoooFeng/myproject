package com.iscas.yf.IntelliPipeline.entity.pipelinecomponent;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import com.iscas.yf.IntelliPipeline.common.entity.OnlyIdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Data
@Entity
@Table(name="d_step_param")
public class StepParam extends OnlyIdEntity implements Cloneable{

    @Column
    private String paramsKey;

    // 可能是String\int\boolean等等类型, 不确定
    @Column
    private String paramValue;

    // true - 可选, false - 必选
    @Column
    private boolean optional;

    @ManyToOne(targetEntity = Step.class)
    @JoinColumn(referencedColumnName = "id", name="step_id")
    private Step step;

    // 空构造函数
    public StepParam(){

    }

    // 通过ActionParam构建StepParam的函数
    public StepParam(ActionParam aParam){
        this.paramsKey = aParam.getParamsKey();
        this.optional = aParam.isOptional();
    }

    public StepParam(String key, String value){
        this.paramsKey = key;
        this.paramValue = value;
    }

    public String getParamsKey() {
        return paramsKey;
    }

    public void setParamsKey(String paramsKey) {
        this.paramsKey = paramsKey;
    }

    public String getParamsValue() {
        return paramValue;
    }

    public void setParamsValue(String paramsValue) {
        this.paramValue = paramsValue;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public StepParam clone(){
        StepParam newStepParam = new StepParam();

        newStepParam.setOptional(this.isOptional());
        newStepParam.setParamsKey(this.getParamsKey());
        newStepParam.setParamsValue(this.paramValue);

        return newStepParam;
    }
}
