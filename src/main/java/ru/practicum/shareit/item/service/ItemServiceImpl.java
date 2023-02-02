package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemDao itemDao;
    private final ItemMapper itemMapper;


    @Override
    public ItemDto addNewItem(ItemDto itemDto, int userId) throws UserNotFoundException {
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        Item addedItem = itemDao.addNewItem(item);
        return itemMapper.toItemDto(addedItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int id, int userId) throws ItemNotFoundException {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        Item updatedItem = itemDao.updateItem(item, id, userId);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(int id) {
        Item foundItem = itemDao.getItemById(id);
        return itemMapper.toItemDto(foundItem);
    }


    @Override
    public Collection<ItemDto> getItemsOfUser(int id) {
        Collection<Item> items = itemDao.getItemsOfUser(id);
        List<ItemDto> foundItems = new ArrayList<>();
        for (Item item : items) {
            foundItems.add(itemMapper.toItemDto(item));
        }
        return foundItems;
    }

    @Override
    public Collection<ItemDto> search(String text) {
        Collection<Item> items = itemDao.search(text);
        List<ItemDto> foundItems = new ArrayList<>();
        for (Item item : items) {
            foundItems.add(itemMapper.toItemDto(item));
        }
        return foundItems;
    }
}
