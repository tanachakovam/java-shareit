package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.WrongBookingRequestException;
import ru.practicum.shareit.booking.exception.WrongEndTimeException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemService itemService;

    @Override
    public BookingDto addNewBooking(BookingDto bookingDto, int userId) {
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        Booking booking = bookingMapper.toBooking(bookingDto);

        if (itemService.getItemById(booking.getItemId()) == null) {
            throw new ItemNotFoundException("Item with this ID doesn't exist.");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new WrongEndTimeException("Incorrect end time.");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new WrongEndTimeException("Incorrect end/start time.");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new WrongEndTimeException("Incorrect start time.");
        }
        if (!itemService.getItemById(booking.getItemId()).getAvailable()) {
            throw new WrongBookingRequestException("Item with this ID is unavailable.");
        }
        if (itemService.getItemsOfUser(userId).contains(itemService.getItemById(booking.getItemId()))) {
            throw new WrongBookingRequestException("Owner can't book his items.");
        }
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookerId(userId);
        booking.setItemName(itemService.getItemById(booking.getItemId()).getName());
        Booking addedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(addedBooking);
    }

    @Override
    public BookingDto approveBooking(int bookingId, boolean approved, int userId) {
        Booking approvingBooking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking  is not found"));
       if (!itemService.getItemsOfUser(userId).contains(itemService.getItemById(approvingBooking.getItemId()))) {
            throw new WrongBookingRequestException("Only owner can approve booking.");
        }
        if (approvingBooking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new WrongBookingRequestException("Booking is already approved.");
        }
        if (approved) {
            approvingBooking.setStatus(BookingStatus.APPROVED);
        } else {
            approvingBooking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(approvingBooking);
        return bookingMapper.toBookingDto(approvingBooking);
    }

    @Override
    public BookingDto getBooking(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking  is not found"));
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        if (!bookingRepository.findAllByBookerId(userId).contains(booking) && !bookingRepository.findAllByOwner(userId).contains(booking)) {
            throw new WrongBookingRequestException("Only owner or booker can get information about booking.");
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getBookingsOfUser(String state, int userId) {
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        Collection<Booking> bookings;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeAndStartAfterOrderByIdDesc(userId, end, start);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByIdDesc(userId, end);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByIdDesc(userId, start);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusEqualsOrderByIdDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusEqualsOrderByIdDesc(userId, BookingStatus.REJECTED);
                break;
            case "ALL":
                bookings = bookingRepository.findAllByBookerId(userId);
                break;
            default:
                throw new WrongBookingRequestException("Wrong status of the booking");
        }
        List<BookingDto> foundBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            foundBookings.add(bookingMapper.toBookingDto(booking));
        }
        return foundBookings;
    }

    @Override
    public Collection<BookingDto> getBookingsOfOwner(String state, int userId) {
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        Collection<Booking> bookings;
        LocalDateTime start = LocalDateTime.now(), end = LocalDateTime.now();
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findAllByOwnerAndEndBeforeAndStartAfterOrderByIdDesc(userId, end, start);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByOwnerAndEndBeforeOrderByIdDesc(userId, end);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByOwnerAndStartAfterOrderByIdDesc(userId, start);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByOwnerAndStatusAndOrderByIdDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByOwnerAndStatusAndOrderByIdDesc(userId, BookingStatus.REJECTED);
                break;
            case "ALL":
                bookings = bookingRepository.findAllByOwner(userId);
                break;
            default:
                throw new WrongBookingRequestException("Wrong status of the booking");
        }
        List<BookingDto> foundBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            foundBookings.add(bookingMapper.toBookingDto(booking));
        }
        return foundBookings;
    }
}

