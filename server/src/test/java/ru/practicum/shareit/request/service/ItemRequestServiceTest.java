package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {


    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRequestMapperImpl requestMapper;

    @Mock
    private UserService userService;


    private ItemRequest request;
    private User user;

    @BeforeEach
    void beforeEach() {
        requestMapper = new ItemRequestMapperImpl();
        requestService = new ItemRequestServiceImpl(userService, requestMapper, requestRepository, itemService);

        user = new User(1, "user", "user@email");
        request = new ItemRequest(1, "description", LocalDateTime.now(), user);
    }

    @Test
    void addNewItemRequest_whenInvoked_thenSavedRequest() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);

        ItemRequestDto requestDto = requestMapper.toItemRequestDto(request);

        ItemRequestDto actualRequest = requestService.addNewItemRequest(requestDto, user.getId());
        assertNotNull(actualRequest);
        assertEquals(requestDto, actualRequest);
    }

    @Test
    void addNewItemRequest_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        ItemRequestDto requestDto = requestMapper.toItemRequestDto(request);

        assertThatThrownBy(
                () -> requestService.addNewItemRequest(requestDto, anyInt())
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("User with this ID doesn't exist.");
    }

    @Test
    void getAllRequestsOfUser_whenInvoked_thenResponseStatusOkWithItemCollection() {
        when(requestRepository.findAllByRequestor_IdOrderByCreatedDesc(anyInt()))
                .thenReturn(List.of(request));
        when(userService.findUserById(anyInt()))
                .thenReturn(user);

        ItemRequestDtoWithItems requestDtoWithItems = requestMapper.toItemRequestDtoWithItems(request, Collections.emptyList());

        Collection<ItemRequestDtoWithItems> actualRequestDtos = requestService.getAllRequestsOfUser(request.getRequestor().getId());

        assertNotNull(actualRequestDtos);
        assertEquals(List.of(requestDtoWithItems), actualRequestDtos);
    }

    @Test
    void getAllRequestsOfUser_whenUserNotFound_thenUserNotFoundExceptionThrown() {

        assertThatThrownBy(
                () -> requestService.getAllRequestsOfUser(anyInt())
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("User with this ID doesn't exist.");
    }

    @Test
    void getRequestById_whenInvoked_thenResponseStatusOkWithRequest() {
        when(requestRepository.findById(anyInt()))
                .thenReturn(Optional.of(request));
        when(userService.findUserById(anyInt()))
                .thenReturn(user);

        ItemRequestDtoWithItems requestDtoWithItems = requestMapper.toItemRequestDtoWithItems(request, Collections.emptyList());

        ItemRequestDtoWithItems actualRequestDtos = requestService.getRequestById(request.getId(), request.getRequestor().getId());

        assertNotNull(actualRequestDtos);
        assertEquals(requestDtoWithItems, actualRequestDtos);
    }

    @Test
    void getRequestById_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        ItemRequestDto requestDto = requestMapper.toItemRequestDto(request);

        assertThatThrownBy(
                () -> requestService.getRequestById(requestDto.getId(), anyInt())
        ).isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("User with this ID doesn't exist.");
    }

    @Test
    void getRequestById_whenRequestNotFound_thenRequestNotFoundExceptionThrown() {
        when(userService.findUserById(anyInt()))
                .thenReturn(user);

        assertThatThrownBy(
                () -> requestService.getRequestById(anyInt(), user.getId())
        ).isInstanceOf(RequestNotFoundException.class)
                .message().isEqualTo("Request  is not found");
    }


    @Test
    void getAllRequestsOfOthers_whenInvoked_thenResponseStatusOkWithItemCollection() {
        when(requestRepository.findAllByRequestor_IdIsNotOrderByCreatedDesc(anyInt(), any()))
                .thenReturn(List.of(request));

        ItemRequestDtoWithItems requestDtoWithItems = requestMapper.toItemRequestDtoWithItems(request, Collections.emptyList());

        Collection<ItemRequestDtoWithItems> actualRequestDtos = requestService.getAllRequestsOfOthers(any(), anyInt());

        assertNotNull(actualRequestDtos);
        assertEquals(List.of(requestDtoWithItems), actualRequestDtos);
    }
}