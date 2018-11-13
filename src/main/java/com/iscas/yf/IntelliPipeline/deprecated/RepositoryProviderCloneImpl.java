package com.iscas.yf.IntelliPipeline.deprecated;

import com.iscas.yf.IntelliPipeline.deprecated.RepositoryProvider;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import java.io.File;

@Deprecated
public class RepositoryProviderCloneImpl implements RepositoryProvider {

    // repoPath: 远程仓库的目录
    private String repoURL;
    // clientPath: 克隆到本地的目录
    private String cloneDirectoryPath;

    public RepositoryProviderCloneImpl(String repoURL, String cloneDirectoryPath) {
        this.repoURL = repoURL;
        this.cloneDirectoryPath = cloneDirectoryPath;
    }
    // 实现get方法
    @Override
    public Repository get() throws Exception {
        File client = new File(cloneDirectoryPath);
        client.mkdir();
        try {
            System.out.println("Cloning " + repoURL + " into "+ cloneDirectoryPath);
            Git result = Git.cloneRepository()
                    .setURI(repoURL)
                    .setDirectory(client)
                    .call();
            System.out.println("Completed Cloning");
            // 返回repo
            return result.getRepository();
        } catch(GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
        }
        // 如果try中没有返回，直接返回null
        return null;
    }
}
