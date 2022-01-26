package com.dev;

import com.dev.objects.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import java.util.*;

/**
 * Test config that turn on H2 in-memory database.
 * This mode is more convenient for fast starting.
 * Change property spring.profiles.active to "test" for run application in this mode.
 */

@Configuration
@Profile("production")
public class TestConfig {

    @Bean
    public Properties dataSource() throws Exception {
        Properties settings = new Properties();
        settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
        settings.put(Environment.URL, "jdbc:mysql://localhost:3306/ShopsSchema?useSSL=false&amp;useUnicode=true&amp;characterEncoding=utf8");
        settings.put(Environment.USER, "root");
        settings.put(Environment.PASS, "Eliraz24579&");
        settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        settings.put(Environment.SHOW_SQL, "true");
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(Environment.HBM2DDL_AUTO, "update");
        settings.put(Environment.ENABLE_LAZY_LOAD_NO_TRANS, true);
        return settings;
    }

    @Bean
    public SessionFactory sessionFactory() throws Exception {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration.setProperties(dataSource());

        List<ClassLoader> classLoaderList = new LinkedList<ClassLoader>();
        classLoaderList.add(ClasspathHelper.contextClassLoader());
        classLoaderList.add(ClasspathHelper.staticClassLoader());

//        Reflections reflections= new Reflections(new ConfigurationBuilder()
//                .setScanners(new SubTypesScanner(false, new), new Resources()
//                        .setU
//                ));

        Set<Class<? extends Object>> entities = new Reflections("com.dev.objects").getSubTypesOf(Object.class);
        for (Class<? extends Object> clazz : entities) {
            configuration.addAnnotatedClass((clazz));
        }
        configuration.addAnnotatedClass(UserObject.class);
        configuration.addAnnotatedClass(OrganizationObject.class);
        configuration.addAnnotatedClass(SaleObject.class);
        configuration.addAnnotatedClass(ShopObjects.class);
        configuration.addAnnotatedClass(SalesOrganizations.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }


    @Bean
    public HibernateTransactionManager transactionManager() throws Exception{
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory());
        return transactionManager;
    }


}