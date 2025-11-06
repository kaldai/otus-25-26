package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final List<SensorData> dataBuffer;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.dataBuffer = new CopyOnWriteArrayList<>();
    }

    @Override
    public void process(SensorData data) {
        synchronized (dataBuffer) {
            dataBuffer.add(data);

            if (dataBuffer.size() >= bufferSize) {
                flush();
            }
        }
    }

    public void flush() {
        List<SensorData> dataToWrite;

        synchronized (dataBuffer) {
            if (dataBuffer.isEmpty()) {
                return;
            }

            // Создаем копию данных для записи и сортируем по времени
            dataToWrite = new ArrayList<>(dataBuffer);
            dataToWrite.sort(Comparator.comparing(SensorData::getMeasurementTime));

            // Очищаем буфер
            dataBuffer.clear();
        }

        try {
            writer.writeBufferedData(dataToWrite);
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
