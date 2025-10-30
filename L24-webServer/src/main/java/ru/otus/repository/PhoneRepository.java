package ru.otus.repository;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.model.Phone;

public interface PhoneRepository extends CrudRepository<Phone, Long> {

    @Query("SELECT * FROM PHONE WHERE CLIENT_ID = :clientId")
    List<Phone> findByClientId(@Param("clientId") Long clientId);

    @Modifying
    @Query("DELETE FROM PHONE WHERE CLIENT_ID = :clientId")
    void deleteByClientId(@Param("clientId") Long clientId);
}
