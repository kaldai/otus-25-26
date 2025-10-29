package ru.otus.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.model.Address;
import ru.otus.model.Client;

@SpringBootTest
class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Test
    void shouldCreateClientWithAddress() {
        Client client = new Client();
        client.setName("Анна Сидорова");

        Address address = new Address();
        address.setStreet("ул. Ленина, 10");
        address.setCity("Москва");
        address.setPostalCode("123456");
        // Устанавливаем двустороннюю связь
        address.setClient(client);
        client.getAddresses().add(address);

        Client saved = clientService.createClient(client);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAddresses()).hasSize(1);
        assertThat(saved.getAddresses().get(0).getStreet()).isEqualTo("ул. Ленина, 10");
    }
}
