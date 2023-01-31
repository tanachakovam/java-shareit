package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemDao {
    private Map<Integer, Item> items = new HashMap<>();
    private int id;

    public Item addNewItem(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return item;
    }


    public Item updateItem(Item item, int userId) {
        if (items.get(item.getId()) == null) {
            throw new ItemNotFoundException("Item with this ID doesn't exist.");
        }
        if (!items.get(id).getOwner().equals(userId)) {
            throw new ItemNotFoundException("Only owner can update the information about item.");
        }
        if (item.getName() == null) {
            item.setName(items.get(id).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(id).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(id).getAvailable());
        }
        item.setId(id);
        items.put(id, item);
        return item;
    }

    public Item getItemById(int id) {
        if (items.get(id) == null) throw new ItemNotFoundException("Item with this ID doesn't exist.");
        return items.get(id);
    }

    public Collection<Item> getItemsOfUser(int id) {
        List<Item> itemsOfUser = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == id) {
                itemsOfUser.add(item);
            }
        }
        return itemsOfUser;
    }

    public Collection<Item> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemsFromSearch = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable()) {
                itemsFromSearch.add(item);
            }
        }
        return itemsFromSearch;
    }
}
