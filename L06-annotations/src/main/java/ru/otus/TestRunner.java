package ru.otus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;
import ru.otus.reflection.ReflectionHelper;

public class TestRunner {

    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    public static void runTests(Class<?> testClass) {
        TestResults results = new TestResults();

        // в задании не было указано, что должно быть только по одной аннотации в тестовом классе, соберем список
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();

        // распределяет методы по категориям
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethods.add(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            } else if (method.isAnnotationPresent(After.class)) {
                afterMethods.add(method);
            }
        }

        for (Method testMethod : testMethods) {
            // создает экземпляр класса-теста
            Object testInstance = null;
            try {
                testInstance = ReflectionHelper.instantiate(testClass);
            } catch (Exception e) {
                logger.error("Не удалось создать экземпляр класса-теста: {}", e.getMessage());
            }
            if (testInstance != null) {
                // выполняет последовательность методов
                runTestSequence(testInstance, testMethod, beforeMethods, afterMethods, results);
            } else {
                results.registerFailure(testMethod);
            }
        }

        // выводит результаты
        printStatistics(results, testClass.getSimpleName());
    }

    // выполняет последовательность методов
    private static void runTestSequence(
            Object testInstance,
            Method testMethod,
            List<Method> beforeMethods,
            List<Method> afterMethods,
            TestResults results) {
        try {
            executeMethods(testInstance, beforeMethods);
            executeTestMethod(testInstance, testMethod, results);
        } catch (Exception e) {
            results.registerFailure(testMethod);
        } finally {
            executeMethods(testInstance, afterMethods);
        }
    }

    // выполняет список методов
    private static void executeMethods(Object instance, List<Method> methods) {
        for (Method method : methods) {
            try {
                ReflectionHelper.callMethod(instance, method.getName());
            } catch (Exception e) {
                logger.error("Ошибка в {}: {}", method.getName(), e.getCause().getMessage());
                throw new RuntimeException("Ошибка при выполнении теста", e);
            }
        }
    }

    private static void executeTestMethod(Object testInstance, Method testMethod, TestResults results) {
        try {
            ReflectionHelper.callMethod(testInstance, testMethod.getName());
            results.registerSuccess();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении теста", e);
        }
    }

    // выводит результаты
    private static void printStatistics(TestResults results, String className) {
        logger.info("Результаты теста {}:", className);
        logger.info("Сколько прошло успешно: {}", results.getPassedCount());
        logger.info("Сколько упало тестов: {}", results.getFailedCount());
        if (!results.getFailedTests().isEmpty()) {
            logger.info("Не пройденные тесты:");
            results.getFailedTests().forEach(test -> logger.info("{}", test));
        }
        logger.info("Сколько было всего тестов: {}", results.getTotal());
    }

    static class TestResults {
        private int passedCount = 0;
        private final List<String> failedTests = new ArrayList<>();

        public void registerSuccess() {
            passedCount++;
        }

        public void registerFailure(Method method) {
            failedTests.add(method.getName());
        }

        public int getTotal() {
            return passedCount + failedTests.size();
        }

        public int getPassedCount() {
            return passedCount;
        }

        public int getFailedCount() {
            return failedTests.size();
        }

        public List<String> getFailedTests() {
            return failedTests;
        }
    }
}
