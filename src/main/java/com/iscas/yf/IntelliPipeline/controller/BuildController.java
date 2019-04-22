package com.iscas.yf.IntelliPipeline.controller;

import com.alibaba.fastjson.JSON;
import com.iscas.yf.IntelliPipeline.entity.*;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepParam;
import com.iscas.yf.IntelliPipeline.entity.record.BuildRecord;
import com.iscas.yf.IntelliPipeline.service.dataservice.BuildRecordService;
import com.iscas.yf.IntelliPipeline.service.dataservice.BuildService;
import com.iscas.yf.IntelliPipeline.service.dataservice.ProjectService;
import com.iscas.yf.IntelliPipeline.service.dataservice.StepService;
import com.iscas.yf.IntelliPipeline.service.decision.DecisionMaker;
import com.iscas.yf.IntelliPipeline.service.util.GitHubRepoService;
import com.iscas.yf.IntelliPipeline.service.util.JenkinsUtils;
import com.iscas.yf.IntelliPipeline.service.util.YamlResolver;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Controller: 采用注解的方式定义该类为处理请求的Controller类
 * @RequestMapping: 用于定义一个请求映射，value是请求的url，method是请求的类型(GET or POST)，
 *      代码中调用printHello方法，即可访问hello/hello
 *
 * 一般用法是，类级别的标注负责将一个特定的请求路径映射到一个控制器上，同时通过方法级别的标注来细化映射的处理逻辑。
 *
 * */

@RequestMapping("/build_data")
@Controller
public class BuildController {

    // log4j - 日志输出
    private static Logger logger = Logger.getLogger(BuildController.class);

    // // Jenkins暴露的地址
    // private static final String JENKINS_URL = "localhost:8888";
    //
    // // 用来测试的项目名
    // private static final String JENKINS_PROJECT_NAME = "JHipster-pipeline-test";
    //
    // // 使用username跟API_TOKEN获得访问Jenkins的权限
    // private static final String JENKINS_USERNAME = "frank";
    // private static final String JENKINS_USER_TOKEN = "4e40d2cc6a3309427e69754dbf4d381a";
    //
    // // 项目专属的Token
    // private static final String JENKINS_JOB_TOKEN = "iscas_yf";

    private Build build;

    private BuildRecord record;
    // 从yaml文件中读取Stage List
    // private List<Step> steps = YamlResolver.testResolving("/home/workplace/sample.yaml");

    @Autowired
    ProjectService projectService;

    @Autowired
    BuildService buildService;

    @Autowired
    StepService stepService;

    @Autowired
    ServletContext servletContext;

    @Autowired
    BuildRecordService recordService;

    /**
     * 开始一次构建, 将projectName作为URL参数传递
     * 通过访问localhost:8180/IntelliPipeline/build?projectName=YOUR_PROJECT_NAME
     * 开始一次构建
     * */
    @Deprecated
    @RequestMapping(value = "/execute", method = RequestMethod.GET)
    private String startBuild(@RequestParam(value="projectName") String projectName){

        // // 注意远程API的格式
        // String remoteUrl = "http://" + JENKINS_USERNAME + ":" + JENKINS_USER_TOKEN
        //         + "@" + JENKINS_URL + "/job/" + projectName + "/build?token=" + JENKINS_JOB_TOKEN;
        //
        // // 在数据库中查找项目
        // Project project = projectService.getProjectByName(projectName);
        //
        // // 当数据库查找不到时, 需要一个统一的错误处理
        // if(project == null){
        //     // TODO: 项目不存在，跳转到所有项目的列表页面
        // }
        //
        // // 通过Java HttpClient触发构建
        // CloseableHttpClient httpClient = HttpClients.createDefault();
        //
        // HttpPost httpPost = new HttpPost(remoteUrl);
        //
        // try{
        //     // 执行POST请求
        //     CloseableHttpResponse response = httpClient.execute(httpPost);
        //
        //     // 得到响应的内容
        //     HttpEntity entity = response.getEntity();
        //
        //     // 打印响应的状态码
        //     System.out.println("Status: " + response.getStatusLine());
        //
        //     // 打印响应内容
        //     if(entity != null) {
        //         System.out.println("Response content: " + EntityUtils.toString(entity));
        //     }
        //
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        // return flow.jsp
        return "flow";
    }


    /**
     * 注解ResponseBody表示该方法的返回结果直接写入HTTP response body中，一般在异步获取数据时使用
     * ，此时程序自动将返回的任意对象值转化成json格式数据
     * 一般有RequestMapping注解的时候，返回值通常解析为跳转路径，加上ResponseBody后不会解析为跳转路径
     * 而是直接写入HTTP response body中！
     * @Param stepNumber: 标示当前执行的step的序号，根据这个序号从stage对象中获取step来执行
     * */
    @ResponseBody
    @RequestMapping(path = "/upload", method = RequestMethod.POST, produces = "application/json")
    private Response executor(@RequestBody Map<String, String> request) throws Exception{
        /**
         * 返回的是一个Response类型的变量，由于ResponseBody注解，Response被自动转换为Json格式的返回
         * */
        logger.info("POST method");

        Response res = new Response();

        logger.info("Received request:" + request.toString());

        // request的内容解析＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        String requestType = request.get("requestType");
        String buildNumber = request.get("buildNumber");

        // 获取/WEB-INF/resources/ 本地路径
        String rootPath = servletContext.getRealPath("/WEB-INF/resources/");

        if(requestType.equals("FAILURE")) {
            this.build.changeStatusToFail();
            buildService.saveBuild(this.build);
            JenkinsUtils.getConsoleOutput(this.build.getProject().getProjectName(), buildNumber, rootPath);
            res.setDecisionType("END");
            return res;
        }

        String projectName = request.get("jobName");

        // step的序号
        int stepNumber = Integer.parseInt(request.get("stepNumber"));


        // currentResult 是Jenkins返回的目前构建结果
        String currentResult = request.get("currentResult");
        logger.info("Current result: " + currentResult);
        logger.info("Request type: " + requestType);

        // 第一次收到Jenkins发来的请求, 标识为START
        if(requestType.equals("START")) {
            // 根据发送过来的jobName找到目标项目
            Project curProject = projectService.getProjectByName(projectName);

            // 如果最新的build处于RUNNING状态, 说明是用户手动启动的构建, 不必再新建一个构建对象.
            if(curProject != null && curProject.getLatestBuild() != null){
                Build latestBuild = curProject.getLatestBuild();
                if(curProject.getLatestBuild() != null && latestBuild.getStatus().equals(Build.Status.RUNNING)){
                    // 为当前的controller保存build对象
                    this.build = latestBuild;
                }
                // 否则是由远程钩子触发的构建, 需要新建一个Build对象并执行
                else {
                    // 根据项目和最新一次构建创建新的build. 执行的流程与上次构建相同
                    Build newBuild = new Build(curProject, latestBuild);

                    curProject.addBuild(newBuild);

                    // 为当前的controller保存build对象
                    this.build = newBuild;

                    // 数据库中先保存build
                    this.build = buildService.saveBuild(this.build);
                    // 数据库中更新项目, 同时级联保存build和其他对象?
                    projectService.saveProject(curProject);
                }
            }
        }

        List<Step> steps = this.build.getSteps();

        // 发生错误, 直接将本次构建结束
        if(steps.size() == 0){
            logger.error("null steps, abort");
            res.setDecisionType("END");
            return res;
        }

        String decision = "";

        // 在这里获取两次构建之间的changeSet
        Git git = DecisionMaker.getGit(rootPath, this.build);
        // 得到对比的数据集
        Map<String, String> analysis = GitHubRepoService.compareLocalAndRemote(git);

        // 保存Build对应的Record, 如果已经有了Record就不再重复新建, 否测JPA会报错
        if(this.build.getRecord() == null && requestType.equals("START")) {
            logger.info("Attention: Create a new record!!!");
            record = new BuildRecord(this.build, analysis);
            this.build.setRecord(record);
            // recordService.createBuildRecord(record);
        }

        switch (requestType) {
            case "START":
                // 开始构建, Jenkins先执行从git仓库获取最新代码的step. 这个Step静默执行, 不在本地留下记录
                Step gitStep = new Step("git");
                StepParam param = new StepParam("url", this.build.getProject().getGitURL());
                gitStep.getStepParams().add(param);

                res.setNameAndParams(gitStep);
                res.setDecisionType("RETRY");

                // 这里直接返回res结果, 不需要后续的判断
                return res;
            case "INIT":
                // 如果decision==END, build的状态会被改为SKIPPED, 一些Step也会被改为SKIPPED
                decision = DecisionMaker.getInitDecision(request, this.build, analysis, git);
                break;
            case "FAILURE":
                // 将本次构建以及构建执行中的step状态改为FAIL
                this.build.changeStatusToFail();
                decision = "END";

                // TODO: 加上发送Email通知的步骤?

                break;
            default:
                decision = DecisionMaker.getRuntimeDecision(request, this.build);

        }

        // 被switch改写的部分
        // // 开始构建, Jenkins先执行从git仓库获取最新代码的step. 这个Step静默执行, 不在本地留下记录
        // if(requestType.equals("START")){
        //     Step gitStep = new Step("git");
        //     StepParam param = new StepParam("url", this.build.getProject().getGitURL());
        //     gitStep.getStepParams().add(param);
        //
        //     res.setNameAndParams(gitStep);
        //     res.setDecisionType("RETRY");
        //
        //     // 这里直接返回res结果, 不需要后续的判断
        //     return res;
        // }
        // // 注意要在构建过程中使用了Git step, 才会有commitSet!
        // else if(requestType.equals("INIT")) {
        //     // 如果decision==END, build的状态会被改为SKIPPED, 一些Step也会被改为SKIPPED
        //     decision = DecisionMaker.getInitDecision(request, this.build, analysis, git);
        // }
        // else if(requestType.equals("FAILURE")){
        //     // 将本次构建以及构建所有的step状态都改为FAIL
        //     this.build.changeStatusToFail();
        //     decision = "END";
        // }
        // else {
        //     decision = DecisionMaker.getRuntimeDecision(request, this.build);
        // }

        logger.info("Decision: " + decision);

        String durationTime = request.get("durationTime");

        // 这个函数实时获取控制台输出并且以log文件形式保存到本地
        String logPath = JenkinsUtils.getConsoleOutput(this.build.getProject().getProjectName(), buildNumber, rootPath);
        logger.info("Parsed logPath: " + logPath);

        // 将日志文件的地址存入build对象中
        this.build.setConsoleOutputFilePath(logPath);

        this.build.setDurationTime(durationTime);

        // TODO: 解耦变成一个有步骤被执行且上一个步骤成功, 根据执行结果更新前一个step的状态.
        if(stepNumber <= steps.size() + 1 && !decision.equals("END")) {
            if(currentResult.equals("SUCCESS") && stepNumber != 1) {
                Step preStep = steps.get(stepNumber - 2);
                if(!preStep.getStatus().equals(Step.Status.SKIPPED)) {
                    preStep.setStatus(Step.Status.SUCCESS);
                    stepService.modifyStep(preStep);
                }
            }
            // 上个步骤失败
            else if(currentResult.equals("FAILURE") && stepNumber != 1) {
                Step preStep = steps.get(stepNumber - 2);
                preStep.setStatus(Step.Status.FAIL);
                stepService.modifyStep(preStep);
            }
        } else if(decision.equals("END") && !this.build.getStatus().equals(Build.Status.FAIL)){
            // END, 执行结束
            Step preStep = steps.get(steps.size() - 1);
            preStep.setStatus(Step.Status.SUCCESS);
            stepService.modifyStep(preStep);
        }
        // else if(decision.equals("END") && this.build.getStatus().equals(Build.Status.FAIL)) {
        //     // END, 执行结束, 且是以失败的方式结束, 保持所有step状态为失败
        // }


        // 运行时决策获取
        // if(requestType.equals("RUNNING") && currentResult.equals("FAILURE")){
        //     decision = DecisionMaker.getRuntimeDecision(request, this.build);
        // }

        res.setDecisionType(decision);

        // 不等于END的情况下才获取step
        if(decision.equals("END") && !this.build.getStatus().equals(Build.Status.SKIPPED)
                && !this.build.getStatus().equals(Build.Status.ABORTED)
                && !this.build.getStatus().equals(Build.Status.FAIL)){
            // 不等于SKIPPED\ABORTED\FAIL的情况下才判定执行成功结束
            this.build.changeStatusToSucceed();
        } else if(decision.equals("SKIP_BUILD")) {
            // 跳过构建的决策, build以及steps都已经被置为SKIPPED, 且加入了发送mail的步骤(队列末尾)
            // 更新mailStep, 相当于给mailStep一个在数据库中独一无二的id, 避免该step对象被存储多次
            Step mailStep = stepService.modifyStep(this.build.getSteps().get(steps.size() - 1));
            res.setNameAndParams(mailStep);
        } else if(decision.equals("NEXT")){
            // 继续执行构建
            Step exeStep = steps.get((stepNumber - 1));
            // 要跳过某些step应该是INIT的时候就已做好决策
            if(exeStep.getStatus().equals(Step.Status.SKIPPED)){
                res.setDecisionType("SKIP_STEP");
            } else {
                exeStep.setStatus(Step.Status.RUNNING);
                res.setNameAndParams(exeStep);
            }
        } else if(decision.equals("END") && this.build.getStatus().equals(Build.Status.FAIL)) {
            // 当前构建状态为失败, 判定结束构建.

        }

        // 这时再更新一次控制台输出日志
        JenkinsUtils.getConsoleOutput(this.build.getProject().getProjectName(), buildNumber, rootPath);

        // 更新数据库中的build, 同时也更新了Step的状态, BuildRecord的状态
        buildService.saveBuild(this.build);

        logger.info("Response: " + JSON.toJSONString(res));

        return res;
    }

    /**
     * 接收到build对象之后, 触发Jenkins的远程构建, 并且为Controller的build对象赋值
     * 如果以后要考虑并发, controller加上@Scope("session")注解.
     * */
    @ResponseBody
    @RequestMapping(path = "/trigger/{buildId}", method = RequestMethod.GET)
    private boolean triggerJenkins(@PathVariable Long buildId) throws Exception{
        logger.info("trigger method");

        this.build = buildService.getBuild(buildId);

        logger.info("this.build.steps.size(): " + build.getSteps().size());

        Project project = build.getProject();

        String projectName = project.getProjectName();

        boolean result = JenkinsUtils.executeBuild(projectName);

        if(result) logger.info("启动项目成功");
        else logger.info("Build launch failed");

        return result;
    }


    /**
     * 服务器端通过Ajax请求的方式从本地获取log文件数据
     * */
    @ResponseBody
    @RequestMapping(path = "/logFile/{projectName}/{fileName}", method = RequestMethod.GET)
    private String getLogContent(@PathVariable String projectName, @PathVariable String fileName) throws Exception{
        fileName = fileName + ".log";
        File logFile = new File(servletContext.getRealPath("/WEB-INF/resources/")
                + "LocalRepo/" + projectName + "/" + fileName);
        logger.info("Path: " + logFile.getCanonicalPath());
        Long fileLen = logFile.length();
        byte[] fileContent = new byte[fileLen.intValue()];
        if(logFile.exists()){

            FileInputStream in = new FileInputStream(logFile);
            in.read(fileContent);
            in.close();
        } else {
            return "Log File Not Exists!";
        }

        return new String(fileContent, "UTF-8");
    }

    /**
     * 返回重绘构建流程图的build对象(Graph形式)
     * */
    @ResponseBody
    @RequestMapping(path = "/get/{buildId}", method = RequestMethod.GET)
    private BuildGraph getExecutingBuild(@PathVariable Long buildId){
        Build build = buildService.getBuild(buildId);
        // build包含信息过多, 转化为Graph, 只包含必要的信息
        return build.transformBuildToGraph();
    }

    @ResponseBody
    @RequestMapping(path = "/stop/{buildId}", method = RequestMethod.GET)
    private Build stopBuildProcess(@PathVariable Long buildId){
        Build build = buildService.getBuild(buildId);
        // 将构建状态改为Aborted, 同时在数据库中更新
        build.changeStatusToAborted();

        // TODO: 停止正在执行的Jenkins任务

        // 保存的是这个函数里获得的build对象
        build = buildService.saveBuild(build);
        return build;
    }

    // @ResponseBody
    // @RequestMapping(path = "/upload", method = RequestMethod.GET, produces = "application/json")
    // public Response getScript(@RequestParam(value="stageNumber")String stepNumber) {
    //
    //     logger.info("GET method");
    //
    //     // 构建Response的内容
    //     Step curStep = mockFlow.steps.get(Integer.parseInt(stepNumber));
    //     assert curStep != null;
    //
    //     String decisionType = DecisionMaker.getDecision("Something");
    //
    //     Response res = new Response();
    //     res.setDecisionType(decisionType);
    //     res.setExecutionStep(curStep);
    //
    //     logger.info("stepName: " + curStep.getStepName());
    //     logger.info("stepParams: " + JSON.toJSONString(curStep.getStepParams()));
    //
    //     // String resJson = JSON.toJSONString(res);
    //
    //     logger.info("Response will be return: ");
    //     logger.info(res);
    //
    //     // 自动将Response转换为Json格式?
    //     return res;
    // }


    // main函数是流程的入口，首先通过REST API发送请求开始构建
    // public static void main(String[] args){
    //     // 注意远程API的格式
    //     String remoteUrl = "http://" + JENKINS_USERNAME + ":" + JENKINS_USER_TOKEN
    //             + "@" + JENKINS_URL + "/job/" + JENKINS_PROJECT_NAME + "/build?token=" + JENKINS_JOB_TOKEN;
    //
    //
    //     // Project project = Project
    //     // 通过Java HttpClient触发构建
    //     CloseableHttpClient httpClient = HttpClients.createDefault();
    //
    //     HttpPost httpPost = new HttpPost(remoteUrl);
    //
    //     try{
    //         // 执行POST请求
    //         CloseableHttpResponse response = httpClient.execute(httpPost);
    //
    //         // 得到响应的内容
    //         HttpEntity entity = response.getEntity();
    //
    //         // 打印响应的状态码
    //         System.out.println("Status: " + response.getStatusLine());
    //
    //         // 打印响应内容
    //         if(entity != null) {
    //             System.out.println("Response content: " + EntityUtils.toString(entity));
    //         }
    //
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //
    // }


}
