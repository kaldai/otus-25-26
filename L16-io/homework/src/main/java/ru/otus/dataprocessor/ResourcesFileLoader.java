package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {
    private final String fileName;
    private final ObjectMapper objectMapper;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Measurement> load() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileProcessException("File not found: " + fileName);
            }
            return objectMapper.readValue(
                    inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, Measurement.class));
        } catch (Exception e) {
            throw new FileProcessException("Error loading file: " + fileName, e);
        }
    }
}
