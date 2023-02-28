package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private ItemDto itemDto;
    private ItemDtoFull itemDtoFull;
    private CommentDto commentDto;
    @MockBean
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder().name("item").description("item description").available(Boolean.TRUE).build();
        BookingDto.BookingDtoForOwner lastBooking = BookingDto.BookingDtoForOwner.builder().id(1).bookerId(1).build();
        BookingDto.BookingDtoForOwner nextBooking = BookingDto.BookingDtoForOwner.builder().id(1).bookerId(1).build();
        itemDtoFull = ItemDtoFull.builder().name("item2").description("item2 description").available(Boolean.FALSE).nextBooking(nextBooking).lastBooking(lastBooking).build();

        commentDto = CommentDto.builder().text("comment").authorName("Name").created(LocalDateTime.now()).build();
    }

    @SneakyThrows
    @Test
    void getItemById_whenInvoked_thenResponseStatusOkWithItemInBody() {
        int itemId = 0;
        int userId = 0;

        when(itemService.getItemById(itemId, userId)).thenReturn(itemDtoFull);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(SHARER_USER_ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDtoFull)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, times(1)).getItemById(itemId, userId);
        assertEquals(objectMapper.writeValueAsString(itemDtoFull), result);
    }


    @SneakyThrows
    @Test
    void addNewItem_whenItemIsValid_thenCreatedItem() {

        when(itemService.addNewItem(itemDto, 1)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService, times(1)).addNewItem(itemDto, 1);
    }

    @SneakyThrows
    @Test
    void addNewItem_whenItemIsNotValid_thenReturnedBadRequest() {
        itemDto.setName(null);

        mockMvc.perform(post("/items")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addNewItem(itemDto, 1);
    }

    @SneakyThrows
    @Test
    void addNewItem_whenItemAvailableIsNotValid_thenReturnedBadRequest() {
        itemDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addNewItem(itemDto, 1);
    }

    @SneakyThrows
    @Test
    void addNewItem_whenItemDescriptionIsNotValid_thenReturnedBadRequest() {
        itemDto.setDescription(null);

        mockMvc.perform(post("/items")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addNewItem(itemDto, 1);
    }


    @SneakyThrows
    @Test
    void updateItem_whenItemIsValid_thenUpdatedItem() {

        when(itemService.updateItem(itemDto, 1, 1)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", 1)
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService, times(1)).updateItem(itemDto, 1, 1);
    }

    @SneakyThrows
    @Test
    void getItemsOfUser_whenInvoked_thenResponseStatusOkWithUserEmptyCollectionInBody() {
        when(itemService.getItemsOfUser(anyInt(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1)).getItemsOfUser(anyInt(), any());
    }

    @SneakyThrows
    @Test
    void getItemsOfUser_whenInvoked_thenResponseStatusOkWithItemCollectionInBody() {
        when(itemService.getItemsOfUser(anyInt(), any()))
                .thenReturn(List.of(itemDtoFull));

        String result = mockMvc.perform(get("/items")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemDtoFull)), result);
        verify(itemService, times(1)).getItemsOfUser(anyInt(), any());
    }

    @SneakyThrows
    @Test
    void addNewComment_whenCommentIsValid_thenCreated() {

        when(itemService.addNewComment(commentDto, 1, 1)).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, times(1)).addNewComment(commentDto, 1, 1);
        assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }

    @SneakyThrows
    @Test
    void addNewComment_whenCommentIsNotValid_thenReturnedBadRequest() {
        commentDto.setText(null);

        when(itemService.addNewComment(commentDto, 1, 1)).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addNewComment(commentDto, 1, 1);
    }

    @SneakyThrows
    @Test
    void search_whenInvoked_thenSearch() {

        when(itemService.search(anyString(), any())).thenReturn(List.of(itemDto));

        String result = mockMvc.perform(get("/items/search?text=item", "item")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemDto)), result);
        verify(itemService, times(1)).search(anyString(), any());
    }
}