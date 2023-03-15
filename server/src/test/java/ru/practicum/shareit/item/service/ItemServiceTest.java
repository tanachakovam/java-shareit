package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.WrongBookingRequestException;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingMapperImpl bookingMapper;
    @Mock
    private CommentMapperImpl commentMapper;
    @Mock
    private ItemMapperImpl itemMapper;

    @Mock
    private UserService userService;


    private Booking booking;
    private Comment comment;

    private User user;
    private Item item;

    @BeforeEach
    void beforeEach() {
        itemMapper = new ItemMapperImpl();
        commentMapper = new CommentMapperImpl();
        bookingMapper = new BookingMapperImpl();
        itemService = new ItemServiceImpl(userService, itemMapper, bookingMapper, itemRepository, bookingRepository,
                commentRepository, commentMapper);

        user = new User(1, "user", "user@email");
        item = new Item(1, "item", "description", true, user.getId(), null);
        booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5), item, user, BookingState.WAITING);
        comment = new Comment(1, "text", item, user, LocalDateTime.now());
    }

    @Test
    void addNewItem_whenInvoked_thenSavedItem() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto itemDto = itemMapper.toItemDto(item);

        ItemDto actualItem = itemService.addNewItem(itemDto, user.getId());
        assertNotNull(actualItem);
        assertEquals(itemDto, actualItem);
    }

    @Test
    void addNewItem_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        ItemDto itemDto = itemMapper.toItemDto(item);

        assertThatThrownBy(
                () -> itemService.addNewItem(itemDto, anyInt())
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("User with this ID doesn't exist.");
    }

    @Test
    void updateItem_whenNotOwner_thenUserNotFoundExceptionThrown() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        ItemDto itemDto = itemMapper.toItemDto(item);

        assertThatThrownBy(
                () -> itemService.updateItem(itemDto, item.getId(), anyInt())
        ).isInstanceOf(ItemNotFoundException.class)
                .message().isEqualTo("Only owner can update the information about item.");
    }

    @Test
    void updateItem_whenUpdateName_thenUpdatedItem() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        item.setName("Updated name");
        item.setAvailable(null);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto itemDto = itemMapper.toItemDto(item);

        ItemDto actualItemDto = itemService.updateItem(itemDto, item.getId(), item.getOwner());
        assertNotNull(actualItemDto);
        assertEquals(itemDto.getName(), actualItemDto.getName());
        assertEquals(itemDto.getDescription(), actualItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), actualItemDto.getAvailable());
    }

    @Test
    void updateItem_whenUpdateDescription_thenUpdatedItem() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        item.setName(null);
        item.setDescription("Updated descr");

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto itemDto = itemMapper.toItemDto(item);

        ItemDto actualItemDto = itemService.updateItem(itemDto, item.getId(), item.getOwner());
        assertNotNull(actualItemDto);
        assertEquals(itemDto.getName(), actualItemDto.getName());
        assertEquals(itemDto.getDescription(), actualItemDto.getDescription());
    }

    @Test
    void updateItem_whenUpdateAvailable_thenUpdatedItem() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        item.setDescription(null);
        item.setName(null);
        item.setAvailable(false);

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto itemDto = itemMapper.toItemDto(item);

        ItemDto actualItemDto = itemService.updateItem(itemDto, item.getId(), item.getOwner());
        assertNotNull(actualItemDto);
        assertEquals(itemDto.getName(), actualItemDto.getName());
        assertEquals(itemDto.getDescription(), actualItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), actualItemDto.getAvailable());

    }

    @Test
    void getItemById_whenInvoked_thenResponseStatusOkWithItem() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        ItemDtoFull itemDtos = itemMapper.toItemDtoFull(item, null, null, Collections.emptyList());

        ItemDtoFull actualItemDtos = itemService.getItemById(item.getId(), anyInt());

        assertNotNull(actualItemDtos);
        assertEquals(itemDtos, actualItemDtos);
        assertNull(actualItemDtos.getNextBooking());
        assertNull(actualItemDtos.getNextBooking());
    }

    @Test
    void getItemById_whenInvokedWithNextBooking_thenResponseStatusOkWithItem() {
        final List<Booking> bookings = List.of(booking);

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByOwner(user.getId()))
                .thenReturn(bookings);
        when(bookingRepository.findNextBooking(anyInt(), any(LocalDateTime.class), anyInt()))
                .thenReturn(bookings);
        BookingDto.BookingDtoForOwner nextBooking = bookingMapper.toBookingDtoForOwner(bookings.stream().findFirst().orElse(null));

        ItemDtoFull itemDtos = itemMapper.toItemDtoFull(item, null, nextBooking, Collections.emptyList());

        ItemDtoFull actualItemDtos = itemService.getItemById(item.getId(), user.getId());

        assertNotNull(actualItemDtos);
        assertEquals(itemDtos, actualItemDtos);
    }

    @Test
    void getItemById_whenInvokedWithLastBooking_thenResponseStatusOkWithItem() {
        final List<Booking> bookings = List.of(booking);

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByOwner(user.getId()))
                .thenReturn(bookings);
        when(bookingRepository.findLastBooking(anyInt(), any(LocalDateTime.class), anyInt()))
                .thenReturn(bookings);
        BookingDto.BookingDtoForOwner lastBooking = bookingMapper.toBookingDtoForOwner(bookings.stream().findFirst().orElse(null));

        ItemDtoFull itemDtos = itemMapper.toItemDtoFull(item, lastBooking, null, Collections.emptyList());

        ItemDtoFull actualItemDtos = itemService.getItemById(item.getId(), user.getId());

        assertNotNull(actualItemDtos);
        assertEquals(itemDtos, actualItemDtos);
    }


    @Test
    void getItemsOfUser_whenInvoked_thenResponseStatusOkWithItemCollection() {
        final List<Item> items = List.of(item);


        when(itemRepository.findAllByOwnerOrderByIdAsc(anyInt(), any()))
                .thenReturn(items);
        when(bookingRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of(booking));

        ItemDtoFull itemDtos = itemMapper.toItemDtoFull(item, null, null, Collections.emptyList());

        Collection<ItemDtoFull> actualItemDtos = itemService.getItemsOfUser(anyInt(), any());

        assertNotNull(actualItemDtos);
        assertEquals(List.of(itemDtos), actualItemDtos);
    }

    @Test
    void findItemById_whenItemNotFound_thenUserNotFoundExceptionThrown() {
        assertThatThrownBy(
                () -> itemService.findItemById(anyInt())
        ).isInstanceOf(ItemNotFoundException.class)
                .message().isEqualTo("Item is not found.");
    }

    @Test
    void findItemsByUser_whenInvoked_thenResponseStatusOkWithItemCollection() {
        final List<Item> items = List.of(item);

        when(itemRepository.findAllByOwner(anyInt(), any()))
                .thenReturn(items);

        Collection<Item> actualItems = itemService.findItemsByUser(1);

        assertNotNull(actualItems);
        assertEquals(items, actualItems);
    }

    @Test
    void getItemForRequests_whenInvoked_thenResponseStatusOkWithItemCollection() {
        final List<Item> items = List.of(item);

        when(itemRepository.findAllByRequestId(anyInt()))
                .thenReturn(items);
        List<ItemDto> itemDtos = itemMapper.toItemDtoCollection(items);


        Collection<ItemDto> actualItems = itemService.getItemForRequests(anyInt());

        assertNotNull(actualItems);
        assertEquals(itemDtos, actualItems);
    }

    @Test
    void search_whenInvoked_thenResponseStatusOkWithItemCollection() {
        final List<Item> items = List.of(item);

        when(itemRepository.search(anyString(), any()))
                .thenReturn(items);
        List<ItemDto> itemDtos = itemMapper.toItemDtoCollection(items);


        Collection<ItemDto> actualItems = itemService.search("item", PageRequest.of(0, 10));

        assertNotNull(actualItems);
        assertEquals(itemDtos, actualItems);
    }

    @Test
    void search_whenInvokedWithBlankText_thenResponseStatusOkWithItemCollection() {
        final List<Item> items = Collections.emptyList();
        List<ItemDto> itemDtos = itemMapper.toItemDtoCollection(items);

        Collection<ItemDto> actualItems = itemService.search("", PageRequest.of(0, 10));

        assertEquals(itemDtos.isEmpty(), actualItems.isEmpty());
    }

    @Test
    void addNewComment_whenInvoked_thenResponseStatusOkWithItemCollection() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyInt(), any()))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        CommentDto actualComment = itemService.addNewComment(commentDto, item.getId(), user.getId());
        assertNotNull(actualComment);
        assertEquals(commentDto, actualComment);
    }

    @Test
    void addNewComment_whenNotValid_thenWrongBookingRequestExceptionThrown() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);

        assertThatThrownBy(
                () -> itemService.addNewComment(any(), item.getId(), user.getId())
        ).isInstanceOf(WrongBookingRequestException.class)
                .message().isEqualTo("Comment can't be written if user didn't booked the item.");
    }

    @Test
    void addNewComment_whenUserNotValid_thenUserNotFoundExceptionThrown() {

        assertThatThrownBy(
                () -> itemService.addNewComment(any(), item.getId(), user.getId())
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("User with this ID doesn't exist.");
    }
}