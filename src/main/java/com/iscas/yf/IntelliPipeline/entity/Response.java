package com.iscas.yf.IntelliPipeline.entity;

import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepParam;

import java.util.HashMap;
import java.util.Map;

public class Response {

    // 返回的decision，可以是"Retry"、"Skip"、"Abort"等，中间结果只有比较低效的保存方式。
    private String decisionType;
    private String stepName;
    private Map<String, String> params;

    public Response() {
        this.params = new HashMap<>();
    }

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setNameAndParams(Step step) {
        this.stepName = step.getStepName();
        for(StepParam param : step.getStepParams()){
            // 将没有内容的参数去除
            System.out.println("param: " + param.getParamsKey() + " / " + param.getParamsValue());
            if(param.getParamsValue().equals("")) continue;
            this.params.put(param.getParamsKey(), param.getParamsValue());
        }
    }

    @Override
    public String toString() {
        return "Response{" +
                "decisionType='" + decisionType + '\'' +
                ", stepName='" + stepName + '\'' +
                ", params=" + params +
                '}';
    }
}
