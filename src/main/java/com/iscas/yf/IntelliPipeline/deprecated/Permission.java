package com.iscas.yf.IntelliPipeline.deprecated;

import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;

import javax.persistence.*;
import java.util.Set;

// @Deprecated
// @Entity
// @Table(name = "d_permission")
// public class Permission extends IdEntity{
//
//     @Column(length = 50, nullable = false, name="pname")
//     private String name;
//
//     @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "pmss")
//     private Set<Role> roles;
//
//     public String getName() {
//         return name;
//     }
//
//     public void setName(String name) {
//         this.name = name;
//     }
//
//     public Set<Role> getRoles() {
//         return roles;
//     }
//
//     public void setRoles(Set<Role> roles) {
//         this.roles = roles;
//     }
// }
