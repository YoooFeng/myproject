package com.iscas.yf.IntelliPipeline.service.decision.rules;

import com.iscas.yf.IntelliPipeline.entity.Build;

import java.util.Date;

public class TimeIntervalStrategy extends Strategy{

    /**
     * 决策构建时间间隔是否满足条件.
     * */
    public void timeIntervalDecision(Build curBuild, Long strategyInterval){
        // 计算上一次构建和本次构建的时间差(单位: 小时)
        // 获取当前项目总build数量
        int len = curBuild.getProject().getBuilds().size();

        // 倒数第二个build的创建时间, len - 2. 遍历, 获取上一次成功的构建.
        Date lastBuildTime = null;
        while(len-- > 0 && len - 2 > 0){
            // 从后往前找到成功的上一个构建, 如果找不到, 那么lastBuildTime为空.
            if(curBuild.getProject().getBuilds().get(len - 2).getStatus() == Build.Status.SUCCEED){
                lastBuildTime = curBuild.getProject().getBuilds().get(len - 2).getCreateTime();
                break;
            }
        }

        Date curBuildTime = curBuild.getCreateTime();
        long diff = Integer.MAX_VALUE;

        // lastBuildTime == null 的情况下, diff为最大值, 保证该策略判定能够通过
        if(len != 0 && lastBuildTime != null){
            diff = curBuildTime.getTime() - lastBuildTime.getTime();
        }

        // diff单位是毫秒, 这里转化为小时
        long hours = diff / (1000 * 60 * 60);

        // 对比实际的时间间隔和配置的时间间隔
        if(hours <= strategyInterval) this.setPassed(false);
        else this.setPassed(true);
    }
}
