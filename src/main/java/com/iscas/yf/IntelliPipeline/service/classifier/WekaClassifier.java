package com.iscas.yf.IntelliPipeline.service.classifier;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.experiment.InstanceQuery;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.treevisualizer.*;

import javax.servlet.ServletContext;
import java.applet.Applet;
import java.awt.*;
import java.util.ArrayList;

public class WekaClassifier extends Applet{

    // 将模型文件保存在项目目录下
    private static String MODEL_STORAGE_DIR = "/home/workplace/Github/IntelliPipeline/target/IntelliPipeline-1.0-SNAPSHOT/WEB-INF/resources/LocalRepo/test/";

    // 模型后缀
    private static String MODEL_EXTENSION = ".model";

    // Logger
    private static Logger logger = Logger.getLogger(WekaClassifier.class);

    // 从数据库中获取数据实例
    public static Instances getInstanceFromDatabase(String projectName, String language) throws Exception{
        InstanceQuery query = new InstanceQuery();

        query.setDatabaseURL("jdbc:mysql://localhost:3306/travistorrent");
        // 为查询配置数据库帐号和密码
        query.setUsername("root");
        query.setPassword("123456");

        // 没有指定projectName的情况
        if(projectName.equals("")) {

            query.setQuery("select "
                    + "distinct(tr_build_id), "
                    + "gh_team_size,"
                    // + "git_branch, "
                    + "git_diff_src_churn, "
                    + "tr_status "
                    + "from travistorrent_8_2_2017 "
                    + "where gh_lang = '"
                    + language
                    + "' and "
                    + "(tr_status = 'passed' or tr_status = 'failed')");

        }
        // 没有指定项目编程语言的情况
        else if(language.equals("")) {

            query.setQuery("select "
                    + "distinct(tr_build_id), "
                    + "gh_team_size,"
                    // + "git_branch, "
                    + "git_diff_src_churn, "
                    + "tr_status "
                    + "from travistorrent_8_2_2017 "
                    + "where gh_project_name = '"
                    + projectName
                    + "' and "
                    + "(tr_status = 'passed' or tr_status = 'failed')");

        }
        // 两个参数都指定的情况
        else {
            query.setQuery("select "
                    + "distinct(tr_build_id), "
                    // + "gh_project_name, "
                    + "gh_team_size,"
                    // + "git_branch, "
                    + "git_diff_src_churn, "
                    + "tr_status "
                    + "from travistorrent_8_2_2017 "
                    + "where gh_project_name='"
                    + projectName
                    + "' and "
                    + "gh_lang = '"
                    + language
                    + "' and "
                    + "(tr_status = 'passed' or tr_status = 'failed')");
            // + "'");
        }

        // 从查询结果中获取数据并返回
        return query.retrieveInstances();
    }

    // 训练数据得到分类器模型, 以 项目名.arff 的格式存放在项目本地目录下
    public static J48 trainModel(Instances trainData) throws Exception {

        // setClassIndex的意思是, 用某一个属性(一列)来代表这一条数据
        // 即指定要预测的某一列属性, 这里指定为 构建的结果(成功或失败)
        trainData.setClassIndex(trainData.numAttributes() - 1);

        // String[] options = new String[1];
        // // -U means unpruned tree
        // options[0] = "-U";

        // 使用的模型是J48树, 相关文献指出, Hoeffding tree有更好的效果
        J48 j48 = new J48();
        // j48.setOptions(options);
        j48.buildClassifier(trainData);

        // 属性列的集合
        // Attribute attribute;

        // 带有特征选择的分类器
        AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();

        // 评价模型分类准确性
        CfsSubsetEval eval = new CfsSubsetEval();

        GreedyStepwise search = new GreedyStepwise();

        // 设置为反向搜索, 从最大子集开始, 逐步减小
        search.setSearchBackwards(true);

        classifier.setClassifier(j48);

        classifier.setEvaluator(eval);

        classifier.setSearch(search);

        // 十折交叉验证
        Evaluation evaluation = new Evaluation(trainData);
        evaluation.crossValidateModel(j48, trainData, 10, new Debug.Random(1));


        // System.out.println(evaluation.toSummaryString("Classifier Ten fold " ,true));
        // 查看模型的验证结果
        System.out.println(evaluation.toSummaryString());



        // StackOverFlow example=======================================================
        // J48 j48 = new J48();
        //
        // // 过滤数据的属性
        // Remove rm = new Remove();
        // // 移除数据的第一个属性
        // rm.setAttributeIndices("1");
        //
        // // 过滤分类器
        // FilteredClassifier fc = new FilteredClassifier();
        // fc.setFilter(rm);
        // fc.setClassifier(j48);
        //
        // // 构建分类器
        // fc.buildClassifier(trainData);
        //
        // for(int i = 0; i < trainData.numInstances(); i++) {
        //     // 执行分类
        //     double prediction = fc.classifyInstance(trainData.instance(i));
        //
        //     System.out.println(trainData
        //             .classAttribute()
        //             .value((int)trainData.instance(i).classValue()));
        //
        //     System.out.println(trainData
        //             .classAttribute()
        //             .value((int)prediction));
        // }
        //
        // rm.setInputFormat(trainData);
        // =======================================================================

        // 输出

        // 查看准确率和召回率
        System.out.println(evaluation.toClassDetailsString());
        return j48;
    }

    /**
     * 保存分类器模型
     * @Param Classifier classifier: 分类器
     * @Param String name: 模型名(暂时与项目名称相同)
     * @Param String dir: 存放模型的地址
     * */
    public static void saveModel(Classifier classifier, String name, String dir) {
        // 利用java序列化存储训练好的模型
        try {
            SerializationHelper.write(dir + name + MODEL_EXTENSION, classifier);
            System.out.println("Save model successfully!");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取模型. Usage: Classifier j48 = readModel("name");
     * */
    public static <T> T loadModel(String name, String dir) {
        Classifier classifier = null;
        try {
            classifier = (Classifier) SerializationHelper.read(dir
                    + name + MODEL_EXTENSION);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return (T)classifier;
    }

    /**
     * 进行预测.
     * @Param projectName: 项目名, 利用项目名可以在HQL查找得到构建的历史数据.
     *                     但是未进行的构建需要实时获得相关特征值来进行预测.
     * */
    public static String predict(Classifier model, String buildRecord) {

        // 用来测试的Instance, 转化为BuildRecord的字符串形式
        String mock = "4,100,?";

        ArrayList<String> classVal = new ArrayList<>();
        classVal.add(mock);
        ArrayList<Attribute> atts = new ArrayList<>();
        atts.add(new Attribute("content", (ArrayList<String>)null));
        atts.add(new Attribute("@@class@@", classVal));

        Instances instance = new Instances("TestInstances", atts, 0);
        instance.setClassIndex(instance.numAttributes() - 1);

        try {
            // 进行判断
            Evaluation eval = new Evaluation(instance);
            // 分类第一个实例, 实际上也只有一个实例
            eval.evaluateModelOnce(model, instance.firstInstance());

            // 看看测试结果
            System.out.println(eval.toSummaryString());

        } catch (Exception e) {
            e.printStackTrace();
        }


        return "";
    }

    /**
     * 在JFrame框体中初始化决策树可视化图形.
     * */
    public static void visualizeTree(String dottyString) {
        final javax.swing.JFrame jf = new javax.swing.JFrame("J48 classifier");
        jf.setSize(1000, 800);
        jf.getContentPane().setLayout(new BorderLayout());
        TreeVisualizer tv = new TreeVisualizer(null, dottyString, new PlaceNode2());
        jf.getContentPane().add(tv, BorderLayout.CENTER);
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
            }
        });

        jf.setVisible(true);
        tv.fitToScreen();
    }

    public static void main(String[] args) throws Exception {
        Instances trainData = getInstanceFromDatabase("myronmarston/vcr");

        // 删除得到记录的第一个属性, 通常是build_id等无关的特征, 删掉不参与构建决策树
        trainData.deleteAttributeAt(0);

        J48 tree = trainModel(trainData);
        // J48 tree = loadModel("TestModel");

        // predict("TestModel");

        // saveModel(tree, "TestModel", "");

        // predict("TestModel");
        //
        visualizeTree(tree.graph());

        // trainData.setClassIndex(trainData.numAttributes() - 1);
        //
        // Evaluation evaluation = new Evaluation(trainData);

        // evaluation.crossValidateModel();

        // System.out.println(trainData);
        // System.out.println("Test");
    }
}
