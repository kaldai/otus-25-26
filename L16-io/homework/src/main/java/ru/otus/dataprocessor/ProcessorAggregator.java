package ru.otus.dataprocessor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ru.otus.model.Measurement;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        Map<String, Double> result = new LinkedHashMap<>();

        for (Measurement measurement : data) {
            result.merge(measurement.name(), measurement.value(), Double::sum);
        }

        return result;
    }
}
