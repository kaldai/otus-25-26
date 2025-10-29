package ru.otus.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.model.Address;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {}
