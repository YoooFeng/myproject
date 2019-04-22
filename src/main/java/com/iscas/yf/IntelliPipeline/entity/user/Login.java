package com.iscas.yf.IntelliPipeline.entity.user;

/**
 * 用于登录会话的POJO对象
 * */
@Deprecated
public class Login {
    private String userName;
    private String userPassword;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
