package ru.practicum.shareit.booking.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


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
    public List<BookingDto> getBookingsOfUser(@RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
                                              @RequestHeader(SHARER_USER_ID) int userId,
                                              @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                              @Positive @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
        return bookingService.getBookingsOfUser(state, userId, PageRequest.of(from / size, size));
    }

    // GET /bookings/owner?state={state}
    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfOwner(@RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
                                               @RequestHeader(SHARER_USER_ID) int userId,
                                               @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                               @Positive @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
        return bookingService.getBookingsOfOwner(state, userId, PageRequest.of(from / size, size));
    }
}
