package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.Project;

import java.util.List;

public interface BuildService {

    // 获取某个Build
    public Build getBuild(Long buildId);

    // 从project中获取所有的build记录
    public List<Build> getAllBuilds(Long project_id);

    // 更改build的状态
    public void updateBuildStatus(Long buildId, Build.Status status);

    // 在数据库中保存build记录
    public Build saveBuild(Build build);

}
