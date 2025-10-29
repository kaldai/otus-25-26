package ru.otus.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.model.Client;
import ru.otus.service.ClientService;

@SpringBootTest
@AutoConfigureMockMvc
class ClientApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientService clientService;

    @Test
    void shouldGetClientById() throws Exception {
        Client client = clientService.createClient(new Client("Мария Кузнецова"));

        mockMvc.perform(get("/api/clients/{id}", client.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Мария Кузнецова"));
    }
}
