package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addNewItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader(SHARER_USER_ID) int userId) {
        return itemRequestService.addNewItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public Collection<ItemRequestDtoWithItems> getRequestsOfUser(@RequestHeader(SHARER_USER_ID) int userId) {
        return itemRequestService.getAllRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDtoWithItems> getRequestsOfOthers(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "20", required = false) Integer size,
            @RequestHeader(SHARER_USER_ID) int userId) {
        return itemRequestService.getAllRequestsOfOthers(PageRequest.of(from / size, size), userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getRequestById(@PathVariable String requestId,
                                                  @RequestHeader(SHARER_USER_ID) int userId) {
        return itemRequestService.getRequestById(Integer.parseInt(requestId), userId);
    }
}