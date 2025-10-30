package ru.otus.repository;

import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.model.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {

    @Query("SELECT * FROM ADDRESS WHERE CLIENT_ID = :clientId")
    Optional<Address> findByClientId(@Param("clientId") Long clientId);

    @Modifying
    @Query("DELETE FROM ADDRESS WHERE CLIENT_ID = :clientId")
    void deleteByClientId(@Param("clientId") Long clientId);
}
