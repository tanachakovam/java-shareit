package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.WrongBookingRequestException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
    @Transactional
    public ItemDto addNewItem(ItemDto itemDto, int userId) throws UserNotFoundException {
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        Item addedItem = itemRepository.save(item);
        return itemMapper.toItemDto(addedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, int id, int userId) throws ItemNotFoundException {
        Item item = itemMapper.toItem(itemDto);
        Item itemToUpdate = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item " + id + " is not found."));
        if (!itemToUpdate.getOwner().equals(userId)) {
            throw new ItemNotFoundException("Only owner can update the information about item.");
        }
        item.setOwner(userId);

        if (item.getName() == null) {
            itemToUpdate.setName(itemToUpdate.getName());
        } else {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() == null) {
            itemToUpdate.setDescription(itemToUpdate.getDescription());
        } else {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() == null) {
            itemToUpdate.setAvailable(itemToUpdate.getAvailable());
        } else {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        itemRepository.save(itemToUpdate);
        return itemMapper.toItemDto(itemToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoFull getItemById(int id, int userId) {
        BookingDto.BookingDtoForOwner nextBooking = null;
        BookingDto.BookingDtoForOwner lastBooking = null;
        List<CommentDto> comments = null;
        Item foundItem = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item " + id + " is not found."));
        if (bookingRepository.findAllByItemId(foundItem.getId()) != null) {
            nextBooking = getNextBooking(foundItem, userId);
            lastBooking = getLastBooking(foundItem, userId);
            comments = getComments(foundItem);
        }
        return itemMapper.toItemDtoFull(foundItem, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Item findItemById(int id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item " + id + " is not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Item> findItemsByUser(int id) {
        return itemRepository.findAllByOwner(id);
    }


    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDtoFull> getItemsOfUser(int id) {
        Collection<Item> items = itemRepository.findAllByOwner(id);
        List<ItemDtoFull> foundItems = new ArrayList<>();
        BookingDto.BookingDtoForOwner nextBooking = null;
        BookingDto.BookingDtoForOwner lastBooking = null;
        List<CommentDto> comments = null;
        for (Item item : items) {
            if (bookingRepository.findAllByItemId(item.getId()) != null) {
                nextBooking = getNextBooking(item, id);
                lastBooking = getLastBooking(item, id);
                comments = getComments(item);
            }
            foundItems.add(itemMapper.toItemDtoFull(item, lastBooking, nextBooking, comments));
        }
        return foundItems;
    }

    private BookingDto.BookingDtoForOwner getLastBooking(Item item, int userId) {
        Booking lastBooking = null;
        if (item.getOwner() == userId && !bookingRepository.findAllByOwner(userId).isEmpty()) {
            lastBooking = bookingRepository.findLastBooking(item.getId(), LocalDateTime.now(), userId).stream().findFirst().orElse(null);
        }
        return bookingMapper.toBookingDtoForOwner(lastBooking);
    }

    private BookingDto.BookingDtoForOwner getNextBooking(Item item, int userId) {
        Booking nextBooking = null;
        if (item.getOwner() == userId && !bookingRepository.findAllByOwner(userId).isEmpty()) {
            nextBooking = bookingRepository.findNextBooking(item.getId(), LocalDateTime.now(), userId).stream().findFirst().orElse(null);
        }
        return bookingMapper.toBookingDtoForOwner(nextBooking);
    }

    private List<CommentDto> getComments(Item item) {
        List<Comment> comments = commentRepository.findCommentsByItemId(item.getId());
        return commentMapper.toCommentDtoCollection(comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Collection<Item> items = itemRepository.search(text);
        return itemMapper.toItemDtoCollection(items);
    }

    @Override
    @Transactional
    public CommentDto addNewComment(CommentDto commentDto, int itemId, int userId) {
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        if (bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).isEmpty()) {
            throw new WrongBookingRequestException("Comment can't be written if user didn't booked the item.");
        }
        if (findItemById(itemId) == null) {
            throw new ItemNotFoundException("Item with this ID is not found.");
        }
        Comment comment = commentMapper.toComment(commentDto);
        comment.setAuthor(userService.findUserById(userId));
        comment.setItem(findItemById(itemId));
        comment.setCreated(LocalDateTime.now());
        Comment addedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(addedComment);
    }
}
