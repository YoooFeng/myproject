package com.iscas.yf.IntelliPipeline.service.user;

import org.apache.shiro.util.StringUtils;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import java.util.regex.Pattern;

public class EditUserValidator implements Validator{

    private static final String SIMPLE_EMAIL_REGEX = "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z{2,4}]";

    public boolean supports(Class aClass) {
        return EditUserValidator.class.isAssignableFrom(aClass);
    }

    public void validate(Object o, Errors errors) {

        EditUserCommand command = (EditUserCommand) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "error.username.empty", "用户名不能为空!");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.email.empty", "邮箱不能为空!");

        if(StringUtils.hasText( command.getEmail() ) && !Pattern.matches( SIMPLE_EMAIL_REGEX, command.getEmail().toUpperCase() )) {
            errors.rejectValue( "email", "error.email.invalid", "邮箱格式错误, 请重新输入!");
        }

    }


}
