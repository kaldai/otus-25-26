package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    @SuppressWarnings("unchecked")
    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;

        if (entitySQLMetaData instanceof EntitySQLMetaDataImpl impl) {
            this.entityClassMetaData = (EntityClassMetaData<T>) impl.getEntityClassMetaData();
        } else {
            throw new IllegalArgumentException("EntitySQLMetaData must be an instance of EntitySQLMetaDataImpl");
        }
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(
                connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), this::mapResultSetToEntity);
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(
                        connection, entitySQLMetaData.getSelectAllSql(), List.of(), this::mapResultSetToEntityList)
                .orElseThrow(() -> new DataTemplateException(new RuntimeException("Unexpected error")));
    }

    @Override
    public long insert(Connection connection, T entity) {
        try {
            List<Object> params = getFieldValuesWithoutId(entity);
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        try {
            List<Object> params = new ArrayList<>(getFieldValuesWithoutId(entity));
            params.add(getIdValue(entity));
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private T mapResultSetToEntity(ResultSet rs) {
        try {
            if (rs.next()) {
                T entity = entityClassMetaData.getConstructor().newInstance();
                setFieldValuesFromResultSet(entity, rs);
                return entity;
            }
            return null;
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private List<T> mapResultSetToEntityList(ResultSet rs) {
        try {
            List<T> entities = new ArrayList<>();
            while (rs.next()) {
                T entity = entityClassMetaData.getConstructor().newInstance();
                setFieldValuesFromResultSet(entity, rs);
                entities.add(entity);
            }
            return entities;
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private void setFieldValuesFromResultSet(T entity, ResultSet rs) throws Exception {
        for (Field field : entityClassMetaData.getAllFields()) {
            field.setAccessible(true);
            Object value = rs.getObject(field.getName());
            field.set(entity, value);
        }
    }

    private List<Object> getFieldValuesWithoutId(T entity) throws Exception {
        List<Object> values = new ArrayList<>();
        for (Field field : entityClassMetaData.getFieldsWithoutId()) {
            field.setAccessible(true);
            values.add(field.get(entity));
        }
        return values;
    }

    private Object getIdValue(T entity) throws Exception {
        Field idField = entityClassMetaData.getIdField();
        idField.setAccessible(true);
        return idField.get(entity);
    }
}
