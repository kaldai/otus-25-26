package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import ru.otus.crm.annotations.Id;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;
    private final String name;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
        this.name = clazz.getSimpleName().toLowerCase();
        this.constructor = getDefaultConstructor();
        this.allFields = getAllFieldsList();
        this.idField = findIdField();
        this.fieldsWithoutId = getFieldsWithoutIdList();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }

    private Constructor<T> getDefaultConstructor() {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No default constructor found for class: " + clazz.getName(), e);
        }
    }

    private List<Field> getAllFieldsList() {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true);
                fields.add(field);
            }
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    private Field findIdField() {
        return allFields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("No field with @Id annotation found in class: " + clazz.getName()));
    }

    private List<Field> getFieldsWithoutIdList() {
        List<Field> fields = new ArrayList<>(allFields);
        fields.remove(idField);
        return fields;
    }
}
