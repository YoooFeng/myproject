package com.iscas.yf.IntelliPipeline.entity;

import java.util.ArrayList;

public class Request {

    // 当前执行的step序号
    private int stepNumber;

    // 请求的类型，表示当前执行的状态，如initial、error等
    private String requestType;

    // 当前构建持续的时间，单位是毫秒
    private Long durationTime;

    // 两次构建之间产生的所有commit集合，都放在一个string里面
    // 格式[commitId: commitId by author: commit message]
    private String commitSet;

    // 控制台输出，可以拿到，但是全部传输过来比较缓慢且无必要(Bug)，可以先行进行一些处理，只发送关键的信息
    private String consoleOutput;

    private String currentResult;

    public int getStepNumber() {
        return stepNumber;
    }

    public String getRequestType() {
        return requestType;
    }

    public long getDurationTime() {
        return durationTime;
    }

    public String getCommitSets() {
        return commitSet;
    }

    public String getConsoleOutput() {
        return consoleOutput;
    }

    public String getCurrentResult() {
        return currentResult;
    }

    @Override
    public String toString() {
        return "Request{" +
                "stepNumber=" + stepNumber +
                ", requestType='" + requestType + '\'' +
                ", durationTime=" + durationTime +
                ", commitSet='" + commitSet + '\'' +
                ", consoleOutput='" + consoleOutput + '\'' +
                '}';
    }
}
