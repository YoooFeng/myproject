package com.iscas.yf.IntelliPipeline.deprecated;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Deprecated
public class Stage extends IdEntity {

    // stage名。DisplayName和Name是同一个。
    private String stageName;

    private Build build;


    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    // Build需要Getter和Setter吗
}
