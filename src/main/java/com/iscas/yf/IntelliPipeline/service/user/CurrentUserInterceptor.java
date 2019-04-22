package com.iscas.yf.IntelliPipeline.service.user;

import com.iscas.yf.IntelliPipeline.entity.user.User;
import com.iscas.yf.IntelliPipeline.service.dataservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CurrentUserInterceptor extends HandlerInterceptorAdapter {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {
        User currentUser = userService.getCurrentUser();
        if( currentUser != null) {
            httpServletRequest.setAttribute("currentUser", currentUser);
        }
    }
}