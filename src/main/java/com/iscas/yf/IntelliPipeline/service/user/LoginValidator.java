package com.iscas.yf.IntelliPipeline.service.user;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class LoginValidator implements Validator{

    public boolean supports(Class aClass) {
        return LoginCommand.class.isAssignableFrom(aClass);
    }

    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "error.username.empty",
                "用户名为空, 请输入用户名!");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.password.empty",
                "密码为空, 请输入密码!");
    }

}
