package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dao.BuildDAO;
import com.iscas.yf.IntelliPipeline.dao.ProjectDAO;
import com.iscas.yf.IntelliPipeline.dao.StepDAO;
import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildServiceImpl implements BuildService{

    @Autowired
    BuildDAO buildDAO;

    @Autowired
    ProjectDAO projectDAO;

    @Autowired
    StepDAO stepDAO;

    public Build getBuild(Long buildId){
        return buildDAO.findOne(buildId);
    }

    // 从project中获取所有的build记录
    public List<Build> getAllBuilds(Long projectId){
        Project project = projectDAO.findOne(projectId);
        return project.getBuilds();
    }

    // 更改build的状态
    public void updateBuildStatus(Long buildId, Build.Status status){
        Build build = buildDAO.findOne(buildId);
        build.setStatus(status);
        buildDAO.save(build);
    }

    // 在数据库中保存build记录, 同时也把relation等关系保存下来
    public Build saveBuild(Build build){
        buildDAO.save(build);
        return build;
    }
}
