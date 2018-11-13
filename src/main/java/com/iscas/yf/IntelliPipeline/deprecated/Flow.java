package com.iscas.yf.IntelliPipeline.deprecated;


import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import com.iscas.yf.IntelliPipeline.deprecated.Stage;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Flow: 存储用户自定义建模过程的数据结构
 * @steps: 只按顺序存放要执行的step的name，不包括step的参数
 * */
@Deprecated
public class Flow extends IdEntity {

    // 一个flow包含许多steps
    public List<Step> steps = new ArrayList<>();

    // 一个flow包含多个stages
    private List<Stage> stages = new ArrayList<>();

    // 没有参数的构造函数
    public Flow(){

    }



    // 构造函数，先mock几个step在里面.需要声明为public才能通过new关键字直接创建该类对象。
    // 是否考虑将flow类设计为单例模式？
    // public Flow(String projectName){
    //
    //     if(projectName.equals("JHipster")){
    //
    //         Stage stage1 = new Stage("check java");
    //         Stage stage2 = new Stage("clean");
    //         Stage stage3 = new Stage("install tools");
    //         Stage stage4 = new Stage("yarn install");
    //         Stage stage5 = new Stage("backend tests");
    //         Stage stage6 = new Stage("frontend tests");
    //         Stage stage7 = new Stage("packaging");
    //
    //
    //         Step step1 = new Step();
    //         Step step2 = new Step();
    //         Step step3 = new Step();
    //         Step step4 = new Step();
    //         Step step5 = new Step();
    //         Step step6 = new Step();
    //         Step step7 = new Step();
    //         Step step8 = new Step();
    //
    //         Map<String, Object> params1 = new HashMap<>();
    //         Map<String, Object> params2 = new HashMap<>();
    //         Map<String, Object> params3 = new HashMap<>();
    //         Map<String, Object> params4 = new HashMap<>();
    //         Map<String, Object> params5 = new HashMap<>();
    //         Map<String, Object> params6 = new HashMap<>();
    //         Map<String, Object> params7 = new HashMap<>();
    //         Map<String, Object> params8 = new HashMap<>();
    //
    //         // 启用mwnw
    //         params1.put("script", "chmod +x mvnw && ./mvnw clean");
    //         // 安装node、yarn等工具
    //         params2.put("script",
    //                 "./mvnw com.github.eirslett:frontend-maven-plugin:install-node-and-yarn -DnodeVersion=v8.9.4 -DyarnVersion=v1.3.2 " +
    //                         "&& ./mvnw com.github.eirslett:frontend-maven-plugin:yarn");
    //         // 执行后端测试
    //         params3.put("script", "./mvnw test");
    //         // 生成后端测试结果
    //         params4.put("testResults","**/target/surefire-reports/TEST-*.xml");
    //         // 执行前端测试
    //         params5.put("script","./mvnw com.github.eirslett:frontend-maven-plugin:yarn -Dfrontend.yarn.arguments=test");
    //         // 生成前端测试报告
    //         params6.put("testResults","**/target/test-results/karma/TESTS-*.xml");
    //         // 对应用进行打包
    //         params7.put("script","./mvnw verify -Pprod -DskipTests");
    //         // 在Jenkins项目页面上创建一个链接，点击链接可以下载打包之后的war包,两个参数
    //         params8.put("artifacts","**/target/*.war");
    //         params8.put("fingerprint", true);
    //
    //
    //
    //         step1.setStepName("sh");
    //         step1.setChildStep(null);
    //         step1.setStepParams(params1);
    //         step1.setDisplayName("Init mvnw");
    //
    //         step2.setStepName("sh");
    //         step2.setChildStep(null);
    //         step2.setStepParams(params2);
    //         step2.setDisplayName("Node & yarn install");
    //
    //         step3.setStepName("sh");
    //         step3.setChildStep(null);
    //         step3.setStepParams(params3);
    //         step3.setDisplayName("Backend test");
    //
    //         step4.setStepName("junit");
    //         step4.setChildStep(null);
    //         step4.setStepParams(params4);
    //         step4.setDisplayName("Generate backend test reports");
    //
    //         step5.setStepName("sh");
    //         step5.setChildStep(null);
    //         step5.setStepParams(params5);
    //         step5.setDisplayName("Frontend test");
    //
    //         step6.setStepName("junit");
    //         step6.setChildStep(null);
    //         step6.setStepParams(params6);
    //         step6.setDisplayName("Generate frontend test reports");
    //
    //         step7.setStepName("sh");
    //         step7.setChildStep(null);
    //         step7.setStepParams(params7);
    //         step7.setDisplayName("Packaging");
    //
    //         step8.setStepName("archiveArtifacts");
    //         step8.setChildStep(null);
    //         step8.setStepParams(params8);
    //         step8.setDisplayName("Archive war package");
    //
    //         stage1.getSteps().put(1, step1);
    //         stage2.getSteps().put(1, step2);
    //
    //
    //         steps.put(1, step1);
    //         steps.put(2, step2);
    //         steps.put(3, step3);
    //         steps.put(4, step4);
    //         steps.put(5, step5);
    //         steps.put(6, step6);
    //         steps.put(7, step7);
    //         steps.put(8, step8);
    //     }
    //
    //
    //     else if(projectName.equals("tale")){
    //         Step step1 = new Step();
    //
    //         // Step需要的map参数
    //         Map<String, Object> params1 = new HashMap<>();
    //         Map<String, Object> params2 = new HashMap<>();
    //         // 如果有键为null && map.size() == 1，表示该step只需要一个参数
    //         params1.put("script", "mvn clean install");
    //
    //         step1.setStepName("sh");
    //         step1.setChildStep(null);
    //         step1.setStepParams(params1);
    //
    //         params2.put("script", "java -jar tale-least.jar");
    //
    //         Step step2 = new Step();
    //
    //         step2.setStepName("sh");
    //         step2.setChildStep(null);
    //         step2.setStepParams(params2);
    //
    //         steps.put(1, step1);
    //         steps.put(2, step2);
    //     }
    // }

}
