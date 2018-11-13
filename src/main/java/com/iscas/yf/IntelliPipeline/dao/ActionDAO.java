package com.iscas.yf.IntelliPipeline.dao;

import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Action;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ActionDAO extends PagingAndSortingRepository<Action, Long>{
}
