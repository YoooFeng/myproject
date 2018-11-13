package com.iscas.yf.IntelliPipeline.common.service.user.security.authorization;


import com.iscas.yf.IntelliPipeline.common.util.Constants;
import com.iscas.yf.IntelliPipeline.service.dataservice.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationHandlerInterceptor extends
        AnonymousHandlerInterceptor {
    private String headerToken = Constants.HTTP_HEADERS_TOKEN;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

//		if (super.preHandle(request, response, handler)) {
//			return true;
//		}
//		String token = getToken(request);
//		if (StringUtils.isEmpty(token)) {
//
//			throw new NotFoundException("unknown user account");
//		}
//		User user = userService.findUserByToken(token);
//		if (user == null) {
//			throw new NotFoundException("unknown user account");
//		}
//		SecurityUtils.setSubject(new Subject(user));
        return true;
    }

    protected String getToken(HttpServletRequest httpRequest) {
        String content = httpRequest.getHeader(headerToken);
        if (StringUtils.isEmpty(content)) {
            return httpRequest.getParameter(headerToken);
        }
        return content;
    }
}
