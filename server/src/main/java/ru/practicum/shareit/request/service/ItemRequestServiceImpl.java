package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDto addNewItemRequest(ItemRequestDto itemRequestDto, int userId) {
        if (userService.findUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(userService.findUserById(userId));
        ItemRequest addedRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(addedRequest);
    }


    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestDtoWithItems> getAllRequestsOfUser(int userId) {
        if (userService.findUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        List<ItemRequestDtoWithItems> itemRequestsOfUser = new ArrayList<>();
        List<ItemDto> items;
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);
        for (ItemRequest itemRequest : itemRequests) {
            items = itemService.getItemForRequests(itemRequest.getId());
            itemRequestsOfUser.add(itemRequestMapper.toItemRequestDtoWithItems(itemRequest, items));
        }
        return itemRequestsOfUser;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDtoWithItems getRequestById(int requestId, int userId) {
        if (userService.findUserById(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException("Request  is not found"));
        return itemRequestMapper.toItemRequestDtoWithItems(itemRequest, itemService.getItemForRequests(itemRequest.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestDtoWithItems> getAllRequestsOfOthers(Pageable pageable, int userId) {
        List<ItemRequestDtoWithItems> itemRequests = new ArrayList<>();
        List<ItemDto> items;
        List<ItemRequest> itemRequestsOfOthers = itemRequestRepository.findAllByRequestor_IdIsNotOrderByCreatedDesc(userId, pageable);
        for (ItemRequest itemRequest : itemRequestsOfOthers) {
            items = itemService.getItemForRequests(itemRequest.getId());
            itemRequests.add(itemRequestMapper.toItemRequestDtoWithItems(itemRequest, items));
        }
        return itemRequests;
    }
}


