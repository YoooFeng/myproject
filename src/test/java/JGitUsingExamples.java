// 测试JGit是否正常工作
public class JGitUsingExamples {

    // 测试用变量
    private static String repoUrl = "https://github.com/otale/tale.git";
    private static String repoPath = "/home/workplace/taleRepo";
    private static String oldCommit = "91ac044a6f9def2f10f11d1b480381ee51841ab0";

    private static String newCommit = "9ab28a9128026a712727ef3583d64619e75db836";

    //====================== 如何clone项目的样例 =================================
    // // 根据本地是否存在仓库，创建不同的provider。
    //
    // // 新建的本地仓库
    // private static RepositoryProvider repoProvider =
    //             new RepositoryProviderCloneImpl("https://github.com/YoooFeng/Just-for-test.git",
    //                     "/home/workplace/JGitTestRepo");
    //
    // // 已经存在的本地仓库
    // private static RepositoryProvider existingRepoProvider =
    //             new RepositoryProviderExistingClientImpl("/home/workplace/JGitTestRepo/.git");
    //
    // // 执行git.pull()方法将仓库下载到本地
    // public static void main(String[] args) throws Exception {
    //     try (Git git = new Git(repoProvider.get())) {
    //             git.pull().call();
    //     }
    // }
    //=============================================================================

    //======================= 通过commitId显示两次不同commit的函数用法 =====================
    // public static void main(String[] args) throws IOException, GitAPIException {
    //     File localPath = File.createTempFile(repoPath, "");
    //     // 先把原目录删除
    //     if(!localPath.delete()) {
    //         throw new IOException("Could not delete temporary file " + localPath);
    //     }
    //
    //     // clone的时候会新建文件目录
    //     try (Repository repository = Git.cloneRepository()
    //             .setURI(repoUrl)
    //             .setDirectory(localPath)
    //             .call().getRepository()) {
    //         try (Git git = new Git(repository)) {
    //
    //             // compare older commit with the newer one, showing an addition
    //             // and 2 changes
    //             listDiff(repository, git,
    //                     oldCommit,
    //                     newCommit);
    //
    //             // // also the diffing the reverse works and now shows a delete
    //             // // instead of the added file
    //             // listDiff(repository, git,
    //             //         "19536fe5765ee79489265927a97cb0e19bb93e70",
    //             //         "3cc51d5cfd1dc3e890f9d6ded4698cb0d22e650e");
    //             //
    //             // // to compare against the "previous" commit, you can use
    //             // // the caret-notation
    //             // listDiff(repository, git,
    //             //         "19536fe5765ee79489265927a97cb0e19bb93e70^",
    //             //         "19536fe5765ee79489265927a97cb0e19bb93e70");
    //         }
    //     }
    // }
    //======================================================================================

    //================================== 获取修改代码行数的用法 ===============================
    // public static void main(String[] args) throws IOException, GitAPIException {
    //     File localPath = new File(repoPath);
    //
    //     // 先把原目录删除
    //     // if (!localPath.delete()) {
    //     //     throw new IOException("Could not delete temporary file " + localPath);
    //     // }
    //
    //     Git git = Git.open(new File(repoPath + "/.git"));
    //     // clone的时候会新建文件目录
    //     // try (Repository repository = Git.cloneRepository()
    //     //         .setURI(repoUrl)
    //     //         .setDirectory(localPath)
    //     //         .call().getRepository()) {
    //         // Git git = new Git(repository)
    //     try (Repository repository = git.getRepository()) {
    //         System.out.println("Repository: " + repository.getDirectory());
    //
    //         int count = 0;
    //         // 进行处理，统计出修改的行数
    //         ArrayList<String> modifiedLinesSets = getModifiedLines(repository, oldCommit);
    //
    //         // 增加和减少的行数都加起来，算出一个总数
    //         for(String s : modifiedLinesSets){
    //             // 正则匹配模式@@ ... @@ 中间的内容
    //             String express = "(@@\\s+\\[+-]\\d+\\[,]\\[+-]\\d+\\s+@@)";
    //             Matcher matcher = Pattern.compile(express).matcher(s);
    //
    //             String[] s1 = s.split("@@");
    //             // 空格分隔
    //             String[] s2 = s1[1].trim().split(" ");
    //             for(String s3 : s2){
    //                 String[] s4 = s3.split(",");
    //                 count += Integer.parseInt(s4[1].trim());
    //             }
    //         }
    //         System.out.println("Modified Lines Sets: " + modifiedLinesSets);
    //         System.out.println("Modified Lines of Code: " + count);
    //     }
    //
    // }
    //======================================================================================

    //==================================== 修改文件类型判定 ==================================
    // public static void main(String[] args) throws IOException, GitAPIException {
    //     File localPath = new File(repoPath);
    //
    //     // // 先把原目录删除
    //     // if (!localPath.delete()) {
    //     //     throw new IOException("Could not delete temporary file " + localPath);
    //     // }
    //
    //     Git git = Git.open(new File(repoPath + "/.git"));
    //     ArrayList<String> modifiedFiles;
    //     String result = "Modified file suffix: ";
    //     boolean flag = false;
    //     // clone的时候会新建文件目录
    //     try (Repository repository = git.getRepository()) {
    //
    //         modifiedFiles = listFileDiffs(repository, git, oldCommit, newCommit);
    //         System.out.println(modifiedFiles);
    //         for(String s : modifiedFiles){
    //             String[] s1 = s.split(":");
    //             for(String s2 : s1){
    //                 // s2是文件名
    //                 s2 = s1[1].trim();
    //                 // s3分割后缀
    //                 String[] s3 = s2.split(".");
    //
    //                 for(String s4 : s3){
    //                     result += "[" + s4 + "] ";
    //                 }
    //             }
    //         }
    //         System.out.println(result);
    //     }
    // }
    //==========================================================================================
}
