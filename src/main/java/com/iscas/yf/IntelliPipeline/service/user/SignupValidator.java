package com.iscas.yf.IntelliPipeline.service.user;

import org.apache.shiro.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

public class SignupValidator implements Validator {

    private static final String SIMPLE_EMAIL_REGEX = "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z{2,4}]";

    public boolean supports(Class aClass) {
        return SignupCommand.class.isAssignableFrom(aClass);
    }

    public void validate(Object o, Errors errors) {

        SignupCommand command = (SignupCommand) o;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "error.username.empty", "用户名不能为空!");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.email.empty", "邮箱不能为空!");

        if(StringUtils.hasText( command.getEmail() ) && !Pattern.matches( SIMPLE_EMAIL_REGEX, command.getEmail().toUpperCase() )) {
            errors.rejectValue( "email", "error.email.invalid", "邮箱格式错误, 请重新输入!");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.password.empty", "密码不能为空!");

    }
}
