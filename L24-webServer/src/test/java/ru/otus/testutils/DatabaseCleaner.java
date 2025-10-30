package ru.otus.testutils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCleaner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clean() {
        // Простое удаление - CASCADE позаботится о связанных записях
        jdbcTemplate.execute("DELETE FROM CLIENT");

        // Сброс последовательностей
        jdbcTemplate.execute("ALTER TABLE CLIENT ALTER COLUMN ID RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE ADDRESS ALTER COLUMN ID RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE PHONE ALTER COLUMN ID RESTART WITH 1");
    }
}
