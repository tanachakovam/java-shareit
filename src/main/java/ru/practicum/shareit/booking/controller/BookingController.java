package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";


    @PostMapping
    public BookingDto addNewBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(SHARER_USER_ID) int userId) {
        return bookingService.addNewBooking(bookingDto, userId);
    }


    //@PatchMapping("/{bookingId}?approved={approved}")
    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable String bookingId, @RequestParam(value = "approved") Boolean approved, @RequestHeader(SHARER_USER_ID) int userId) {
        return bookingService.approveBooking(Integer.parseInt(bookingId), approved, userId);
    }

    //GET /bookings/{bookingId}
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable String bookingId, @RequestHeader(SHARER_USER_ID) int userId) {
        return bookingService.getBooking(Integer.parseInt(bookingId), userId);
    }

    //GET /bookings?state={state}
    @GetMapping
    public Collection<BookingDto> getBookingsOfUser(@RequestParam(value = "state", defaultValue = "ALL", required = false) String state, @RequestHeader(SHARER_USER_ID) int userId) {
        return bookingService.getBookingsOfUser(state, userId);
    }

    // GET /bookings/owner?state={state}
    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsOfOwner(@RequestParam(value = "state", defaultValue = "ALL", required = false) String state, @RequestHeader(SHARER_USER_ID) int userId) {
        return bookingService.getBookingsOfOwner(state, userId);
    }
}
