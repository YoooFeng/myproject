package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dataview.UserView;
import com.iscas.yf.IntelliPipeline.entity.user.User;

import java.util.List;

public interface UserService {

    User getCurrentUser();

    User createUser(User user);

    User getUser(Long userId);

    void deleteUser(Long userId);

    User updateUser(User user);

    List<User> getAllUsers();

    User findById(Long id);

}
