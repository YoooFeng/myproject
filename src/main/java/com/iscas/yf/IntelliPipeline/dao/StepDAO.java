package com.iscas.yf.IntelliPipeline.dao;

import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StepDAO extends PagingAndSortingRepository<Step, Long> {

    // 使用Query注解的方式找到build对应的step
    // @Query("select step from Step step where step.build_id = ?1")
    // List<Step> findByBuildId(Long build_id);
}
