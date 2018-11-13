package com.iscas.yf.IntelliPipeline.service.util;


import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepParam;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class YamlResolver {

    // 解析完成后, 返回List<Stage>
    // 直接返回List<Step>
    public static List<Step> testResolving(String filePath){
        Yaml yaml = new Yaml();

        // 要得到的List<Stage>对象
        // List<Stage> stages = new ArrayList<>();
        List<Step> steps = new ArrayList<>();

        try{
            FileInputStream in = new FileInputStream(new File(filePath));
            Map data = yaml.loadAs(in, Map.class);

            // 不用记录编号, 按上下文顺序放入数组即可
            // 记录stage的序号，从1开始。
            // int stageNumber = 1;

            // 使用iterator来遍历LinkedHashMap
            Iterator<Map.Entry> stageIterator = data.entrySet().iterator();
            // 首先遍历的是stage这个维度
            while(stageIterator.hasNext()) {
                Map.Entry stageEntry = stageIterator.next();

                // stages只有键没有值，跳过不处理
                if (stageEntry.getKey().equals("stages")) continue;

                // 将Character[]格式的键转化为String格式
                String stageName = stageEntry.getKey().toString().trim()
                        .replace("stage(", "")
                        .replace(")", "");


                // 同样不记录序号, 按顺序放入数组即可
                // 记录当前stage中所有的step序号，从1开始。
                // int stepNumber = 1;

                // 对stage和step进行处理。嵌套循环处理stage下的所有step。
                ArrayList stepsArray = (ArrayList) stageEntry.getValue();

                // Stage对象, 构造函数要求初始化时传入stageName(合理？)
                // Stage curStage = new Stage(stageName);

                // 遍历某个stage下的所有step
                for (Object step : stepsArray) {
                    System.out.println(step);

                    // 处理step名称以及参数。注意类型转换。
                    Map.Entry stepEntry = (Map.Entry) ((LinkedHashMap) step).entrySet().iterator().next();

                    // stepDisplayName在括号中，需要进行一些处理
                    String stepDisplayName = stepEntry.getKey().toString().trim();

                    // 去掉不需要的信息
                    stepDisplayName = stepDisplayName
                            .replace("step(", "")
                            .replace(")", "");

                    // 当前Step。用DisplayName构造。
                    Step curStep = new Step(stepDisplayName);

                    // 只需要保存stageName
                    curStep.setStageName(stageName);

                    String stepContent = stepEntry.getValue().toString();

                    // 处理step的参数名和值
                    // 先以双引号来分割, 注意转义字符 \
                    String[] stepSplitContent = stepContent.split("\"");

                    // 0是sh、junit等stepName，1是所有参数，需再进行处理
                    String stepName = stepSplitContent[0].trim();
                    // 传入stepName
                    curStep.setStepName(stepName);

                    String stepAllParams = stepSplitContent[1].trim();

                    // 多个参数之间用逗号间隔
                    String[] splitAllParams = stepAllParams.split(",");



                    // 单独处理每个参数
                    for (String param : splitAllParams) {

                        // Step的参数对象
                        StepParam curStepParam = new StepParam();

                        param = param.trim();

                        // 以冒号为分割符，分割参数名和参数值。设定分割的份数，参数值中的冒号不进行分割
                        String[] splitParam = param.split(":", 2);

                        // 参数名和参数值，分别加入Map中。
                        String paramName = splitParam[0].trim();
                        String paramValue = splitParam[1].trim();

                        curStepParam.setParamsKey(paramName);
                        curStepParam.setParamsValue(paramValue);

                        // 将参数按照解析的顺序依次put到Params Map中
                        curStep.getStepParams().add(curStepParam);



                    }
                    // 当前step处理完毕，加入Stage中
                    steps.add(curStep);
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        return steps;
    }

    // public static void main(String[] args){
    //     List<Step> steps = testResolving("/home/workplace/sample.yaml");
    // }


}
