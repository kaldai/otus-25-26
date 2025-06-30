package ru.otus;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("java:S4738")
public class HelloOtus {

    private static final Logger logger = LoggerFactory.getLogger(HelloOtus.class);

    public static void main(String[] args) {
        ImmutableList<String> names = ImmutableList.of("Alice", "Bob", "Charlie");
        logger.info("Immutable List: {}", names);
    }
}
