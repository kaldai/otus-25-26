package ru.otus.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.model.Phone;

@Repository
public interface PhoneRepository extends CrudRepository<Phone, Long> {}
