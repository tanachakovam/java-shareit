package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserNotFoundException;

import java.util.Collection;


public interface ItemService {
    Item addNewItem(Item item, int userId) throws UserNotFoundException;

    Item updateItem(Item item, int id, int userId);

    Item getItemById(int id);

    Collection<Item> getItemsOfUser(int id);

    Collection<Item> search(String text);
}
