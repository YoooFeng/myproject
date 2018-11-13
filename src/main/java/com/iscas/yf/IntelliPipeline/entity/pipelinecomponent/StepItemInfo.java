package com.iscas.yf.IntelliPipeline.entity.pipelinecomponent;

import java.util.Map;

public class StepItemInfo {
    public Long id;
    public String displayName;

    // 记录每一条param的值, 在step中进行新建操作
    public Map<String, String> paramValue;

    public String stageName;
    public Relation inRelation;
    public Relation outRelation;
    public Integer xPos;
    public Integer yPos;
}
