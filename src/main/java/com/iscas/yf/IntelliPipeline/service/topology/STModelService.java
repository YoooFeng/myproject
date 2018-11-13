package com.iscas.yf.IntelliPipeline.service.topology;

import com.google.common.base.Optional;
import com.iscas.yf.IntelliPipeline.dataview.ProjectView;

import java.io.IOException;

public interface STModelService {
    public Optional<String> transViewToSTM(ProjectView.Item view) throws IOException;
}
