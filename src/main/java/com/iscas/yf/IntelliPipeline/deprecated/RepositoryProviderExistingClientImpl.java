package com.iscas.yf.IntelliPipeline.deprecated;

import com.iscas.yf.IntelliPipeline.deprecated.RepositoryProvider;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

@Deprecated
// 用于处理clone已经存在本地的仓库的情况
public class RepositoryProviderExistingClientImpl implements RepositoryProvider {

    private String clientPath;

    public RepositoryProviderExistingClientImpl(String clientPath) {
        this.clientPath = clientPath;
    }
    @Override
    public Repository get() throws Exception {
        try (Repository repo = new FileRepository(clientPath)) {
            return repo;
        }
    }
}
