package com.iscas.yf.IntelliPipeline.service.util;

import com.iscas.yf.IntelliPipeline.entity.Project;
import com.iscas.yf.IntelliPipeline.entity.record.BuildRecord;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.Service;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.List;

public class HQLUtils {

    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;

    /**
     * @Param: sql - SQL语句, 用来在数据库中查询目标对象.
     * @Param: target - class对象, 用来封装查询到的Java POJO对象.
     * */
    public static List<Project> getResultFromSQL(String sql, Class target) {

        Session session = buildSessionFactory().openSession();

        SQLQuery query = session.createSQLQuery(sql);

        if(target != null) {
            // Hibernate将查询结果封装成POJO对象?
            query.addEntity(target);
        }

        // 返回查询到的满足条件的所有record对象. Unchecked!
        List<Project> records = query.list();

        return records;
    }

    // 第一次使用这个
    public static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            return new Configuration().configure().buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        sessionFactory = new Configuration().configure().buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }

    public static void main(String[] args) throws Exception {
        String sql = "select * from d_project where project_name = 'Shipping-Test'";

        // String sql = "select * from d_record where "
        List<Project> list = getResultFromSQL(sql, Project.class);

        System.out.println(list.size());
    }

}
