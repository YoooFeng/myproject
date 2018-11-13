package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dataview.StepView;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;

import java.util.List;

public interface StepService {

    public List<Step> getAllSteps(Long buildId);

    public Step getStep(Long stepId);

    public void deleteStep(Long stepId);

    public Step modifyStep(Step step);

    public void modifySteps(List<Step> steps);

}
