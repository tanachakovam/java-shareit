package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserNotFoundException;

import java.util.Collection;


public interface ItemService {
    ItemDto addNewItem(ItemDto itemDto, int userId) throws UserNotFoundException;

    ItemDto updateItem(ItemDto itemDto, int id, int userId);

    ItemDtoFull getItemById(int id, int userId);

    Collection<ItemDtoFull> getItemsOfUser(int id);

    Collection<ItemDto> search(String text);

    CommentDto addNewComment(CommentDto commentDto, int itemId, int userId);

    Item findItemById(int id);

    Collection<Item> findItemsByUser(int id);
}
