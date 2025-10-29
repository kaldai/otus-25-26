package ru.otus.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.model.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {}
