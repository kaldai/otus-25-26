package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class TestRunnerTest {

    private static final Logger logger = LoggerFactory.getLogger(TestRunnerTest.class);

    @Before
    public void setUp() {
        logger.info("setUp теста");
    }

    @After
    public void tearDown() {
        logger.info("tearDown теста");
    }

    @Test
    public void successfulTest() {
        logger.info("Тест пройден успешно");
    }

    @Test
    public void failingTest() {
        logger.info("Выполнение неудачного теста");
        throw new RuntimeException("Ошибка при выполнении теста");
    }
}