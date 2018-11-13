package com.iscas.yf.IntelliPipeline.entity.key;

import java.io.Serializable;

public class BuildKey implements Serializable{

    private Long buildId;
    private String projectName;

    public Long getBuildId() {
        return buildId;
    }

    public void setBuildId(Long buildId) {
        this.buildId = buildId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof BuildKey){
            BuildKey key = (BuildKey)o;
            if(this.buildId == key.getBuildId() && this.projectName == key.getProjectName()) return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return this.projectName.hashCode();
    }
}
