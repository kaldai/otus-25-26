package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import ru.otus.cachehw.HwCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

public class DbServiceClientWithCacheImpl implements DBServiceClient {
    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final HwCache<String, Client> cache; // Используем String вместо Long

    public DbServiceClientWithCacheImpl(
            TransactionManager transactionManager,
            DataTemplate<Client> clientDataTemplate,
            HwCache<String, Client> cache) { // Кэш передаем как зависимость
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        this.cache = cache;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            Client savedClient;

            if (client.getId() == null) {
                savedClient = clientDataTemplate.insert(session, clientCloned);
            } else {
                savedClient = clientDataTemplate.update(session, clientCloned);
            }

            // Используем строковый ключ
            cache.put(String.valueOf(savedClient.getId()), savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        // Используем строковый ключ
        String key = String.valueOf(id);
        Client cachedClient = cache.get(key);
        if (cachedClient != null) {
            return Optional.of(cachedClient);
        }

        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            clientOptional.ifPresent(client -> cache.put(String.valueOf(client.getId()), client));
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            clientList.forEach(client -> cache.put(String.valueOf(client.getId()), client));
            return clientList;
        });
    }
}
