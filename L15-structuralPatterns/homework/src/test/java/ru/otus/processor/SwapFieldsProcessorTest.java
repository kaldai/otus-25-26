package ru.otus.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.model.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SwapFieldsProcessorTest {

    @Test
    void testSwapFieldsProcessor() {
        // Arrange
        var processor = new SwapFieldsProcessor();
        var originalMessage =
                Message.builder(1L).field11("value11").field12("value12").build();

        // Act
        var result = processor.process(originalMessage);

        // Assert
        assertEquals("value12", result.getField11());
        assertEquals("value11", result.getField12());
    }
}