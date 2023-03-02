package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.Collection;
import java.util.List;


public interface ItemService {
    ItemDto addNewItem(ItemDto itemDto, int userId) throws UserNotFoundException;

    ItemDto updateItem(ItemDto itemDto, int id, int userId);

    ItemDtoFull getItemById(int id, int userId);

    Collection<ItemDtoFull> getItemsOfUser(int id, Pageable pageable);

    Collection<ItemDto> search(String text, Pageable pageable);

    CommentDto addNewComment(CommentDto commentDto, int itemId, int userId);

    Item findItemById(int id);

    Collection<Item> findItemsByUser(int id);

    List<ItemDto> getItemForRequests(int requestId);
}
