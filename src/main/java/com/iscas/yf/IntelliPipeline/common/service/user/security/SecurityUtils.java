package com.iscas.yf.IntelliPipeline.common.service.user.security;

@Deprecated
public class SecurityUtils {
    private static ThreadLocal<Subject> subject = new ThreadLocal<>();

    public static void setSubject(Subject threadLocalSubject) {
        subject.set(threadLocalSubject);
    }

    public static Subject getSubject() {
        return subject.get();
    }
}
