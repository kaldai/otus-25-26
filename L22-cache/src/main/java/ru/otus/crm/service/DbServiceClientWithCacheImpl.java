package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

public class DbServiceClientWithCacheImpl implements DBServiceClient {

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final HwCache<Long, Client> cache;

    public DbServiceClientWithCacheImpl(
            TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        this.cache = new MyCache<>();
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

            cache.put(savedClient.getId(), savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client cachedClient = cache.get(id);
        if (cachedClient != null) {
            return Optional.of(cachedClient);
        }

        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            clientOptional.ifPresent(client -> cache.put(client.getId(), client));
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            clientList.forEach(client -> cache.put(client.getId(), client));
            return clientList;
        });
    }
}
