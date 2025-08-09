package ru.otus.homework.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.Log;
import ru.otus.homework.TestLogging;

public class TestLoggingImpl implements TestLogging {

    private static final Logger logger = LoggerFactory.getLogger(TestLoggingImpl.class);

    @Log
    @Override
    public void method(int intParam) {
        logger.debug("method with one intParam: {}", intParam);
    }

    @Override
    public void method(int intParam1, int intParam2) {
        logger.debug("method with two params: {}, {}", intParam1, intParam2);
    }

    @Log
    @Override
    public void method(int intParam1, int intParam2, String stringParam3) {
        logger.debug("method with three params: {}, {}, {}", intParam1, intParam2, stringParam3);
    }

    @Log
    @Override
    public void method(int intParam1, int intParam2, String stringParam3, Object objectaram4) {
        logger.debug("method with three params: {}, {}, {}, {}", intParam1, intParam2, stringParam3, objectaram4);
    }
}
