package ru.otus.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.impl.TestLoggingImpl;

public class ProxyDemo {
    private static final Logger logger = LoggerFactory.getLogger(ProxyDemo.class);

    public static void main(String[] args) {

        logger.info("Hello World!");
        TestLogging testLogging = Ioc.createMyClass(TestLogging.class, new TestLoggingImpl());

        testLogging.method(6);
        testLogging.method(6, 7);
        testLogging.method(6, 7, "World");
        testLogging.method(6, 7, "World", testLogging);
        logger.info("Goodbye World!");
    }
}
