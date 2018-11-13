package com.iscas.yf.IntelliPipeline.controller;

import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.Project;
import com.iscas.yf.IntelliPipeline.service.classifier.WekaClassifier;
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

    /**
     * 获取分类器预测结果
     * */
    @RequestMapping(value = {"/get/{projectId}"}, method = RequestMethod.GET)
    @ResponseBody
    public String getPrediction(@PathVariable("projectId") Long id) throws Exception{

        Project project = projectService.getProject(id);
        List<Build> builds = project.getBuilds();

        if(builds.size() == 0) {
            return "unknown";
        }

        Build latestBuild = builds.get(builds.size() - 1);

        String projectName = project.getProjectName();
        // 获取/WEB-INF/resources/ 本地路径
        String rootPath = servletContext.getRealPath("/WEB-INF/resources/");

        // 获取代码仓库的差异
        Git git = DecisionMaker.getGit(rootPath, latestBuild);
        Map<String, String> diff = GitHubRepoService.compareLocalAndRemote(git);



        // 存放分类器模型的目录
        String modelPath = rootPath + "LocalRepo/" + projectName + "/model/";
        String modelFile = projectName + ".model";

        // 先查看是否存在模型, 如果不存在就训练并保存模型
        boolean exist = new File(modelPath, modelFile).exists();

        // 模型文件存在, 直接使用
        if(exist) {
            // 模型用项目名保存, 直接读取
            Classifier model = WekaClassifier.loadModel(projectName, modelPath);



        } else {
            try {
                // TODO: 否则重新训练模型. 这里获取的数据应该是travistorrent的构建数据.
                // 所以projectName应该是模板项目的项目名
                Instances trainData = WekaClassifier
                        .getInstanceFromDatabase("", "");

                Classifier model = WekaClassifier.trainModel(trainData);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return "";
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
            String result = WekaClassifier.predict(model, "");

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
