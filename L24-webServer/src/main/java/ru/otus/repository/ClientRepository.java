package ru.otus.repository;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.model.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {

    List<Client> findByNameContainingIgnoreCase(String name);

    @Query("SELECT * FROM CLIENT WHERE UPPER(NAME) LIKE UPPER(CONCAT('%', :name, '%'))")
    List<Client> customFindByName(@Param("name") String name);
}
