package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dao.BuildStrategyDAO;
import com.iscas.yf.IntelliPipeline.dataview.BuildStrategyView;
import com.iscas.yf.IntelliPipeline.entity.BuildStrategy;
import com.iscas.yf.IntelliPipeline.entity.Project;

public interface BuildStrategyService {

    // 创建同时也可以修改
    public BuildStrategy createStrategy(Project project, BuildStrategyView.Item view);

    // 删除
    public void deleteStrategy(Long id);

    public BuildStrategy createStrategy(Long strategyId, BuildStrategyView.Item view);
}
