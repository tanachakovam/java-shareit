package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(BookingDto bookingDto, int userId);

    BookingDto approveBooking(int bookingId, boolean approved, int userId);

    BookingDto getBooking(int bookingId, int userId);

    List<BookingDto> getBookingsOfUser(String state, int userId, Pageable pageable);

    Booking getBookingById(int bookingId);

    List<BookingDto> getBookingsOfOwner(String state, int userId, Pageable pageable);
}
