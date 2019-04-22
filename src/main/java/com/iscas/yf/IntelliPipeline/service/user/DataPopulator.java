package com.iscas.yf.IntelliPipeline.service.user;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class DataPopulator implements InitializingBean{

    private DataSource dataSource;

    @SuppressWarnings({"FieldCanBeLocal"})
    private SessionFactory sessionFactory;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void afterPropertiesSet() throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);

        // 项目启动时初始化admin帐号及其相关权限
        jdbcTemplate.execute("INSERT INTO d_role values (1, 'user', '普通用户') ");
        jdbcTemplate.execute("INSERT INTO d_role values (2, 'admin', '管理员用户') ");
        jdbcTemplate.execute("INSERT INTO roles_permissions values (2, 'user:*') ");
        jdbcTemplate.execute("INSERT INTO d_user(id, username, email, password) values (1, 'admin', 'admin@IntelliPipeline.com', " +
                "'"+ new Sha256Hash("admin").toHex() + "') ");
        jdbcTemplate.execute("INSERT INTO users_roles values (1, 2)");

    }

}
