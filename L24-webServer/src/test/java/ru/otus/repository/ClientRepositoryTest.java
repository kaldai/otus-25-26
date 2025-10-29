package ru.otus.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.model.Client;

@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void shouldSaveAndFindClient() {
        Client client = new Client("Иван Иванов");
        clientRepository.save(client);

        Client found = clientRepository.findById(client.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Иван Иванов");
    }
}
