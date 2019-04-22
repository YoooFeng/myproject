package com.iscas.yf.IntelliPipeline.dao;


import com.iscas.yf.IntelliPipeline.dataview.UserView;
import com.iscas.yf.IntelliPipeline.entity.user.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserDAO {

    User getUser(Long userId);

    User findByName(String username);

    void createUser(User user);

    List<User> getAllUsers();

    void deleteUser(Long userId);

    void updateUser(User user);

}
