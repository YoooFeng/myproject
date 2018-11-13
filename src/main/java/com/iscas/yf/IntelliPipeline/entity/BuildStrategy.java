package com.iscas.yf.IntelliPipeline.entity;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "d_build_strategy")
public class BuildStrategy extends IdEntity {

    // 构建时间间隔
    @Column(name = "time_interval")
    private long time_interval;

    // 累积修改的代码行数
    @Column(name = "modified_lines")
    private int modified_lines;

    // 是否修改过模型?比较模糊
    @Column(name = "model_modified")
    private boolean model_modified;

    // separated by ','
    // 关键路径
    @Column(name = "key_paths")
    private String key_paths;

    // 跳过的路径
    @Column(name = "skip_paths")
    private String skip_paths;

    // 提交者的邮箱列表
    @Column(name = "committers_mail")
    private String committers_mail;

    // OneToOne, 策略跟项目是一对一关系
    @OneToOne(targetEntity = Project.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id", unique = true)
    private Project project;

    public BuildStrategy(){

    }


    public long getTime_interval() {
        return time_interval;
    }

    public void setTime_interval(long timeInterval) {
        this.time_interval = timeInterval;
    }

    public int getModified_lines() {
        return modified_lines;
    }

    public void setModified_lines(int modifiedLOC) {
        this.modified_lines = modifiedLOC;
    }

    public boolean isModel_modified() {
        return model_modified;
    }

    public void setModel_modified(boolean modelModified) {
        this.model_modified = modelModified;
    }

    public String getCommitters_mail() {
        return committers_mail;
    }

    public void setCommitters_mail(String committersMail) {
        this.committers_mail = committersMail;
    }

    public String getKey_paths() {
        return key_paths;
    }

    public void setKey_paths(String keyPaths) {
        this.key_paths = keyPaths;
    }

    public String getSkip_paths() {
        return skip_paths;
    }

    public void setSkip_paths(String skipPaths) {
        this.skip_paths = skipPaths;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
