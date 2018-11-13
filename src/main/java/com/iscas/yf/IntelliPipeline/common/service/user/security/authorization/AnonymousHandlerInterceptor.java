package com.iscas.yf.IntelliPipeline.common.service.user.security.authorization;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AnonymousHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception{
        Method method = ((HandlerMethod) handler).getMethod();
        Anonymous anonymous = method.getAnnotation(Anonymous.class);
        if(anonymous != null) return true;
        return false;
    }
}
