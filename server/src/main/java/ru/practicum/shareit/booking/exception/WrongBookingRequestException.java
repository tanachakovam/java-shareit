package ru.practicum.shareit.booking.exception;

public class WrongBookingRequestException extends RuntimeException {
    public WrongBookingRequestException(String message) {
        super(message);
    }
}
