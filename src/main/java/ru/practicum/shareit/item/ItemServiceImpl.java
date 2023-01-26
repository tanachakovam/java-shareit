package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;

import java.util.*;

@Service
@Component
public class ItemServiceImpl implements ItemService {

    private final UserService userService;

    private Map<Integer, Item> items = new HashMap<>();
    private int id;

    @Autowired
    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Item addNewItem(Item item, int userId) throws UserNotFoundException {
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        item.setId(++id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item, int id, int userId) throws ItemNotFoundException {
        if (!items.containsKey(id)) {
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

    @Override
    public Item getItemById(int id) {
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException("Item with this ID doesn't exist.");
        }
        return items.get(id);
    }


    @Override
    public Collection<Item> getItemsOfUser(int id) {
        List<Item> itemsOfUser = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == id) {
                itemsOfUser.add(item);
            }
        }
        return itemsOfUser;
    }

    @Override
    public Collection<Item> search(String text) {
        List<Item> itemsFromSearch = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable()) {
                itemsFromSearch.add(item);
            }
        }
        return itemsFromSearch;
    }


}
