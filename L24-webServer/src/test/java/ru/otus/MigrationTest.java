package ru.otus;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateTables() {
        // Проверяем, что таблицы созданы через прямые запросы
        Integer clientCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CLIENT", Integer.class);
        assertThat(clientCount).isEqualTo(5);

        Integer addressCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ADDRESS", Integer.class);
        assertThat(addressCount).isEqualTo(5);

        Integer phoneCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PHONE", Integer.class);
        assertThat(phoneCount).isEqualTo(7);
    }

    @Test
    void shouldHaveInitialData() {
        // Проверяем конкретные данные
        String clientName = jdbcTemplate.queryForObject("SELECT NAME FROM CLIENT WHERE ID = 1", String.class);
        assertThat(clientName).isEqualTo("Иван Иванов");

        String address = jdbcTemplate.queryForObject("SELECT STREET FROM ADDRESS WHERE CLIENT_ID = 1", String.class);
        assertThat(address).isEqualTo("ул. Ленина, д. 10, кв. 5");
    }
}
