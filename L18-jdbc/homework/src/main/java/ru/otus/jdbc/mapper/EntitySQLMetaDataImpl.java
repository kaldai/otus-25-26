package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.StringJoiner;

@SuppressWarnings({"java:S1452"})
public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final EntityClassMetaData<?> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    public EntityClassMetaData<?> getEntityClassMetaData() {
        return entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        return String.format("SELECT * FROM %s", entityClassMetaData.getName());
    }

    @Override
    public String getSelectByIdSql() {
        return String.format(
                "SELECT * FROM %s WHERE %s = ?",
                entityClassMetaData.getName(), entityClassMetaData.getIdField().getName());
    }

    @Override
    public String getInsertSql() {
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner values = new StringJoiner(", ");
        for (Field field : entityClassMetaData.getFieldsWithoutId()) {
            columns.add(field.getName());
            values.add("?");
        }

        return String.format("INSERT INTO %s (%s) VALUES (%s)", entityClassMetaData.getName(), columns, values);
    }

    @Override
    public String getUpdateSql() {
        StringJoiner setClause = new StringJoiner(", ");

        for (Field field : entityClassMetaData.getFieldsWithoutId()) {
            setClause.add(field.getName() + " = ?");
        }

        return String.format(
                "UPDATE %s SET %s WHERE %s = ?",
                entityClassMetaData.getName(),
                setClause,
                entityClassMetaData.getIdField().getName());
    }
}
