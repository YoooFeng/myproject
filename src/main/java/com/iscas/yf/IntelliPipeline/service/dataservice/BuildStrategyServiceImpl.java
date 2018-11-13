package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dao.BuildStrategyDAO;
import com.iscas.yf.IntelliPipeline.dataview.BuildStrategyView;
import com.iscas.yf.IntelliPipeline.entity.BuildStrategy;
import com.iscas.yf.IntelliPipeline.entity.Project;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuildStrategyServiceImpl implements BuildStrategyService {
    private Logger logger = Logger.getLogger(BuildStrategyServiceImpl.class);

    @Autowired
    BuildStrategyDAO strategyDAO;

    // 创建新的策略
    public BuildStrategy createStrategy(Project project, BuildStrategyView.Item view){
        // 新的策略对象
        BuildStrategy strategy = new BuildStrategy();
        strategy.setProject(project);
        // 转换成新的strategy. 目前是六条规则
        strategy.setCommitters_mail(view.committers_mail);
        strategy.setKey_paths(view.key_paths);
        strategy.setModel_modified(view.model_modified);
        strategy.setModified_lines(view.modified_lines);
        strategy.setSkip_paths(view.skip_paths);
        strategy.setTime_interval(view.time_interval);

        strategyDAO.save(strategy);

        // 返回有id的strategy
        return strategy;

    }

    // 删除
    public void deleteStrategy(Long id){
        strategyDAO.delete(id);
    }

    // 修改已有的策略
    public BuildStrategy createStrategy(Long strategyId, BuildStrategyView.Item view){
        BuildStrategy strategy = strategyDAO.findOne(strategyId);

        strategy.setCommitters_mail(view.committers_mail);
        strategy.setKey_paths(view.key_paths);
        strategy.setModel_modified(view.model_modified);
        strategy.setModified_lines(view.modified_lines);
        strategy.setSkip_paths(view.skip_paths);
        strategy.setTime_interval(view.time_interval);

        strategyDAO.save(strategy);

        // 返回有id的strategy
        return strategy;
    }
}
