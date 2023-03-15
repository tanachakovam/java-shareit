package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto addNewItemRequest(ItemRequestDto itemRequestDto, int userId);

    ItemRequestDtoWithItems getRequestById(int requestId, int userId);


    Collection<ItemRequestDtoWithItems> getAllRequestsOfUser(int userId);

    Collection<ItemRequestDtoWithItems> getAllRequestsOfOthers(Pageable pageable, int userId);
}
