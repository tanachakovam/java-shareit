package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    private UserDto user;

    @BeforeEach
    void setUp() {
        user = UserDto.builder().name("user").email("user@email").build();

    }

    @SneakyThrows
    @Test
    void getUserById_whenInvoked_thenResponseStatusOkWithUserInBody() {

        when(userService.getUserById(anyInt())).thenReturn(user);

        String result = mockMvc.perform(get("/users/{id}", anyInt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getUserById(anyInt());
        assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @SneakyThrows
    @Test
    void addNewUser_whenUserIsValid_thenCreatedUser() {
        user.setEmail("user@email");

        when(userService.addNewUser(user)).thenReturn(user);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @SneakyThrows
    @Test
    void addNewUser_whenUserIsNotValid_thenReturnedBadRequest() {
        user.setEmail("null");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addNewUser(user);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIsNotValid_thenReturnedBadRequest() {
        int userId = 0;
        UserDto.UserUpdateDto userToUpdate = new UserDto.UserUpdateDto();
        userToUpdate.setEmail("null");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(userToUpdate, userId);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIsValid_thenUpdatedUser() {
        int userId = 0;
        UserDto.UserUpdateDto userToUpdate = new UserDto.UserUpdateDto();
        userToUpdate.setEmail("userNew@email");

        when(userService.updateUser(userToUpdate, userId)).thenReturn(userToUpdate);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userToUpdate), result);
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkWithUserCollectionInBody() {
        when(userService.getAllUsers())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void deleteUserById_whenInvoked_thenResponseStatusOk() {
        int id = 0;
        doNothing().when(userService).deleteUserById(id);

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(id);
    }
}