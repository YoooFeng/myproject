package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.entity.record.BuildRecord;

public interface BuildRecordService {

    // 在数据库中存储
    public BuildRecord createBuildRecord(BuildRecord record);

    // 根据id删除某条记录
    public void deleteBuildRecord(Long id);

}
