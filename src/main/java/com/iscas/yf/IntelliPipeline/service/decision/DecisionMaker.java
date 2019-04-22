package com.iscas.yf.IntelliPipeline.service.decision;

import com.iscas.yf.IntelliPipeline.entity.Build;
import com.iscas.yf.IntelliPipeline.entity.BuildStrategy;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Relation;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.Step;
import com.iscas.yf.IntelliPipeline.entity.pipelinecomponent.StepParam;
import com.iscas.yf.IntelliPipeline.service.dataservice.StepService;
import com.iscas.yf.IntelliPipeline.service.decision.rules.StepNumberStrategy;
import com.iscas.yf.IntelliPipeline.service.decision.rules.TimeIntervalStrategy;
import com.iscas.yf.IntelliPipeline.service.util.GitHubRepoService;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: 解偶该类的判断逻辑
public class DecisionMaker {

    // log4j - 日志输出
    private static Logger logger = Logger.getLogger(DecisionMaker.class);

    @Autowired
    StepService stepService;
    /**
     * requestType = "INIT", 判断是否进行此次构建
     * @Param requestBody 发送过来的请求
     * @Param curBuild 此次构建对象
     * @return String
     * */
    public static String getInitDecision(Map<String, String> requestBody,
                                         Build curBuild, Map<String, String> analysis, Git git) throws Exception{

        // 要返回的decision
        String initDecision = "";

        // 综合所有策略的结果, 作出决策
        boolean allStrategies = false;

        // 从request中获取到step计数器
        int stepNumber = Integer.parseInt(requestBody.get("stepNumber"));

        List<Step> steps = curBuild.getSteps();

        // 默认必须执行的StepNumber策略
        StepNumberStrategy stepNumberStrategy = new StepNumberStrategy();
        stepNumberStrategy.stepNumberDecision(stepNumber, steps.size());

        // 如果StepNumber策略没有通过, 直接返回initDecision = "END"
        if(!stepNumberStrategy.isPassed()) {
            initDecision = "END";
            return initDecision;
        }

        // 最后一个step已经被执行, 那么返回END让流程结束, 标记build为SUCCEED.
        // if(stepNumber == steps.size() + 1){
        //     curBuild.changeStatusToSucceed();
        //     initDecision = "END";
        //     return initDecision;
        // }

        // 获取project的strategy

        BuildStrategy strategy = curBuild.getProject().getStrategy();

        // 如果用户没有配置strategy, 直接执行本次构建
        if(strategy == null) {
            initDecision = "NEXT";
            return initDecision;
        }

        /**
         * 用户配置的时间间隔, 判断是否配置
         * 值为null时有两种情况, 一种是没有勾选该策略, 一种是勾选了但是没有填入数值
         * 都按照没有配置该策略处理
         * */
        TimeIntervalStrategy timeIntervalStrategy = new TimeIntervalStrategy();
        if(strategy.getTime_interval() > 0) {
            // 获取构建策略中配置的时间间隔
            long strategyTimeInterval = strategy.getTime_interval();
            // 不为null和-1, 标记为已配置
            timeIntervalStrategy.setConfigured(true);
            timeIntervalStrategy.timeIntervalDecision(curBuild, strategyTimeInterval);
        } else {
            timeIntervalStrategy.setConfigured(false);
        }

        // 时间间隔不满足配置条件, 直接返回跳过
        if(timeIntervalStrategy.isConfigured() && !timeIntervalStrategy.isPassed()) {
            initDecision = "SKIP_BUILD";

            curBuild.changeStatusToSkipped();

            // 邮件中解释为什么会跳过本次构建的原因
            String msg = "This build is skipped because the time interval configured.";

            notificationStep(curBuild, msg);
            return initDecision;
        }

        // 如果不配置, int初始值为-1

        int strategyModifiedLOC = strategy.getModified_lines();

        // 过程编排模型是否修改由客户端进行判定
        boolean strategyModelModified = strategy.isModel_modified();

        // 配置
        // 路径和邮件地址都是用逗号 , 分隔的. 还需要记录所有修改文件的路径. 判断是否进行了配置
        String[] strategyKeyPaths = strategy.getKey_paths() == null ? null : strategy.getKey_paths().split(",");
        String strategySkipPaths = strategy.getSkip_paths();
        String[] strategyAuthorMails = strategy.getCommitters_mail() == null ? null : strategy.getCommitters_mail().split(",");

        // 包含两次build之间所有commitId的信息，需要进行额外处理
        // 格式：[commitId : author : commitMsg] ...... []
        // String commits = requestBody.get("commitSet");

        // 如果commits为空, 有两种情况, 一是第一次构建, 二是没有修改仓库
        // if(commits.length() < 10){
        //     if(requestBody.get("buildNumber").equals("1")){
        //         return "NEXT";
        //     }
        //      else {
        //         // curBuild.changeStatusToSkipped();
        //         return "NEXT";
        //     }
        // }


        // 得到有经验的作者列表, 空格隔开. 5次构建内

        // 回溯指定个数的commit
        // String exAuthor = GitHubRepoService.getExperiencedAuthors(5, git).toString();

        // 代码修改行数
        int modifiedLinesOfCode = Integer.parseInt(analysis.get("ModifiedLines"));
        logger.info("ModifiedLines: " + modifiedLinesOfCode);

        // 修改的路径
        String modifiedPaths = analysis.get("ModifiedPaths");
        logger.info("ModifiedPaths: " + modifiedPaths);

        // 作者
        String committerMails = analysis.get("Authors");
        logger.info("CommitterMails: " + committerMails);

        // 有经验的作者提交, 直接跳过本次构建
        String[] authors = committerMails.split(" ");
        // for(String author : authors){
        //     if(author.equals("vishal.lal@container-solutions.com")){
        //         curBuild.changeStatusToSkipped();
        //         return "SKIP_BUILD";
        //     }
        // }

        // 修改的文件类型
        String modifiedTypes = analysis.get("ModifiedTypes");
        logger.info("ModifiedTypes: " + modifiedTypes);

        // TEST CORE DOC 等类型
        String types = judgeFileType(modifiedTypes);
        logger.info("Types: " + types);

        // 前10次commit的作者
        // StringBuffer experiencedAuthors = GitHubRepoService.getExperiencedAuthors(10, git);
        // String[] curAuthors = analysis.get("Authors").split(" ");

        // build对应的最新一次commit Id
        String latestCommit = analysis.get("LatestCommit");
        curBuild.setLatestCommitId(latestCommit);

        // 查看提交的作者是否在用户配置的黑名单中
        boolean isFreshHand;
        // 是否存在关键路径
        boolean isKeyPaths;
        // 是否全部是跳过路径
        boolean isSkipPaths;

        // TODO: 解耦规则判断的过程
        // 提交者邮箱策略----------------------------------------------------------------------
        if(strategyAuthorMails != null && strategyAuthorMails.length > 0) isFreshHand = false;
        // 如果用户没有设置黑名单, 那么不进行提交者的判定
        else isFreshHand = true;

        // 不等于true, 才进入双重循环进行对比
        if(!isFreshHand){
            for(String sAuthor : strategyAuthorMails){
                // 相等
                sAuthor = sAuthor.trim();
                if(committerMails.contains(sAuthor)){
                    isFreshHand = true;
                    break;
                }
            }
        } else isFreshHand = false;
        //-------------------------------------------------------------------------------------

        //关键路径策略--------------------------------------------------------------------------
        if(strategyKeyPaths != null && strategyKeyPaths.length > 0) isKeyPaths = false;
        else isKeyPaths = true;
        if(!isKeyPaths){
            for(String keyPath : strategyKeyPaths){
                keyPath = keyPath.trim();
                if(modifiedPaths.contains(keyPath)){
                    isKeyPaths = true;
                    break;
                }
            }
        } else isKeyPaths = false;
        //---------------------------------------------------------------------------------------

        //跳过路径策略-----------------------------------------------------------------------------
        if(strategySkipPaths != null && strategySkipPaths.length() > 0) isSkipPaths = false;
        else isSkipPaths = true;
        if(!isSkipPaths){
            for(String modifiedPath : modifiedPaths.split(" ")){
                modifiedPath = modifiedPath.trim();

                // 如果跳过路径中存在test
                if(modifiedPath.contains("Test") || modifiedPath.contains("test") || types.equals("TEST")){
                    // 跳过建模中除与test有关之外的步骤
                    skipStepsExceptTest(curBuild);
                }

                // 存在非跳过路径的修改, 逻辑相反
                if(!strategySkipPaths.contains(modifiedPath)){
                    isSkipPaths = false;
                    break;
                }
            }
        } else isSkipPaths = true;
        //----------------------------------------------------------------------------------------

        // 几个策略综合考虑, 得出决策 )
        // allStrategies = isFreshHand || isKeyPaths ||
        //           (hours >= strategyTimeInterval) || (modifiedLinesOfCode >= strategyModifiedLOC);

        // 要进行构建的情况, TODO: 暂时不考虑可配置策略
        if(types.contains("CORE")){
            initDecision = "NEXT";
        }
        // TODO: 只要修改了test就只执行测试
        else if(modifiedPaths.contains("Test") || modifiedPaths.contains("test") || types.equals("TEST")){
            // 跳过建模中除与test有关之外的步骤
            skipStepsExceptTest(curBuild);
        }
        else if(types.equals("CONFIG")){
            skipStepsExceptDocker(curBuild);
            initDecision = "NEXT";
        }
        // 只修改了文档
        else if(types.equals("DOCUMENT")){
            // TODO: 将这部分代码抽取出来
            // 将本次构建的所有步骤都跳过, mail是后面加入的步骤, 所以不会被设置为跳过.
            curBuild.changeStatusToSkipped();

            // 邮件中解释为什么会跳过本次构建的原因
            String msg = "(1) The authors are " + authors.toString() + ";";
            msg += " (2) Modified code lines is " + modifiedLinesOfCode;

            notificationStep(curBuild, msg);

            initDecision = "SKIP_BUILD";
        } else {
            // 如果两个规则都不满足, 默认执行构建?
            initDecision = "NEXT";
        }

        // String modifiedFileTypes = analysis.get("ModifiedTypes");
        //
        // String fileTypes = judgeFileType(modifiedFileTypes);
        //
        // // 含有核心代码, 或者提交者中有新手, 进行构建
        // if(fileTypes.contains("CORE") || fileTypes.contains("DOCKERFILE") || isFreshHand) {
        //     initDecision = "NEXT";
        // }
        // // 跳过了含有核心代码的情况, 这是含有测试代码且不含核心代码的情况. 通过DisplayName判定Test步骤?
        // else if(fileTypes.contains("TEST")){
        //     for(Step step : curBuild.getSteps()){
        //         // DisplayName中没有包含Test的将不会被执行!
        //         if(step.getStatus().equals(Step.Status.SUSPENDED) &&
        //                 (!step.getDisplayName().contains("test") || !step.getDisplayName().contains("Test"))){
        //             step.setStatus(Step.Status.SKIPPED);
        //         }
        //     }
        //     initDecision = "NEXT";
        // }
        // // 这是只含有Doc修改或者无任何修改的情况, 跳过所有流程, 只执行notification
        // else {
        //     // 将本次构建的所有步骤都跳过, 除去mail步骤.
        //     curBuild.changeStatusToSkipped();
        //
        //     // 通知的步骤是自动
        //     String msg = "the modified file types are[" + modifiedFileTypes + "], " +
        //             "and there are " + String.valueOf(modifiedLinesOfCode) + " LOC modified.";
        //
        //     notificationStep(curBuild, msg);
        //
        //     initDecision = "SKIP_BUILD";
        // }

        // 如果判定要执行构建, 将本地的代码仓库更新到最新状态
        if(initDecision.equals("NEXT")){
            // 将本地的仓库更新到最新HEAD版本, 运行时取消注释
            // git.pull().call();
        }

        return initDecision;
    }

    /**
     * 跳过所有stageName没有包含docker的步骤
     * */
    public static void skipStepsExceptDocker(Build curBuild){
        List<Step> steps = curBuild.getSteps();

        for(Step step : steps){
            if(!step.getStageName().contains("docker") && !step.getStageName().contains("Docker")){
                step.setStatus(Step.Status.SKIPPED);
            }
        }
    }

    /**
     * 跳过build中stageName或displayName没有包含"test" or "Test" or "测试"的步骤
     * */
    public static void skipStepsExceptTest(Build curBuild){
        List<Step> steps = curBuild.getSteps();
        // 遍历
        for(Step step : steps){
            // stageName或displayName不包含test
            if(!step.getStageName().contains("test") && !step.getStageName().contains("Test") && !step.getStageName().contains("测试")
                    && !step.getDisplayName().contains("test") && !step.getDisplayName().contains("Test") && !step.getDisplayName().contains("测试")){
                step.setStatus(Step.Status.SKIPPED);
            }
        }
    }

    /**
     * 进行构建时决策, 处理 currentResult == "FAILURE" 的情况
     * */
    public static String getRuntimeDecision(Map<String, String> requestBody, Build curBuild){
        // 如果最后一个Step执行完, 返回END
        if(requestBody.get("currentResult").equals("SUCCESS") && (curBuild.getSteps().size() + 1) <= Integer.parseInt(requestBody.get("stepNumber"))){
            return "END";
        } else if(requestBody.get("currentResult").equals("FAILURE") || requestBody.get("currentResult").equals("NETWORK_ERROR")){
            // 这里应该进行简单的分析
            return "RETRY";
        }
        else {
            return "NEXT";
        }
    }

    /**
     * @Function 利用commitId对两次构建之间的修改信息进行处理，统计两次build之间总共修改的代码行数。
     * @Param commitId - 格式[commitId : author : commitMsg] [...] []
     * @Param curBuild - 当前的build对象
     * */
    @Deprecated
    public static ArrayList<String> getModifiedSet(String commits, Git git) throws Exception {
        List<String> parsedCommitId;

        if(commits !=  null && commits.length() > 0){
            parsedCommitId = processCommits(commits);
        } else return null;

        try (Repository repository = git.getRepository()) {
            System.out.println("Repository: " + repository.getDirectory());

            int commitNumber = parsedCommitId.size() - 1;
            // 进行处理，统计出修改的行数. oldCommit是最开始的commit, 也即是commitSet分割后的第一个commit
            ArrayList<String> modifiedLinesSets = GitHubRepoService.getModifiedLines(repository, commitNumber);

            return modifiedLinesSets;
        }
    }

    /**
     * @Function 获取所有修改的文件类型
     * @Param
     * */
    @Deprecated
    public static String getModifiedFileTypes(ArrayList<String> changeSet) throws Exception{
        StringBuffer sb = new StringBuffer();

        for (String s : changeSet) {
            int idx1 = s.indexOf("a/");
            int idx2 = s.indexOf("b/");
            // 后缀名切割开
            String[] s1 = s.substring(idx1, idx2).trim().split("\\.");
            for (String s2 : s1) {
                // s2是后缀名
                s2 = s1[1].trim();
                if(s2.equals("Dockerfile")) sb.append(s2 + ", ");
                // 如果已经存在了, 就不往里面加入重复的后缀名
                if(sb.toString().contains(s2)) continue;
                else sb.append("*." + s2);
            }
        }
        return sb.toString();
    }



    /**
     * 处理commitId
     * */
    public static List<String> processCommits(String commits) {
        List<String> parsedCommitId = new LinkedList<>();
        if (commits != null && commits.length() > 0) {
            String[] s1 = commits.split("]");
            for (String string1 : s1) {
                string1 = string1.replace("[", "");
                String[] s2 = string1.split(":");
                parsedCommitId.add(s2[0]);
            }
            return parsedCommitId;
        } else return null;
    }

    /**
     * 通过.git文件夹获取Git对象.
     * 如果iPipeline维护的代码库为空, 新建文件夹并下载最新代码;
     * 非空则获取最新代码
     * */
    public static Git getGit(String rootPath, Build curBuild) throws Exception{

        String repoPath = rootPath + "LocalRepo/" + curBuild.getProject().getProjectName();
        String repoUrl = curBuild.getProject().getGitURL();

        // 新建File对象
        File localPath = new File(repoPath + "/GitResource");
        if(!localPath.isDirectory()) localPath.mkdirs();

        // 新建Git对象
        Git git;

        File gitFile = new File(localPath + "/.git");
        if(gitFile.isDirectory()) {
            git = Git.open(new File(localPath + "/.git"));
        } else {

            // 先把源代码目录删除
            // if (!localPath.delete()) {
            //     throw new IOException("Could not delete temporary file " + localPath);
            // }

            // clone的时候会新建文件目录
            try (Repository repository = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(localPath)
                    .call().getRepository()) {
                git = new Git(repository);
            }
        }

        // 不在这里pull最新代码, 还需要分析代码差异
        // git.pull().call();

        return git;
    }

    /**
     * 生成notification step.
     * 生成的notification这 一个任务 加入List<Step>的队列末尾
     * */
    public static void notificationStep(Build curBuild, String analysis){

        List<Step> steps = curBuild.getSteps();

        // 声明为final
        final Step notification = new Step("mail");

        StepParam param1 = new StepParam("subject", "The Build #" + curBuild.getId() + " has been skipped.");
        param1.setOptional(false);

        String msg = "This build is skipped by DecisionMaker. For " + analysis;

        StepParam param2 = new StepParam("body", msg);
        param2.setOptional(false);

        // 通知邮件的收件人
        StepParam param3 = new StepParam("to", "yangfeng16@otcaix.iscas.ac.cn");
        param3.setOptional(false);

        // 参数也要建立关联关系
        param1.setStep(notification);
        param2.setStep(notification);
        param3.setStep(notification);

        List<StepParam> params = new ArrayList<>();
        params.add(param1);
        params.add(param2);
        params.add(param3);

        String nodeId = String.valueOf(notification.hashCode());

        // 与最后一个步骤建立Relation, 方便构图
        Relation re = new Relation(steps.get(steps.size() - 1).getNodeId(), nodeId, true);
        re.setStep(notification);

        ArrayList<Relation> relations = new ArrayList<>();
        relations.add(re);

        notification.setNodeId(nodeId);
        notification.setStatus(Step.Status.SUCCESS);
        notification.setBuild(curBuild);
        notification.setStageName("Notice");
        notification.setDisplayName("SendingEmail");
        // 最后生成的Email任务的位置坐标
        notification.setxPos(950);
        notification.setyPos(200);
        notification.setStepParams(params);
        notification.setRelations(relations);

        // 将mail步骤加入队列末尾. Add reference
        steps.add(notification);
    }

    public static String judgeFileType(String fileTypes){
        StringBuffer sb = new StringBuffer();

        // 是否包含核心代码
        if(fileTypes.contains(".java") || fileTypes.contains(".cpp")
                || fileTypes.contains(".py")){
            sb.append("CORE");
        }
        // 这里的test不是文件后缀名, 而是测试代码的包名
        if(fileTypes.contains("test")){
            sb.append("TEST");
        }
        if(fileTypes.contains("Dockerfile") || fileTypes.contains(".travis.yml")){
            sb.append("CONFIG");
        }
        // 是否修改了文档
        if(fileTypes.contains(".md") || fileTypes.contains(".txt") ||
                fileTypes.contains(".doc") || fileTypes.contains(".ppt") ||
                fileTypes.contains(".docx") || fileTypes.contains(".yml") ||
                fileTypes.contains(".xml")){
            sb.append("DOCUMENT");
        }
        return sb.toString();
    }

    @Deprecated
    public static int getModifiedLines(ArrayList<String> modifiedLinesSets){
        int count = 0;
        // 增加和减少的行数都加起来，算出一个总数. 可以同时得到修改文件类型和代码行数!
        for (String s : modifiedLinesSets) {
            // 正则匹配模式@@ ... @@ 中间的内容
            String express = "(@@\\s+\\[+-]\\d+\\[,]\\[+-]\\d+\\s+@@)";
            Matcher matcher = Pattern.compile(express).matcher(s);

            String[] s1 = s.split("@@");
            // 空格分隔
            String[] s2 = s1[1].trim().split(" ");
            for (String s3 : s2) {
                String[] s4 = s3.split(",");
                count += Integer.parseInt(s4[1].trim());
            }
        }

        return count;
    }

    // public static void main(String[] args) throws Exception{
    //     ArrayList<String> strs = new ArrayList<>();
    //
    //     String str1 = "diff --git a/README.md b/README.md\n" +
    //             "index 6ec561e..a6d0502 100644\n" +
    //             "--- a/README.md\n" +
    //             "+++ b/README.md\n" +
    //             "@@ -57,0 +58,2 @@\n" +
    //             "+\n" +
    //             "+11";
    //
    //     String str2 = "diff --git a/Hello.java b/Hello.java\n" +
    //             "index 6ec561e..a6d0502 100644\n" +
    //             "--- a/README.md\n" +
    //             "+++ b/README.md\n" +
    //             "@@ -57,0 +58,2 @@\n" +
    //             "+\n" +
    //             "+11";
    //     strs.add(str1);
    //     strs.add(str2);
    //
    //     int num = getModifiedLines(strs);
    //
    //     System.out.println("d");
    // }
}
