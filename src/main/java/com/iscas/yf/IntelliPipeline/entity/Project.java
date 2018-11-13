package com.iscas.yf.IntelliPipeline.entity;

import com.google.common.collect.ImmutableList;
import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "d_project")
public class Project extends IdEntity {
    @Column(name = "project_name")
    private String projectName;

    @Column
    private String gitURL;

    @Column
    private String owner;

    // 一个项目可能有多次builds
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
    private List<Build> builds;

    @Enumerated(EnumType.STRING)
    private Status status;

    // mapped by 自己
    @OneToOne(targetEntity = BuildStrategy.class, mappedBy = "project", cascade = CascadeType.ALL)
    private BuildStrategy strategy;

    // 对当前的项目构建状态进行标注, 定义如何？
    public enum Status{
        STABLE, UNSTABLE, BUILDING;
        public static Status of(String input){
            if(input != null) {
                for(Status res : values()){
                    if(res.name().equalsIgnoreCase(input)) return res;
                }
            }
            return null;
        }
    }

    // 空构造函数
    @SuppressWarnings("unused")
    protected Project(){
        this.builds = new ArrayList<>();
    }

    // 单个参数的构造函数, 只需要提供项目名
    public Project(String projectName){
        this.projectName = projectName;
        this.builds = new ArrayList<>();
    }

    // 构造函数, 需要输入project名称、对应的git仓库地址以及用户名
    public Project(String projectName, String gitURL, String owner){
        this.projectName = projectName;
        this.gitURL = gitURL;
        this.owner = owner;
        this.builds = new ArrayList<>();
        this.status = Status.STABLE;
    }


    // Getter and setter
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getGitURL() {
        return gitURL;
    }

    public void setGitURL(String gitURL) {
        this.gitURL = gitURL;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<Build> getBuilds() {
        return ImmutableList.copyOf(builds);
    }

    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BuildStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(BuildStrategy strategy) {
        this.strategy = strategy;
    }

    // add build
    public void addBuild(Build build){
        builds.add(build);
    }

    // add builds
    public void addBuilds(List<Build> builds){
        this.builds.addAll(builds);
    }

    // change status to stable
    public void changeStatusToStable(){
        this.status = Status.STABLE;
    }

    // change status to unstable
    public void changeStatusToUnstable(){
        this.status = Status.UNSTABLE;
    }

    // change status to building
    public void changeStatusToBuilding(){
        this.status = Status.BUILDING;
    }

    public Build getLatestBuild(){
        if(this.builds.size() == 0) return null;

        int size = this.getBuilds().size();
        // 返回最新的一次构建
        if(size != 0){
            return this.getBuilds().get(size - 1);
        } else {
            return null;
        }

    }
}
