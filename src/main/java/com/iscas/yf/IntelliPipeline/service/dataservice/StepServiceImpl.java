package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dao.BuildDAO;
import com.iscas.yf.IntelliPipeline.dao.StepDAO;
import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class StepServiceImpl implements StepService{

    @Autowired
    StepDAO stepDAO;

    @Autowired
    BuildDAO buildDAO;

    // 这里要获得所有属于某一次build的所有step
    public List<Step> getAllSteps(Long buildId) {
        Build build = buildDAO.findOne(buildId);
        return build.getSteps();
    }

    public Step getStep(Long stepId){
        return stepDAO.findOne(stepId);
    }

    public void deleteStep(Long stepId){
        stepDAO.delete(stepId);
    }

    public Step modifyStep(Step step){
        stepDAO.save(step);
        return step;
    }

    public void modifySteps(List<Step> steps){
        stepDAO.save(steps);
    }
}
