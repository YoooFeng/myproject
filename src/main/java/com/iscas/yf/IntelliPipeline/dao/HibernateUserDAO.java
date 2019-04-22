package com.iscas.yf.IntelliPipeline.dao;

import com.iscas.yf.IntelliPipeline.dataview.UserView;
import com.iscas.yf.IntelliPipeline.entity.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userDAO")
@SuppressWarnings("unchecked")
public class HibernateUserDAO extends HibernateDAO implements UserDAO {

    public User getUser(Long userId) {
        return (User) getSession().get(User.class, userId);
    }

    public User findByName(String username) {
        String query = "from d_user u where u.username = :username";
        return (User) getSession().createQuery(query).setString("username", username).uniqueResult();
    }

    public void createUser(User user){
        getSession().save(user);
    }

    public List<User> getAllUsers() {
        return getSession().createQuery("from d_user order by username").list();
    }

    public void deleteUser(Long userId) {
        User user = getUser(userId);
        if( user != null) {
            getSession().delete(user);
        }
    }

    public void updateUser(User user) {
        getSession().update(user);
    }
}
