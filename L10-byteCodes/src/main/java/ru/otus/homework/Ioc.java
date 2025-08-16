package ru.otus.homework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class Ioc {

    private static final Logger logger = LoggerFactory.getLogger(Ioc.class);

    @SuppressWarnings("unchecked")
    public static <T> T createMyClass(Class<T> interfaceClass, T target) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass}, new DemoInvocationHandler(target));
    }

    private static class DemoInvocationHandler implements InvocationHandler {
        private final Object target;
        private final Class<?> targetClass;

        DemoInvocationHandler(Object target) {
            this.target = target;
            this.targetClass = target.getClass();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method realMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());

            if (realMethod.isAnnotationPresent(Log.class)) {
                logMethodCall(realMethod, args);
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
