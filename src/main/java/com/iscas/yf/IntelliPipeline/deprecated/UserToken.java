// package com.iscas.yf.IntelliPipeline.entity.user;
//
// import com.iscas.yf.IntelliPipeline.common.entity.IdEntity;
// import com.iscas.yf.IntelliPipeline.common.util.Uniques;
//
// import javax.persistence.*;
//
// @Entity
// @Table(name = "d_user_token")
// public class UserToken extends IdEntity {
//
//     @OneToOne
//     @JoinColumn(name = "user_id")
//     private User user;
//
//     @Column(unique = true, nullable = false)
//     private String content;
//
//     public UserToken(){
//         this.content = Uniques.getUniqueString();
//     }
//     public User getUser() {
//         return user;
//     }
//
//     public void setUser(User user) {
//         this.user = user;
//     }
//
//     public String getContent() {
//         return content;
//     }
//
//     public void setContent(String content) {
//         this.content = content;
//     }
//
//     public void refreshContent() {
//         this.content = Uniques.getUniqueString();
//     }
// }