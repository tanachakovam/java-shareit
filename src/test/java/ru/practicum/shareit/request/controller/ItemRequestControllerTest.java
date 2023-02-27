package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestServiceImpl requestService;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoWithItems requestDtoWithItems;


    @BeforeEach
    void setUp() {
        ItemRequestDtoWithItems.Requestor requestor2 = ItemRequestDtoWithItems.Requestor.builder().id(2).build();
        itemRequestDto = ItemRequestDto.builder().id(1).description("description").created(LocalDateTime.now()).build();
        requestDtoWithItems = ItemRequestDtoWithItems.builder().id(1).description("description").created(LocalDateTime.now()).requestor(requestor2).items(null).build();
    }

    @SneakyThrows
    @Test
    void addNewItemRequest_whenRequestIsValid_thenCreatedRequest() {
       when(requestService.addNewItemRequest(itemRequestDto, 1))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
        verify(requestService, times(1)).addNewItemRequest(itemRequestDto, 1);

    }


    @SneakyThrows
    @Test
    void addNewItem_whenItemDescriptionIsNotValid_thenReturnedBadRequest() {
        itemRequestDto.setDescription(null);

        mockMvc.perform(post("/requests")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).addNewItemRequest(itemRequestDto, 1);
    }


    @SneakyThrows
    @Test
    void getRequestsOfUser_whenInvoked_thenResponseStatusOkWithCollectionInBody() {
        when(requestService.getAllRequestsOfUser(1))
                .thenReturn(List.of(requestDtoWithItems));


        String result = mockMvc.perform(get("/requests")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDtoWithItems)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService, times(1)).getAllRequestsOfUser(1);
        assertEquals(objectMapper.writeValueAsString(List.of(requestDtoWithItems)), result);
    }

    @SneakyThrows
    @Test
    void getRequestsOfOthers_whenInvoked_thenResponseStatusOkWithCollectionInBody() {
        when(requestService.getAllRequestsOfOthers(PageRequest.of(0, 20), 1))
                .thenReturn(List.of(requestDtoWithItems));


        String result = mockMvc.perform(get("/requests/all")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDtoWithItems)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService, times(1)).getAllRequestsOfOthers(PageRequest.of(0, 20), 1);
        assertEquals(objectMapper.writeValueAsString(List.of(requestDtoWithItems)), result);
    }


    @SneakyThrows
    @Test
    void getRequestById_whenInvoked_thenResponseStatusOkWithRequestInBody() {
        when(requestService.getRequestById(1, 1))
                .thenReturn(requestDtoWithItems);


        String result = mockMvc.perform(get("/requests/{requestId}", 1)
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDtoWithItems)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService, times(1)).getRequestById(1, 1);
        assertEquals(objectMapper.writeValueAsString(requestDtoWithItems), result);
    }
}