package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserNotFoundException;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;


    @PostMapping
    public ItemDto addNewItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(SHARER_USER_ID) int userId) throws UserNotFoundException {
        return itemService.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable String itemId, @RequestHeader(SHARER_USER_ID) int userId) {
        return itemService.updateItem(itemDto, Integer.parseInt(itemId), userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable String itemId, @RequestHeader(SHARER_USER_ID) int userId) {
        return itemService.getItemById(Integer.parseInt(itemId));
    }

    @GetMapping
    public Collection<ItemDto> getItemsOfUser(@RequestHeader(SHARER_USER_ID) int userId) {
        return itemService.getItemsOfUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(value = "text") String text, @RequestHeader(SHARER_USER_ID) int userId) {
        return itemService.search(text);
    }
}