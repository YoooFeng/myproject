package com.iscas.yf.IntelliPipeline.deprecated;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

@Deprecated
public class GitHubAuthenticatingService {

    public static GitHubClient autherticateClient(String userName, String userPassword) {
        return new GitHubClient().setCredentials(userName, userPassword);
    }

    public static RepositoryService authenticateRepoService(String userName, String userPassword){
        RepositoryService service = new RepositoryService();
        service.getClient().setCredentials(userName, userPassword);
        return service;
    }




}
