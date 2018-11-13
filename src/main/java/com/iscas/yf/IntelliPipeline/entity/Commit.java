package com.iscas.yf.IntelliPipeline.entity;

public class Commit {

    // commit hash
    private String commitId;

    // author of a commit
    private String author;

    // commit msg
    private String msg;

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
