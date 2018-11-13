package com.iscas.yf.IntelliPipeline.service.decision.rules;

/**
 * 所有用户可配置策略的策略模式接口
 * */
public abstract class Strategy {

    // TODO: 能否抽取所有Strategy共有的性质?

    /**
     * 规则是否判定通过
     * */
    private boolean isPassed;

    /**
     * 规则是否被配置
     * */
    private boolean isConfigured;

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    public boolean isConfigured() {
        return isConfigured;
    }

    public void setConfigured(boolean configured) {
        isConfigured = configured;
    }
}
