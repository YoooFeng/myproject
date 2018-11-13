package com.iscas.yf.IntelliPipeline.service.decision;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.bayes.NaiveBayes;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class BuildPredictor {

    private Instances train_instances = null;
    private Instances test_instances = null;
    // 从数据库中获取数据, 预测某个项目的某次构建结果
    // 项目名和构建序号唯一标识一次构建


    public void getFileInstances(String filename) throws Exception{

        // 通过读取文件来获取存储的数据?
        // 训练数据
        DataSource trData = new DataSource( filename );
        train_instances = trData.getDataSet();
        train_instances.setClassIndex(train_instances.numAttributes() - 1);


        DataSource testData = new DataSource("");
        test_instances = testData.getDataSet();
        test_instances.setClassIndex(test_instances.numAttributes() - 1);

        AttributeSelection filter = new AttributeSelection();
        // 配置过滤器
        CfsSubsetEval eval = new CfsSubsetEval();
        GreedyStepwise search = new GreedyStepwise();
        search.setSearchBackwards(true);
        filter.setEvaluator(eval);
        filter.setSearch(search);

        // 使用filter选择特定的特征,生成新的训练数据
        try {
            Instances ntr_instances = Filter.useFilter(train_instances, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void classify() throws Exception {

        // 朴素贝叶斯分类器
        NaiveBayes classifier = new NaiveBayes();
        classifier.buildClassifier(train_instances);

        System.out.println( classifier.classifyInstance(train_instances.instance(0)));
    }
}
