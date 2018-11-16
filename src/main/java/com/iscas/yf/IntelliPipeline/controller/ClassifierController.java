package com.iscas.yf.IntelliPipeline.controller;

import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.Project;
import com.iscas.yf.IntelliPipeline.entity.record.BuildRecord;
import com.iscas.yf.IntelliPipeline.service.classifier.WekaClassifier;
import com.iscas.yf.IntelliPipeline.service.dataservice.BuildService;
import com.iscas.yf.IntelliPipeline.service.dataservice.ProjectService;
import com.iscas.yf.IntelliPipeline.service.decision.DecisionMaker;
import com.iscas.yf.IntelliPipeline.service.util.GitHubRepoService;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "classifier")
public class ClassifierController {
    private static final Logger logger = Logger.getLogger(ClassifierController.class);

    @Autowired
    ProjectService projectService;

    @Autowired
    ServletContext servletContext;

    @Autowired
    BuildService buildService;

    /**
     * 获取分类器预测结果
     * */
    @RequestMapping(value = {"/get/{buildId}"}, method = RequestMethod.GET)
    @ResponseBody
    public String getPrediction(@PathVariable("buildId") Long buildId) throws Exception{

        // 传送过来的参数实际上有两个, 用'-'分割
        // String[] ids = id.split("-");
        // Long projectId = Long.parseLong(ids[0]);
        // Long buildId = Long.parseLong(ids[1]);

        // 预测过程: commit触发构建 -> build对象建立 -> 进行预测 -> 分析配置规则
        // -> 规则和预测结果共同决定构建是否执行
        Build build = buildService.getBuild(buildId);
        BuildRecord record = build.getRecord();

        Project project = build.getProject();
        List<Build> builds = project.getBuilds();

        if(builds.size() == 0) {
            return "unknown";
        }

        String projectName = project.getProjectName();
        // 获取/WEB-INF/resources/ 本地路径
        String rootPath = servletContext.getRealPath("/WEB-INF/resources/");

        // 没有必要获取代码仓库的差异, 因为相关数据已经包含在BuildRecord中
        // Git git = DecisionMaker.getGit(rootPath, latestBuild);
        // Map<String, String> diff = GitHubRepoService.compareLocalAndRemote(git);

        BuildRecord mockRecord = new BuildRecord();
        mockRecord.setModified_lines(50L);
        mockRecord.setCommitter_num(11);

        // 获得实例字符串, 进行预测.
        // String instance = record.toPredictionString();
        String mockInstance = mockRecord.toPredictionString();

        // 存放分类器模型的目录
        String modelPath = rootPath + "LocalRepo/" + projectName + "/model/";
        String modelFile = projectName + ".model";

        // 先查看是否存在模型, 如果不存在就训练并保存模型
        boolean exist = new File(modelPath, modelFile).exists();

        // 模型文件存在, 直接使用
        if(exist) {
            // 模型用项目名保存, 直接读取
            Classifier model = WekaClassifier.loadModel(projectName, modelPath);

            // 返回值介于0-1之间,
            // 越接近0, 说明失败的概率越高; 越接近1, 说明成功的概率越高.
            double prediction = WekaClassifier.predict(model, mockInstance);
            if(prediction >= 0.5 && prediction <= 1.0) {
                return "passed";
            } else if(prediction <= 0.5 && prediction >= 0.0) {
                return "failed";
            }

        } else {
            try {
                // TODO: 否则重新训练模型. 这里获取的数据应该是travistorrent的构建数据.
                // 所以projectName应该是模板项目的项目名
                Instances trainData = WekaClassifier
                        .getInstanceFromDatabase("", "java");

                Classifier model = WekaClassifier.trainModel(trainData);

                // 返回值介于0-1之间,
                // 越接近0, 说明失败的概率越高; 越接近1, 说明成功的概率越高.
                double prediction = WekaClassifier.predict(model, mockInstance);
                if(prediction >= 0.5 && prediction <= 1.0) {
                    return "passed";
                } else if(prediction <= 0.5 && prediction >= 0.0) {
                    return "failed";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return "unknown";
    }

    @RequestMapping(value = {"/show/{projectId}"}, method = RequestMethod.GET)
    @ResponseBody
    public void showClassifierTree(@PathVariable("projectId") Long id) {
        Project project = projectService.getProject(id);
        String projectName = project.getProjectName();
        // 获取/WEB-INF/resources/ 本地路径
        String rootPath = servletContext.getRealPath("/WEB-INF/resources/");

        // 存放分类器模型的目录
        String modelPath = rootPath + "LocalRepo/" + projectName + "/model/";
        String modelFile = projectName + ".model";

        // 先查看是否存在模型, 如果不存在就训练并保存模型
        boolean exist = new File(modelPath, modelFile).exists();

        // 模型文件存在, 直接使用
        if(exist) {
            // 模型用项目名保存, 直接读取
            Classifier model = WekaClassifier.loadModel(projectName, modelPath);

            // 进行预测
            double result = WekaClassifier.predict(model, "");

        } else {
            try {
                // TODO: 否则重新训练模型. 这里获取的数据应该是travistorrent中某个项目的构建数据,
                // 所以projectName应该是模板项目的项目名
                Instances trainData = WekaClassifier.getInstanceFromDatabase("", "");

                Classifier model = WekaClassifier.trainModel(trainData);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
