package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.service.ItemService;

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
    public ItemDto addNewItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(SHARER_USER_ID) int userId) {
        return itemService.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable String itemId, @RequestHeader(SHARER_USER_ID) int userId) {
        return itemService.updateItem(itemDto, Integer.parseInt(itemId), userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoFull getItemById(@PathVariable String itemId, @RequestHeader(SHARER_USER_ID) int userId) {
        return itemService.getItemById(Integer.parseInt(itemId), userId);
    }

    @GetMapping
    public Collection<ItemDtoFull> getItemsOfUser(@RequestHeader(SHARER_USER_ID) int userId) {
        return itemService.getItemsOfUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(value = "text") String text, @RequestHeader(SHARER_USER_ID) int userId) {
        return itemService.search(text);
    }

    // POST /items/{itemId}/comment
    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@Valid @RequestBody CommentDto commentDto, @PathVariable String itemId, @RequestHeader(SHARER_USER_ID) int userId) {
        return itemService.addNewComment(commentDto, Integer.parseInt(itemId), userId);
    }
}