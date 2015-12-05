package com.enigmastation.ml.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private static final SessionFactory factory;

    static {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static Session getSession() {
        return factory.openSession();
    }
}
