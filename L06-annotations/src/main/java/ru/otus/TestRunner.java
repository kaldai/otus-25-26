package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;
import ru.otus.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

  private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

  public static void runTests(Class<?> testClass) {
    TestResults results = new TestResults();

    // в задании не было указано, что должно быть только по одной аннотации в тестовом классе, соберем список
    List<Method> beforeMethods = new ArrayList<>();
    List<Method> testMethods = new ArrayList<>();
    List<Method> afterMethods = new ArrayList<>();

    // распределяет методы по категориям
    categorizeMethods(testClass, beforeMethods, testMethods, afterMethods);

    for (Method testMethod : testMethods) {
      // создает экземпляр класса-теста
      Object testInstance = null;
      try {
        testInstance = ReflectionHelper.instantiate(testClass);
      } catch (Exception e) {
        logger.error("Не удалось создать экземпляр класса-теста: {}", e.getMessage());
      }
      if (testInstance == null) {
        results.registerFailure(testMethod);
        continue;
      }

      // выполняет последовательность методов
      runTestSequence(testInstance, testMethod, beforeMethods, afterMethods, results);
    }

    // выводит результаты
    printStatistics(results, testClass.getSimpleName());
  }

  // распределяет методы по категориям
  private static void categorizeMethods(
      Class<?> testClass, List<Method> beforeMethods, List<Method> testMethods, List<Method> afterMethods) {
    for (Method method : testClass.getDeclaredMethods()) {
      if (method.isAnnotationPresent(Before.class)) {
        beforeMethods.add(method);
      } else if (method.isAnnotationPresent(Test.class)) {
        testMethods.add(method);
      } else if (method.isAnnotationPresent(After.class)) {
        afterMethods.add(method);
      }
    }
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