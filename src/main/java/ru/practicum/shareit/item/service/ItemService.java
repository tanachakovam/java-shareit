package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserNotFoundException;

import java.util.Collection;


public interface ItemService {
    ItemDto addNewItem(ItemDto itemDto, int userId) throws UserNotFoundException;

    ItemDto updateItem(ItemDto itemDto, int id, int userId);

    ItemDto getItemById(int id);

    Collection<ItemDto> getItemsOfUser(int id);

    Collection<ItemDto> search(String text);
}
