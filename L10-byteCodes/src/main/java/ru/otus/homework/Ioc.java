package ru.otus.homework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class Ioc {

    private static final Logger logger = LoggerFactory.getLogger(Ioc.class);

    @SuppressWarnings("unchecked")
    public static <T> T createMyClass(Class<T> interfaceClass, T target) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[] {interfaceClass},
                new LoggingInvocationHandler(interfaceClass, target));
    }

    private static class LoggingInvocationHandler implements InvocationHandler {
        private final Object target;
        private final Map<Method, Boolean> loggingMethods = new HashMap<>();
        private final Set<String> methodsToLog = new HashSet<>();

        LoggingInvocationHandler(Class<?> interfaceClass, Object target) {
            this.target = target;
            cacheLoggingMethods(interfaceClass);
        }

        private void cacheLoggingMethods(Class<?> interfaceClass) {
            // Собираем сигнатуры методов с аннотацией @Log в целевом классе
            for (Method method : target.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Log.class)) {
                    String signature = methodSignature(method);
                    methodsToLog.add(signature);
                }
            }

            // Сопоставляем методы интерфейса с закэшированными сигнатурами
            for (Method method : interfaceClass.getMethods()) {
                String signature = methodSignature(method);
                loggingMethods.put(method, methodsToLog.contains(signature));
            }
        }

        private String methodSignature(Method method) {
            return method.getName()
                    + Arrays.toString(Arrays.stream(method.getParameterTypes())
                            .map(Class::getName)
                            .toArray(String[]::new));
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (loggingMethods.getOrDefault(method, false)) {
                logMethodCall(method, args);
            }
            return method.invoke(target, args);
        }

        private void logMethodCall(Method method, Object[] args) {
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("executed method: ").append(method.getName()).append(", params: ");

            if (args != null) {
                String params = String.join(
                        ", ", Arrays.stream(args).map(String::valueOf).toArray(String[]::new));
                logMessage.append(params);
            }

            logger.info(logMessage.toString());
        }
    }
}
