package com.iscas.yf.IntelliPipeline.deprecated;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Deprecated
public class GitUtil {

    // 本地代码仓库。设置为final。
    public final static String localRepoPath = "/home/workplace/MyRepo";

    // git配置文件路径。设置为final。
    public final static String localRepoGitConfig = "/home/workplace/MyRepo/.git";

    // 本地开发代码的地方
    public static String localCodeDir = "D:/platplat";

    // Git仓库项目地址。可以通过某种方式根据不同的项目更改。
    public static String remoteRepoURI;



    // Getter和Setter是否有必要？
    // public static String getRemoteRepoURI() {
    //     return remoteRepoURI;
    // }
    //
    // public static void setRemoteRepoURI(String remoteRepoURI) {
    //     GitUtil.remoteRepoURI = remoteRepoURI;
    // }


    /**
     * 新建一个分支并同步到远程仓库
     * @param branchName
     * @throws IOException
     * @throws GitAPIException
     */
    public static String newBranch(String branchName){
        String newBranchIndex = "refs/heads/"+branchName;
        String gitPathURI = "";
        try {
            // 初始化Git
            Git git = setupRepo();

            // 检查新建的分支是否已经存在，如果存在则将已存在的分支强制删除并新建一个分支
            List<Ref> refs = git.branchList().call();
            for (Ref ref : refs) {
                if (ref.getName().equals(newBranchIndex)) {
                    System.out.println("Removing branch before");
                    git.branchDelete().setBranchNames(branchName).setForce(true)
                            .call();
                    break;
                }
            }
            // 新建分支
            Ref ref = git.branchCreate().setName(branchName).call();
            // 推送到远程
            git.push().add(ref).call();
            gitPathURI = remoteRepoURI + " " + "feature/" + branchName;
        }  catch (GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return gitPathURI;
    }


    // TODO: 参数化
    public static void commitFiles() throws IOException, GitAPIException{
        String filePath = "";
        Git git = Git.open( new File(localRepoGitConfig) );
        // 创建用户文件的过程
        File myFile = new File(filePath);
        myFile.createNewFile();
        git.add().addFilepattern("pets").call();
        // 提交
        git.commit().setMessage("Added pets").call();
        // 推送到远程
        git.push().call();
    }

    /**
     * 从远端仓库pull代码到本地
     * @Param cloneURL - 远端仓库地址
     *
     * */
    public static boolean pullBranchToLocal(String cloneURL){
        boolean flag = false;
        String[] splitURL = cloneURL.split(" ");
        String branchName = splitURL[1];
        // TODO: 应该需要加上项目的名字
        String fileDir = localRepoPath + "/" + branchName;

        File file = new File(fileDir);
        if(file.exists()){
            deleteFolder(file);
        }
        Git git;
        try {
            git = Git.open(new File(localRepoGitConfig));
            git.cloneRepository().setURI(cloneURL).setDirectory(file).call();
            flag = true;
        } catch(IOException e) {
            // 处理文件读写错误
            e.printStackTrace();
        } catch (GitAPIException e){
            // 处理Git错误
            e.printStackTrace();
        }
        return flag;
    }

    public static void deleteFolder(File file){
        if(file.isFile() || file.list().length==0){
            file.delete();
        }else{
            File[] files = file.listFiles();
            for(int i=0;i<files.length;i++){
                deleteFolder(files[i]);
                files[i].delete();
            }
        }
    }


    // 建立与远程仓库的联系，仅需要执行一次
    public static Git setupRepo() throws GitAPIException{
        Git git = Git.cloneRepository().setURI(remoteRepoURI).setDirectory(new File(localRepoPath)).call();
        return git;
    }
}
