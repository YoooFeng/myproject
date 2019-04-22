package com.iscas.yf.IntelliPipeline.entity.user;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="d_role")
public class Role extends IdEntity {

    private String name;
    private String description;

    // 一个角色下可能有多个用户
    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, mappedBy = "role", fetch = FetchType.LAZY)
    private Set<User> users;

    @ElementCollection(targetClass = String.class)
    @JoinTable(name="roles_permissions")
    private Set<String> permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
