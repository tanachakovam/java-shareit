package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.WrongBookingRequestException;
import ru.practicum.shareit.booking.exception.WrongEndTimeException;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapperImpl bookingMapper;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    private Booking booking;

    private User user;
    private Item item;

    @BeforeEach
    void beforeEach() {
        bookingMapper = new BookingMapperImpl();
        bookingService = new BookingServiceImpl(bookingMapper, userService, bookingRepository, itemService);
        user = new User(1, "user", "user@email");
        item = new Item(1, "item", "description", true, user.getId(), null);
        booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5), item, user, BookingStatus.WAITING);
    }

    @Test
    void addNewBooking_whenBookingValid_thenSaveBooking() {

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        BookingDto actualBookingDto = bookingService.addNewBooking(bookingDto, user.getId());
        assertNotNull(actualBookingDto);
        assertEquals(bookingDto, actualBookingDto);
    }

    @Test
    void addNewBooking_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertThatThrownBy(
                () -> bookingService.addNewBooking(bookingDto, user.getId())
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("User with this ID doesn't exist.");
    }

    @Test
    void addNewBooking_whenItemNotFound_thenItemNotFoundExceptionThrown() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertThatThrownBy(
                () -> bookingService.addNewBooking(bookingDto, user.getId())
        ).isInstanceOf(ItemNotFoundException.class)
                .message().isEqualTo("Item with this ID doesn't exist.");
    }

    @Test
    void addNewBooking_whenEndNotValid_thenWrongEndTimeExceptionThrown() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);

        booking.setEnd(LocalDateTime.now().minusDays(5));

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertThatThrownBy(
                () -> bookingService.addNewBooking(bookingDto, user.getId())
        ).isInstanceOf(WrongEndTimeException.class)
                .message().isEqualTo("Incorrect end time.");
    }

    @Test
    void addNewBooking_whenStartNotValid_thenWrongEndTimeExceptionThrown() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);

        booking.setStart(LocalDateTime.now().minusDays(5));

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertThatThrownBy(
                () -> bookingService.addNewBooking(bookingDto, user.getId())
        ).isInstanceOf(WrongEndTimeException.class)
                .message().isEqualTo("Incorrect start time.");
    }

    @Test
    void addNewBooking_whenStartAndEndNotValid_thenWrongEndTimeExceptionThrown() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);

        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStart(LocalDateTime.now().plusDays(3));

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertThatThrownBy(
                () -> bookingService.addNewBooking(bookingDto, user.getId())
        ).isInstanceOf(WrongEndTimeException.class)
                .message().isEqualTo("Incorrect end/start time.");
    }

    @Test
    void addNewBooking_whenAvailableNotValid_thenWrongBookingRequestExceptionThrown() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);

        booking.getItem().setAvailable(false);
        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertThatThrownBy(
                () -> bookingService.addNewBooking(bookingDto, user.getId())
        ).isInstanceOf(WrongBookingRequestException.class)
                .message().isEqualTo("Item with this ID is unavailable.");
    }

    @Test
    void addNewBooking_whenBookerNotValid_thenWrongBookingRequestExceptionThrown() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);
        when(itemService.findItemsByUser(user.getId()))
                .thenReturn(List.of(item));

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertThatThrownBy(
                () -> bookingService.addNewBooking(bookingDto, user.getId())
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("Owner can't book his items.");
    }

    @Test
    void approveBooking_whenNotOwner_thenUserNotFoundExceptionThrown() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);

        assertThatThrownBy(
                () -> bookingService.approveBooking(booking.getId(), true, anyInt())
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("Only owner can approve booking.");
    }

    @Test
    void approveBooking_whenIsValid_thenApprovedBooking() {
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        booking.setItem(item);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto actualBookingDto = bookingService.approveBooking(booking.getId(), true, item.getOwner());
        assertNotNull(actualBookingDto);
        assertEquals(actualBookingDto.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_whenIsValid_thenRejectedBooking() {
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        booking.setItem(item);
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto actualBookingDto = bookingService.approveBooking(booking.getId(), false, item.getOwner());
        assertNotNull(actualBookingDto);
        assertEquals(actualBookingDto.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void approveBooking_whenBookingNotFound_thenBookingNotFoundExceptionThrown() {
        assertThatThrownBy(
                () -> bookingService.approveBooking(booking.getId(), true, anyInt())
        ).isInstanceOf(BookingNotFoundException.class)
                .message().isEqualTo("Booking  is not found");
    }

    @Test
    void approveBooking_whenApproved_thenWrongBookingRequestExceptionThrown() {
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        assertThatThrownBy(
                () -> bookingService.approveBooking(booking.getId(), true, item.getOwner())
        ).isInstanceOf(WrongBookingRequestException.class)
                .message().isEqualTo("Booking is already approved.");
    }

    @Test
    void getBooking_whenNotValid_thenUserNotFoundExceptionThrown() {

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        assertThatThrownBy(
                () -> bookingService.getBooking(booking.getId(), user.getId())
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("Only owner or booker can get information about booking.");
    }

    @Test
    void getBooking_whenUserNotValid_thenUserNotFoundExceptionThrown() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        assertThatThrownBy(
                () -> bookingService.getBooking(booking.getId(), user.getId())
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("User with this ID doesn't exist.");
    }

    @Test
    void getBooking_whenStatusWaiting_thenResponseStatusOkWithBooking() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findAllByBookerId(user.getId()))
                .thenReturn(List.of(booking));

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        BookingDto actualBookingDto = bookingService.getBooking(booking.getId(), user.getId());

        assertNotNull(actualBookingDto);
        assertEquals(bookingDto, actualBookingDto);
    }

    @Test
    void getBookingsOfUser_whenStatusWaiting_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfUser("WAITING", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfUser_whenStatusCurrent_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(anyInt(), any(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfUser("CURRENT", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfUser_whenStatusNotValid_thenWrongBookingRequestExceptionThrown() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        assertThatThrownBy(
                () -> bookingService.getBookingsOfUser("ddd", user.getId(), PageRequest.of(0, 10))
        ).isInstanceOf(WrongBookingRequestException.class)
                .message().isEqualTo("Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void getBookingsOfUser_whenStatusPast_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfUser("PAST", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfUser_whenStatusFuture_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfUser("FUTURE", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfUser_whenStatusRejected_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfUser("REJECTED", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfUser_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        assertThatThrownBy(
                () -> bookingService.getBookingsOfUser("WAITING", anyInt(), PageRequest.of(0, 10))
        ).isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with this ID doesn't exist.");
    }

    @Test
    void getBookingsOfUser_whenInvoked_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyInt(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfUser("ALL", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfOwner_whenInvoked_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwner(anyInt(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfOwner("ALL", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfOwner_whenStatusRejected_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerAndStatusAndOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfOwner("REJECTED", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfOwner_whenStatusNotValid_thenWrongBookingRequestExceptionThrown() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        assertThatThrownBy(
                () -> bookingService.getBookingsOfOwner("ddd", user.getId(), PageRequest.of(0, 10))
        ).isInstanceOf(WrongBookingRequestException.class)
                .message().isEqualTo("Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void getBookingsOfOwner_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        assertThatThrownBy(
                () -> bookingService.getBookingsOfOwner("WAITING", anyInt(), PageRequest.of(0, 10))
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("User with this ID doesn't exist.");
    }

    @Test
    void getBookingsOfOwner_whenStatusWaiting_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerAndStatusAndOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfOwner("WAITING", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfOwner_whenStatusFuture_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerAndStartAfterOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfOwner("FUTURE", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfOwner_whenStatusPast_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerAndEndBeforeOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfOwner("PAST", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getBookingsOfOwner_whenStatusCurrent_thenResponseStatusOkWithBookingCollection() {
        final List<Booking> bookings = List.of(booking);

        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByOwnerAndEndAfterAndStartBeforeOrderByStartDesc(anyInt(), any(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingDtos = bookingMapper.toBookingDtoCollection(bookings);

        List<BookingDto> actualBookingDtos = bookingService.getBookingsOfOwner("CURRENT", user.getId(), PageRequest.of(0, 10));

        assertNotNull(actualBookingDtos);
        assertEquals(bookingDtos, actualBookingDtos);
    }
}