package com.iscas.yf.IntelliPipeline.controller;

import com.iscas.yf.IntelliPipeline.common.controller.BaseController;
import com.iscas.yf.IntelliPipeline.dao.StepDAO;
import com.iscas.yf.IntelliPipeline.dataview.ProjectView;
import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.BuildGraph;
import com.iscas.yf.IntelliPipeline.entity.Project;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Relation;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepParam;
import com.iscas.yf.IntelliPipeline.service.dataservice.BuildService;
import com.iscas.yf.IntelliPipeline.service.dataservice.ProjectService;
import com.iscas.yf.IntelliPipeline.service.dataservice.StepService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * REST接口
 * 返回REST数据，包括创建、删除、展示、修改项目
 * 返回的不是具体的页面, 而是页面对应的数据,
 * 具体的页面跳转等控制器查看views包下对应名称的controller
 * */
@Controller
@RequestMapping(value = "/project_data")
public class ProjectController {

    private Logger logger = Logger.getLogger(ProjectController.class);

    /**
     * 注入Project数据库CRUD服务
     * */
    @Autowired
    private ProjectService projectService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private StepService stepService;

    /**
     * 新建项目. 传过来的不是一个Project对象, 是一堆Item, 即Project中的属性
     * url - http://localhost:8180/IntelliPipeline/project_data/new
     * */
    @RequestMapping(value="/new", method = RequestMethod.POST)
    @ResponseBody
    // @Transactional
    public ProjectView.Item createProject(@RequestBody ProjectView.Item projectView){
        return ProjectView.viewOf(projectService.createProject(projectView));
    }

    /**
     * 获取项目详细信息
     * @Param id
     * @return
     * */
    @RequestMapping(value={"/get/{projectId}"}, method = RequestMethod.GET)
    @ResponseBody
    public ProjectView.DetailedItem getProject(@PathVariable("projectId") Long id){
        Project project = projectService.getProject(id);
        return ProjectView.detailedViewOf(project);
    }

    /**
     * 获取项目列表
     * @Param projectId
     * @Param projectView
     * @return
     *
     * http://localhost:8180/IntelliPipeline/project_data/list
     * */
    @ResponseBody
    @RequestMapping(value="/list", method = RequestMethod.GET)
    public List<ProjectView.DetailedItem> getProjects(){
        logger.info("GET http://localhost:8180/IntelliPipeline/project_data/list");
        List<Project> projects = projectService.getAllProjects();
        // for(Project project : projects) logger.info(project.getProjectName());

        return ProjectView.detailedViewListOf(projects);
    }

    /**
     * 修改项目
     *
     * @Param projectId
     * @Param projectView
     * @reutrn
     * */
    @RequestMapping(value = {"/modify/{projectId}"}, method = RequestMethod.PUT)
    @ResponseBody
    public ProjectView.Item modifyProject(
            @PathVariable("projectId") Long projectId,
            @RequestBody ProjectView.Item projectView){
        return ProjectView.viewOf(projectService.modifyProject(projectId, projectView));
    }

    /**
     * 删除项目
     *
     * @Param projectId
     * @return
     * */
    @RequestMapping(value = {"/delete/{projectId}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteProject(@PathVariable("projectId") Long projectId) {
        boolean success = projectService.deleteProject(projectId);
        return BaseController.DResponseBuilder.instance()
                .add("result", success ? "success":"fail")
                .build();
    }

    /**
     * 项目执行构建的函数
     * @Param BuildGraph POST发送过来的数据, 会自动装箱转换成一个BuildGraph对象
     * @Param id project的Id, 当前构建的主体
     * @return Build
     *
     * url: http://localhost:8180/IntelliPipeline/project_data/build/{projectId}
     * */
    @RequestMapping(value = {"/build/{projectId}"}, method = RequestMethod.POST)
    @ResponseBody
    // 自动装箱将传输过来的JSON字符串转化为graph对象
    public Long executeBuild(@PathVariable("projectId")Long id , @RequestBody BuildGraph graph){

        // 没有得到数据, 转化失败
        if(graph.transformGraphToBuild() == null) return null;

        Project project = projectService.getProject(id);

        // 转化成build对象
        Build build = new Build();
        build.setProject(project);
        build.setSteps(graph.transformGraphToBuild());

        List<Step> steps = build.getSteps();

        List<Step> sortedSteps = new ArrayList<>();

        // 找到没有inRelation的step, 作为队列中第一个step
        for(Step step : steps){
            if(step.getInRelations() == null || step.getInRelations().size() == 0){
                sortedSteps.add(step);
                break;
            }
        }

        // TODO : 按照Relation关系将step插入链表, 目前还不支持并行和多点连线!
        Step firstStep = sortedSteps.get(sortedSteps.size() - 1);

        while(firstStep.getOutRelations() != null && firstStep.getOutRelations().size() != 0 &&  firstStep.getOutRelations().get(0).getToId() != null) {
            for(Relation re : firstStep.getOutRelations()) {
                String nextNodeId = re.getToId();
                for (Step step : steps) {
                    if (step.getNodeId().equals(nextNodeId)) {
                        sortedSteps.add(step);
                        break;
                    }
                }
            }
            firstStep = sortedSteps.get(sortedSteps.size() - 1);
        }

        // 注意这里要为每个step建立跟build的关联, 并且将step状态设置为SUSPENDED
        for(Step step : sortedSteps){
            step.setStatus(Step.Status.SUSPENDED);
            step.setBuild(build);
            for(StepParam param : step.getStepParams()){
                param.setStep(step);
            }
        }

        // 查看是否排序成功
        logger.info("SortedStep size: " + sortedSteps.size());

        build.setSteps(sortedSteps);

        // 将build的状态设置为RUNNING
        build.changeStatusToRunning();

        // 把build存储到数据库中
        buildService.saveBuild(build);

        return build.getId();
    }

}










