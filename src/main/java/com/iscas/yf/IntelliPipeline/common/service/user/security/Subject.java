package com.iscas.yf.IntelliPipeline.common.service.user.security;

import com.iscas.yf.IntelliPipeline.entity.user.User;

public class Subject {
    private User user;

    public Subject(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
