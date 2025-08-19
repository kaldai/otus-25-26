package ru.otus.processor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.model.Message;

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

    @Test
    void testEvenSecondExceptionProcessor_OddSecond_NoException() {
        // Arrange
        var fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0, 1); // Нечетная секунда
        var processor = new EvenSecondExceptionProcessor(() -> fixedTime);
        var message = Message.builder(1L).build();

        // Act & Assert
        assertDoesNotThrow(() -> processor.process(message));
    }

    @Test
    void testEvenSecondExceptionProcessor_EvenSecond_ThrowsException() {
        // Arrange
        var fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0, 2); // Четная секунда
        var processor = new EvenSecondExceptionProcessor(() -> fixedTime);
        var message = Message.builder(1L).build();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> processor.process(message));
    }
}
