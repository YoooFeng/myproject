package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dao.*;
import com.iscas.yf.IntelliPipeline.dataview.ProjectView;
import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.BuildGraph;
import com.iscas.yf.IntelliPipeline.entity.Project;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import com.iscas.yf.IntelliPipeline.service.util.JenkinsUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 实现了ProjectService
@Service
public class ProjectServiceImpl implements ProjectService{
    private Logger logger = Logger.getLogger(ProjectServiceImpl.class);

    // 所有用到的数据交互相关
    @Autowired
    private ProjectDAO projectDAO;
    @Autowired
    private StepDAO stepDAO;
    @Autowired
    private StepParamDAO stepParamDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private BuildDAO buildDAO;

    /**
     * 获取所有Project列表
     * */
    @Override
    public List<Project> getAllProjects(){
        return (List<Project>) projectDAO.findAll();
    }

    /**
     * 新建Project. 通过获取view中的数据进行保存.
     * */
    @Override
    public Project createProject(ProjectView.Item view){
        logger.info("creating project for " + view.name);

        // 去掉多余的双引号
        view.name = view.name.replace("\"", "");
        view.gitUrl = view.gitUrl.replace("\"", "");

        boolean jenkinsResponse = true;

        // 尝试在Jenkins中创建项目
        try{
            jenkinsResponse = JenkinsUtils.createProject(view.name, view.gitUrl);
        } catch (Exception e){
            e.printStackTrace();
        }

        // 如果Jenkins中创建失败, 那么服务端也不会创建新项目
        if(!jenkinsResponse) return null;
        else {
            Project project = new Project(view.name);
            project.setGitURL(view.gitUrl);
            project.setStatus(Project.Status.STABLE);
            // project.setOwner();

            projectDAO.save(project);
            return project;
        }
    }

    @Override
    public Project getProject(Long projectId){
        Project project = projectDAO.findOne(projectId);
        // if(project.getBuilds() != null){
        //     for(Build build : project.getBuilds()){
        //         getInstanceOf
        //     }
        // }
        return project;
    }

    @Override
    public boolean deleteProject(Long projectId){
        Project project = projectDAO.findOne(projectId);
        // 删除数据库中所有相关的build
        buildDAO.delete(project.getBuilds());
        // 再删除项目
        projectDAO.delete(projectId);
        return true;
    }

    /**
     * 修改Project之后不需要存储起来? 或者执行update类似的操作?
     * */
    @Override
    public Project modifyProject(Long projectId, ProjectView.Item view){
        logger.info("modify project");
        Project project = projectDAO.findOne(projectId);
        project.setProjectName(view.name);
        project.setGitURL(view.gitUrl);

        return project;
    }

    @Override
    public Project saveProject(Project project){
        projectDAO.save(project);
        for(Build build : project.getBuilds()){
            logger.info("save build" + build.getLatestCommitId() + ".");
            buildDAO.save(build);
        }
        return project;
    }

    public void getInstanceOfBuild(Build build){

    }

    public List<Step> transGraph(BuildGraph graph){
        List<Step> steps = graph.transformGraphToBuild();
        return steps;
    }

    public Project getProjectByName(String projectName){
        // 先全部找出来
        Iterable<Project> projects = projectDAO.findAll();
        for(Project project : projects){
            // 然后一个一个对比
            if(project.getProjectName().equals(projectName)){
                return project;
            }
        }
        return null;
    }
}
