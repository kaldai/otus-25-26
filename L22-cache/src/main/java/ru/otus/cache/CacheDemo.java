package ru.otus.cache;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DbServiceClientWithCacheImpl;

public class CacheDemo {
    private static final Logger log = LoggerFactory.getLogger(CacheDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        var dbServiceClient = new DbServiceClientWithCacheImpl(transactionManager, clientTemplate);
        testCachePerformance(dbServiceClient);
    }

    private static void testCachePerformance(DbServiceClientWithCacheImpl dbServiceClient) {
        var client = new Client("Test Client");
        var savedClient = dbServiceClient.saveClient(client);
        long clientId = savedClient.getId();

        long startTime = System.nanoTime();
        dbServiceClient.getClient(clientId);
        long dbTime = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        dbServiceClient.getClient(clientId);
        long cacheTime = System.nanoTime() - startTime;

        log.info("DB retrieval time: {} ns", dbTime);
        log.info("Cache retrieval time: {} ns", cacheTime);
        log.info("Cache is {} times faster", (double) dbTime / cacheTime);
    }
}
