package com.iscas.yf.IntelliPipeline.deprecated;

import org.eclipse.jgit.lib.Repository;

@Deprecated
public interface RepositoryProvider {
    Repository get() throws Exception;
}
