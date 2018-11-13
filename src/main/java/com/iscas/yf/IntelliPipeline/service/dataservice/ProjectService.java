package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dataview.ProjectView;
import com.iscas.yf.IntelliPipeline.entity.Project;

import java.util.List;

public interface ProjectService {


    public Project saveProject(Project project);

    /**
     * 创建项目. 需要输入的只是ProjectName还有GitURL
     * @Param view
     * @return project
     * */
    public Project createProject(ProjectView.Item view);

    /**
     * 获取所有项目
     * @Param
     * @return List
     * */
    public List<Project> getAllProjects();

    /**
     * 获取项目详细信息
     * @Param proejctId
     * @return project
     */
    public Project getProject(Long projectId);

    /**
     * 删除项目
     * @Param projectId
     * @return boolean
     * */
    public boolean deleteProject(Long projectId);

    /**
     * 修改项目信息
     *
     * @Param projectId
     * @Param ProjectView
     * @return project
     * */
    public Project modifyProject(Long projectId, ProjectView.Item view);

    public Project getProjectByName(String projectName);
}



