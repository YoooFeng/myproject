package com.iscas.yf.IntelliPipeline.controller;

import com.iscas.yf.IntelliPipeline.dataview.UserView;
import com.iscas.yf.IntelliPipeline.entity.user.User;
import com.iscas.yf.IntelliPipeline.service.dataservice.UserService;
import com.iscas.yf.IntelliPipeline.service.user.SignupCommand;
import com.iscas.yf.IntelliPipeline.service.user.SignupValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SignupController {

    private SignupValidator signupValidator = new SignupValidator();

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String userSignup(SignupCommand command, BindingResult errors) {

        signupValidator.validate(command, errors);

        if( errors.hasErrors() ) {
            return "/login";
        }

        User user = new User();
        user.setPassword( new Sha256Hash(command.getPassword()).toHex() );
        user.setUsername(command.getUsername());
        user.setEmail(command.getEmail());

        userService.createUser( user );

        SecurityUtils.getSubject().login(new UsernamePasswordToken(command.getUsername(), command.getUsername()));

        return "redirect:/project/main";


    }

}
