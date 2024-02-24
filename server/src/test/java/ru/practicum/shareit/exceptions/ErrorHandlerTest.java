package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void testNotFoundException() {
        String message = "Сущность не найдена";
        NotFoundException exception = new NotFoundException(message);
        ErrorResponse response = errorHandler.handleNotFoundException(exception);
        assertEquals(response.getError(), message);
    }

    @Test
    void testBadRequestException() {
        String message = "Некорректный запрос";
        BadRequestException exception = new BadRequestException(message);
        ErrorResponse response = errorHandler.handleBadRequestException(exception);
        assertEquals(response.getError(), message);
    }

}