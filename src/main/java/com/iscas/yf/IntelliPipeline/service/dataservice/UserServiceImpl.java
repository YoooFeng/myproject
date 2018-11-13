package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dao.UserDAO;
import com.iscas.yf.IntelliPipeline.dataview.UserView;
import com.iscas.yf.IntelliPipeline.entity.user.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {

    private Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    UserDAO userDAO;

    public User createUser(UserView.Item userItem){
        User user = new User();

        user.setName(userItem.name);
        user.setPassword(userItem.password);
        user.setEmail(userItem.email);

        userDAO.save(user);

        return user;
    }
}
