package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getRequestsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requests of userId={}", userId);
        return requestClient.getRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsOfOthers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get requests of userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAllRequestsOfOthers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable Long requestId) {
        log.info("Get request requestId={}, userId={}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> addNewItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating request {}, userId={}", itemRequestDto, userId);
        return requestClient.addNewItemRequest(userId, itemRequestDto);
    }
}
