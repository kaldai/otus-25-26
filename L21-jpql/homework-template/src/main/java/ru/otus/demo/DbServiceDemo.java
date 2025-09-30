package ru.otus.demo;

import java.util.List;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);

        // Создаем клиента с адресом и телефонами
        var address = new Address("ул. Ленина, д. 1");
        var phones = List.of(new Phone("+7-111-222-33-44"), new Phone("+7-555-666-77-88"));

        var client = new Client("Иван Иванов");
        client.setAddress(address);
        client.setPhones(phones);

        // Сохраняем клиента (каскадно сохранятся адрес и телефоны)
        var savedClient = dbServiceClient.saveClient(client);
        log.info("Saved client: {}", savedClient);

        // Получаем клиента по ID
        var foundClient = dbServiceClient
                .getClient(savedClient.getId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        log.info("Found client: {}", foundClient);

        // Получаем всех клиентов
        var allClients = dbServiceClient.findAll();
        log.info("All clients:");
        allClients.forEach(c -> log.info("Client: {}", c));
    }
}
