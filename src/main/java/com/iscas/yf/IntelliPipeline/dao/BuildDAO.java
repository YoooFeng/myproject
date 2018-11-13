package com.iscas.yf.IntelliPipeline.dao;


import com.iscas.yf.IntelliPipeline.entity.Build;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BuildDAO extends PagingAndSortingRepository<Build, Long>{

    // 使用Query注解的方式找到build对应的step
    // @Query("select build from Build build where build.project_id = ?1")
    // List<Build> findByProjectId(Long project_id);

}
