package com.iscas.yf.IntelliPipeline.service.decision;

import com.iscas.yf.IntelliPipeline.entity.Commit;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析CommitSet, 包括两次build之间的所有commit记录
 * */
public class CommitSetResolver {

    /**
     * 解析CommitSet
     * 格式: [{commitId} : {entry.author} : {entry.msg}] [...]
     * */
    public static List<Commit> doResolve(String rawCommitSet) {
        List<Commit> commitSet = new ArrayList<>();
        rawCommitSet = rawCommitSet.trim();

        // "] "
        String[] rawCommits = rawCommitSet.split("] ");

        for(String s : rawCommits){
            // 每次处理一个commit
            Commit curCommit = new Commit();
            s = s.trim();
            s = s.replace("[", "");
            s = s.replace("]", "");
            // " : "
            String[] params = s.split(" : ");
            curCommit.setCommitId(params[0].trim());
            curCommit.setAuthor(params[1].trim());
            curCommit.setMsg(params.length < 3 ? "" : params[2].trim());
            commitSet.add(curCommit);

        }
        return commitSet;
    }

    // public static void main(String[] args){
    //     String commitSets = "[1234 : Tom : hello] [5678 : Jerry : world]";
    //     List<Commit> commits = doResolve(commitSets);
    //     System.out.println("");
    // }

}
