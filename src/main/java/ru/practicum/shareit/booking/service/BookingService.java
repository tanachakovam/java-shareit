package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto addNewBooking(BookingDto bookingDto, int userId);

    BookingDto approveBooking(int bookingId, boolean approved, int userId);

    BookingDto getBooking(int bookingId, int userId);

    Collection<BookingDto> getBookingsOfUser(String state, int userId);

    Collection<BookingDto> getBookingsOfOwner(String state, int userId);
}
