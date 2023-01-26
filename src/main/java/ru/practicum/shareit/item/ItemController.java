package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserNotFoundException;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemServiceImpl itemService;

    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNewItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(SHARER_USER_ID) int userId) throws UserNotFoundException {
        Item item = ItemMapper.toItem(itemDto, userId);
        Item addedItem = itemService.addNewItem(item, userId);
        return ItemMapper.toItemDto(addedItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable String itemId, @RequestHeader(SHARER_USER_ID) int userId) {
        Item item = ItemMapper.toItem(itemDto, userId);
        Item updatedItem = itemService.updateItem(item, Integer.parseInt(itemId), userId);
        return ItemMapper.toItemDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable String itemId, @RequestHeader(SHARER_USER_ID) int userId) {
        Item foundItem = itemService.getItemById(Integer.parseInt(itemId));
        return ItemMapper.toItemDto(foundItem);
    }

    @GetMapping
    public Collection<ItemDto> getItemsOfUser(@RequestHeader(SHARER_USER_ID) int userId) {
        Collection<Item> items = itemService.getItemsOfUser(userId);
        List<ItemDto> foundItems = new ArrayList<>();
        for (Item item : items) {
            foundItems.add(ItemMapper.toItemDto(item));
        }
        return foundItems;
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(value = "text") String text, @RequestHeader(SHARER_USER_ID) int userId) {
        Collection<Item> items = itemService.search(text);
        List<ItemDto> foundItems = new ArrayList<>();
        for (Item item : items) {
            foundItems.add(ItemMapper.toItemDto(item));
        }
        return foundItems;
    }
}
