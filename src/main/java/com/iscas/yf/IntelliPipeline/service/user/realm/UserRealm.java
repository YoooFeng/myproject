package com.iscas.yf.IntelliPipeline.service.user.realm;

import com.iscas.yf.IntelliPipeline.dao.HibernateUserDAO;
import com.iscas.yf.IntelliPipeline.entity.user.Role;
import com.iscas.yf.IntelliPipeline.entity.user.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {

    private HibernateUserDAO hibernateUserDAO;

    @Autowired
    public void setHibernateUserDAO(HibernateUserDAO hibernateUserDAO) {
        this.hibernateUserDAO = hibernateUserDAO;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        Long userId = (Long)principals.fromRealm(getName()).iterator().next();

        User user = hibernateUserDAO.getUser(userId);

        if(user == null) {
            throw new UnknownAccountException();//没找到帐号
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Role role = user.getRole();
        info.addRole(role.getName());
        info.addStringPermissions( role.getPermissions() );
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

        User user = hibernateUserDAO.findByName(token.getUsername());

        if(user == null) {
            throw new UnknownAccountException();//没找到帐号
        }

        return new SimpleAuthenticationInfo(user.getId(), user.getPassword(), user.getUsername());
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }

}
