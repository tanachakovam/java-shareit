package ru.practicum.shareit.booking.exception;

public class WrongEndTimeException extends RuntimeException {
    public WrongEndTimeException(String message) {
        super(message);
    }
}
