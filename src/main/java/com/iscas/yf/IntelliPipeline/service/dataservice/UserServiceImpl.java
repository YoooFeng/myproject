package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dao.HibernateDAO;
import com.iscas.yf.IntelliPipeline.dao.UserDAO;
import com.iscas.yf.IntelliPipeline.dataview.UserView;
import com.iscas.yf.IntelliPipeline.entity.user.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service("userService")
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User getCurrentUser() {
        final Long currentUserId = (Long)SecurityUtils.getSubject().getPrincipal();

        if(currentUserId != null) {
            return getUser(currentUserId);
        } else {
            return null;
        }
    }

    public User createUser(User user) {

        userDAO.createUser( user );

        return user;

    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public User getUser(Long userId) {
        return userDAO.getUser( userId );
    }

    public void deleteUser(Long userId) {
        userDAO.deleteUser( userId );
    }

    public User updateUser(User user) {

        userDAO.updateUser( user );

        return user;
    }

    public User findById(Long id) {
        User user = userDAO.getUser(id);
        return user;
    }

}
