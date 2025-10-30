package ru.otus.repository;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.model.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {

    @Query("SELECT * FROM CLIENT WHERE UPPER(NAME) LIKE UPPER(CONCAT('%', :name, '%'))")
    List<Client> findByNameContainingIgnoreCase(@Param("name") String name);
}
