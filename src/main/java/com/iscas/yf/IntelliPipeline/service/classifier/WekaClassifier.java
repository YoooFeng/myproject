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
import weka.core.*;
import weka.experiment.InstanceQuery;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.treevisualizer.*;

import javax.servlet.ServletContext;
import java.applet.Applet;
import java.awt.*;
import java.util.ArrayList;

public class WekaClassifier extends Applet{

    // 将模型文件保存在项目目录下
    private static String MODEL_STORAGE_DIR = "/home/workplace/Github/IntelliPipeline/target/IntelliPipeline-1.0-SNAPSHOT/WEB-INF/resources/LocalRepo/java/";

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
    public static double predict(Classifier model, String buildRecord) {

        // 逗号将三个属性分割, 最后一个tr_status属性值为"?", 不需要加入instance
        String[] strs = buildRecord.split(",");

        Attribute team_size = new Attribute("gh_team_size");
        Attribute loc = new Attribute("git_diff_src_churn");
        Attribute status = new Attribute("tr_status");
        ArrayList<Attribute> atts = new ArrayList<>();
        atts.add(team_size);
        atts.add(loc);
        atts.add(status);

        double[] attValues = new double[3];
        // attValues[0] = 0;
        // attValues[1] = 100;
        attValues[0] = Double.parseDouble(strs[0]);
        attValues[1] = Double.parseDouble(strs[1]);
        // 这里设置的权重(weight)指的是这个instance的权重
        BinarySparseInstance i = new BinarySparseInstance(1.0, attValues);

        // atts.add(new Attribute("gh_team_size", "4"));
        // atts.add(new Attribute("git_diff_src_churn", "100"));

        Instances instance = new Instances("TestInstances", atts, 0);
        instance.add(i);
        instance.setClassIndex(instance.numAttributes() - 1);

        try {
            // 进行判断
            Evaluation eval = new Evaluation(instance);

            // 对一个实例进行分类, 如何查看分类结果?
            // eval.evaluateModelOnce(model, instance.firstInstance());

            // 看看测试结果
            System.out.println("Classify only one instance");
            System.out.println(eval.evaluateModelOnce(model, instance.firstInstance()));

            // 预测会得到一个0-1之间的值, 越接近0, 说明失败的概率越高, 越接近1, 说明成功的概率越高.
            return eval.evaluateModelOnce(model, instance.firstInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 预测失败, 返回0L
        return 0L;
    }

    /**
     * 在JFrame框体中初始化决策树可视化图形.
     * @Param String dottyString -
     * */
    public static void visualizeTree(String dottyString) {
        final javax.swing.JFrame jf = new javax.swing.JFrame("Decision Tree");
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
        Instances trainData = getInstanceFromDatabase("", "java");

        // 删除得到记录的第一个属性, 通常是build_id等无关的特征, 删掉不参与构建决策树
        trainData.deleteAttributeAt(0);
        System.out.println(trainData.toSummaryString());

        J48 tree = trainModel(trainData);
        // J48 tree = loadModel("TestModel", MODEL_STORAGE_DIR);

        saveModel(tree, "java", MODEL_STORAGE_DIR);

        // 预测结果
        // predict(tree, "");

        visualizeTree(tree.graph());

        // trainData.setClassIndex(trainData.numAttributes() - 1);
        //
        // Evaluation evaluation = new Evaluation(trainData);

        // evaluation.crossValidateModel();

        // System.out.println(trainData);
        // System.out.println("Test");
    }
}
