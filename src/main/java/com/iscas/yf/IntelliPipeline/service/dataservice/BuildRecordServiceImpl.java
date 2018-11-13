package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dao.BuildRecordDAO;
import com.iscas.yf.IntelliPipeline.entity.record.BuildRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class BuildRecordServiceImpl implements BuildRecordService {

    private Logger logger = Logger.getLogger(BuildRecordServiceImpl.class);

    @Autowired
    BuildRecordDAO buildRecordDAO;

    // 保存
    public BuildRecord createBuildRecord(BuildRecord record) {

        buildRecordDAO.save(record);

        return record;
    }

    // 删除
    public void deleteBuildRecord(Long id) {
        buildRecordDAO.delete(id);
    }
}
