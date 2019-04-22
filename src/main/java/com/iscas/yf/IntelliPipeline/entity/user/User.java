package com.iscas.yf.IntelliPipeline.entity.user;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springside.modules.utils.Encodes;
import org.springside.modules.security.utils.Digests;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "d_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User extends IdEntity{

    // 已经默认存在Id属性
    private String username;
    private String email;
    private String password;
    // salt用于加密
    private String salt;

    // 一个用户只能属于一种角色
    @ManyToOne
    @JoinTable(name = "users_roles")
    private Role role;

    public User() {

    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Basic(optional = false)
    @Column(length=100)
    public String getUsername() {
        return username;
    }



    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCredentialSalt() {
        return username + salt;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
