package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public interface UserService {
    UserDto addNewUser(UserDto userDto) throws UserValidationException;

    UserDto.UserUpdateDto updateUser(UserDto.UserUpdateDto userDtoToUpd, int userId);

    List<UserDto> getAllUsers();

    UserDto getUserById(int userId);

    void deleteUserById(int id);

    User findUserById(int userId);
}
