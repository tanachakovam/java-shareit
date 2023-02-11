package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.WrongBookingRequestException;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
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
    public ItemDto getItemById(int id) {
        Item foundItem = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item " + id + " is not found."));
        return itemMapper.toItemDto(foundItem);
    }


    @Override
    public Collection<ItemDto> getItemsOfUser(int id) {
        Collection<Item> items = itemRepository.findAllByOwner(id);
        List<ItemDto> foundItems = new ArrayList<>();
        for (Item item : items) {
            foundItems.add(itemMapper.toItemDto(item));
        }
        return foundItems;
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Collection<Item> items = itemRepository.search(text);
        List<ItemDto> foundItems = new ArrayList<>();
        for (Item item : items) {
            foundItems.add(itemMapper.toItemDto(item));
        }
        return foundItems;
    }

    @Override
    public CommentDto addNewComment(CommentDto commentDto, int itemId, int userId) {
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        if (commentRepository.checkIfUserBookedItem(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED) == null) {
            throw new WrongBookingRequestException("Comment can't be written if user didn't booked the item.");
        }

        if (getItemById(itemId) == null) {
            throw new ItemNotFoundException("Item with this ID is not found.");
        }
        Comment comment = commentMapper.toComment(commentDto);
        comment.setAuthorName(userService.getUserById(userId).getName());
        comment.setItemId(itemId);
        Comment addedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(addedComment);

    }
}
