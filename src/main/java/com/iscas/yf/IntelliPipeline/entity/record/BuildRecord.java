package com.iscas.yf.IntelliPipeline.entity.record;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.Project;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "d_record")
public class BuildRecord extends IdEntity{

    // 在一张大表中对构建的各项特征进行保存

    // 与Build对象是一对一的关系, 直接使用build保存的一些数据来计算
    @OneToOne(targetEntity = Build.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "build_id", unique = true)
    private Build build;

    // 保存的特征, 当有新的构建数据加入时需要动态计算
    @Column(name = "last_build")
    private Build.Status last_build;

    @Column(name = "committer_recent")
    private Long committer_recent;

    @Column(name = "committer_history")
    private Long committer_history;

    // 开发者在当前项目的开发经验
    @Column(name = "committer_exp")
    private Long committer_exp;

    // 项目最近的构建成功率
    @Column(name = "project_recent")
    private Long project_recent;

    // 项目的历史构建成功率(只计数成功\失败两类构建)
    @Column(name = "project_history")
    private Long project_history;

    // 记录本次构建的相关提交者, 多个提交者用空格分割
    @Column(name = "committer")
    private String committer;

    // 记录本次构建被修改的代码行数
    @Column(name = "modified_lines")
    private Long modified_lines;

    // Non-arg constructor
    public BuildRecord() {

    }

    // 构造函数, 生成BuildRecord的时候为其绑定对应的构建
    public BuildRecord(Build build,  Map<String, String> analysis) {
        this.build = build;

        Project project = build.getProject();
        List<Build> builds = project.getBuilds();

        // 保存当前构建累积变更的作者列表
        if(analysis.get("Authors") != null) {
            this.committer = analysis.get("Authors");
        } else this.committer = null;

        // 保存本次构建累积的代码修改行数
        if(analysis.get("ModifiedLines") != null
                && !analysis.get("ModifiedLines").equals("0")) {
            // 将String转化为Long
            this.modified_lines = Long.parseLong(analysis.get("ModifiedLines"));
        }

        // 上一次构建的结果
        if(builds.size() > 0) {
            // 获取当前项目倒数第二个构建的状态
            this.last_build = builds.get(builds.size() - 2).getStatus();
        } else this.last_build = null;

        // project_recent - 该项目最近几次构建的成功率

        // project_history - 该项目历史构建的成功率
        if(builds.size() > 0) {
            int total = 0;
            int succeess = 0;
            int fail = 0;

            for(Build b : builds) {
                if(b.getStatus().equals(Build.Status.FAIL)) fail += 1;
                else if(b.getStatus().equals(Build.Status.SUCCEED)) succeess += 1;
                else continue;
                total += 1;
            }

            this.project_history = (long)succeess / (long)total;

        } else this.project_history = 0L;

    }

    // getter and setter
    public Long getModified_lines() {
        return modified_lines;
    }

    public void setModified_lines(Long modified_lines) {
        this.modified_lines = modified_lines;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        // 将changeSet和build中的特征计算之后保存在数据库中
        this.build = build;

    }

    // 获取上一次构建的结果并保存
    public Build.Status getLast_build() {
        return last_build;
    }

    // 记录上一次构建的结果
    public void setLast_build(Build.Status last_build) {
        this.last_build = last_build;
    }

    public Long getCommitter_recent() {
        return committer_recent;
    }

    // 统计该提交者最近的提交情况
    public void setCommitter_recent(Long committer_recent) {
        this.committer_recent = committer_recent;
    }

    public Long getCommitter_history() {
        return committer_history;
    }

    // 统计该提交者整个开发进程中的提交情况
    public void setCommitter_history(Long committer_history) {
        this.committer_history = committer_history;
    }

    public Long getCommitter_exp() {
        return committer_exp;
    }

    // 计算开发者的开发经验, 取幂值
    public void setCommitter_exp(Long committer_exp) {
        this.committer_exp = committer_exp;
    }

    public Long getProject_recent() {
        return project_recent;
    }

    // 最近项目的构建成功率, 先group by project_name 然后按照构建的编号来获取
    public void setProject_recent(Long project_recent) {
        this.project_recent = project_recent;
    }

    public Long getProject_history() {
        return project_history;
    }

    // 项目构建的总成功率
    public void setProject_history(Long project_history) {
        this.project_history = project_history;
    }

    // 保存到数据库之前将所需的参数在本函数中计算完成
    public void doCalculate() {

        if(this.build == null) {
            return;
        }

        // 得到Project对象
        Project project = this.build.getProject();

        // 获取所有构建 - builds
        List<Build> builds = project.getBuilds();

        // 第一次构建, 只有当前这个构建记录
        if(builds.size() == 1) {

            // 没有上一次构建, 赋值为null
            this.last_build = null;

            // TODO: 通过Build记录获取所有的committer
            this.committer = null;
        }
    }

}
