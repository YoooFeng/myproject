package com.iscas.yf.IntelliPipeline.dao;

import com.iscas.yf.IntelliPipeline.entity.record.BuildRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BuildRecordDAO extends PagingAndSortingRepository<BuildRecord, Long> {
}
