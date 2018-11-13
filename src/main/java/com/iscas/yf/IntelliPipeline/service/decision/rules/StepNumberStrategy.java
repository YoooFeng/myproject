package com.iscas.yf.IntelliPipeline.service.decision.rules;

/**
 * 对stepNumber进行决策, 实现了接口Strategy.
 * */
public class StepNumberStrategy extends Strategy{

    // 通过public方法访问private变量
    public StepNumberStrategy() {
        this.setConfigured(true);
    }

    /**
     * 决策的具体方法, 只返回是否通过判定的结果, 判断规则对外界是黑盒
     * */
    public void stepNumberDecision(int stepNumber, int stepSize){

        // 最后一个step已经被执行, 那么返回false(未通过)让流程结束, 标记为END
        if(stepNumber == stepSize + 1 || stepNumber > stepSize){
            this.setPassed(false);
        } else {
            this.setPassed(true);
        }
    }
}
