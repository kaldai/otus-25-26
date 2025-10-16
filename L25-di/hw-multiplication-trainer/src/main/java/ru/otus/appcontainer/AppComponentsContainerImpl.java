package ru.otus.appcontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings({"squid:S1068", "java:S112", "java:S6204"})
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();
    private final Map<Class<?>, List<Object>> appComponentsByType = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        try {
            Object configInstance = configClass.getDeclaredConstructor().newInstance();
            List<Method> componentMethods = getComponentMethods(configClass);

            for (Method method : componentMethods) {
                AppComponent annotation = method.getAnnotation(AppComponent.class);
                String componentName = annotation.name();

                if (appComponentsByName.containsKey(componentName)) {
                    throw new RuntimeException("Component with name '" + componentName + "' already exists");
                }

                Object[] dependencies = resolveDependencies(method);

                Object component = method.invoke(configInstance, dependencies);

                appComponents.add(component);
                appComponentsByName.put(componentName, component);

                Class<?> componentType = method.getReturnType();
                appComponentsByType
                        .computeIfAbsent(componentType, k -> new ArrayList<>())
                        .add(component);

                Class<?> implementationClass = component.getClass();
                appComponentsByType
                        .computeIfAbsent(implementationClass, k -> new ArrayList<>())
                        .add(component);
            }

        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            throw new RuntimeException("Failed to process configuration", e);
        }
    }

    private List<Method> getComponentMethods(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(
                        m -> m.getAnnotation(AppComponent.class).order()))
                .collect(Collectors.toList());
    }

    private Object[] resolveDependencies(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] dependencies = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            dependencies[i] = getAppComponent(parameterType);
        }

        return dependencies;
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> components = appComponentsByType.get(componentClass);

        if (components == null || components.isEmpty()) {
            List<Object> candidates =
                    appComponents.stream().filter(componentClass::isInstance).collect(Collectors.toList());

            if (candidates.isEmpty()) {
                throw new RuntimeException("Component not found for type: " + componentClass.getName());
            } else {
                appComponentsByType.put(componentClass, candidates);
                components = candidates;
            }
        }

        if (components.size() == 1) {
            return (C) components.getFirst();
        } else {
            throw new RuntimeException("Multiple components found for type: " + componentClass.getName()
                    + ". Use getAppComponent(String) to specify which one you need.");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        Object component = appComponentsByName.get(componentName);
        if (component == null) {
            throw new RuntimeException("Component not found with name: " + componentName);
        }
        return (C) component;
    }
}
