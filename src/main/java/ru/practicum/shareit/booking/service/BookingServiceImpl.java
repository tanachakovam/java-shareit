package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.WrongBookingRequestException;
import ru.practicum.shareit.booking.exception.WrongEndTimeException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDto addNewBooking(BookingDto bookingDto, int userId) {
        if (userService.findUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setItem(itemService.findItemById(bookingDto.getItemId()));
        if (itemService.findItemById(bookingDto.getItemId()) == null) {
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
        if (!itemService.findItemById(booking.getItem().getId()).getAvailable()) {
            throw new WrongBookingRequestException("Item with this ID is unavailable.");
        }
        if (itemService.findItemsByUser(userId).contains(itemService.findItemById(bookingDto.getItemId()))) {
            throw new UserNotFoundException("Owner can't book his items.");
        }
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(userService.findUserById(userId));
        booking.setItem(itemService.findItemById(booking.getItem().getId()));
        Booking addedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(addedBooking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(int bookingId, boolean approved, int userId) {
        Booking approvingBooking = getBookingById(bookingId);
        Item item = itemService.findItemById(approvingBooking.getItem().getId());
        if (!item.getOwner().equals(userId)) {
            throw new UserNotFoundException("Only owner can approve booking.");
        }
        if (BookingStatus.APPROVED.equals(approvingBooking.getStatus())) {
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
    @Transactional(readOnly = true)
    public BookingDto getBooking(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking  is not found"));
        if (userService.findUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        if (!bookingRepository.findAllByBookerId(userId).contains(booking) && !bookingRepository.findAllByOwner(userId).contains(booking)) {
            throw new UserNotFoundException("Only owner or booker can get information about booking.");
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(int bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking  is not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsOfUser(String state, int userId, Pageable pageable) {
        if (userService.findUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
                break;
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
                break;
            default:
                throw new WrongBookingRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingMapper.toBookingDtoCollection(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsOfOwner(String state, int userId, Pageable pageable) {
        if (userService.findUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findAllByOwnerAndEndAfterAndStartBeforeOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByOwnerAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByOwnerAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByOwnerAndStatusAndOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByOwnerAndStatusAndOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
                break;
            case "ALL":
                bookings = bookingRepository.findAllByOwner(userId, pageable);
                break;
            default:
                throw new WrongBookingRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingMapper.toBookingDtoCollection(bookings);
    }
}

