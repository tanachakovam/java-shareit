package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserDao userDao;


    @Override
    public List<UserDto> getAllUsers() {
        Collection<User> users = userDao.getAllUsers();
        List<UserDto> foundUsers = new ArrayList<>();
        for (User user : users) {
            foundUsers.add(userMapper.toUserDto(user));
        }
        return foundUsers;
    }

    @Override
    public UserDto addNewUser(UserDto userDto) throws UserValidationException {
        User user = userMapper.toUser(userDto);
        User addedUser = userDao.addNewUser(user);
        return userMapper.toUserDto(addedUser);
    }

    @Override
    public UserDto.UserUpdateDto updateUser(UserDto.UserUpdateDto userDtoToUpd, int userId) {
        User user = userMapper.toUserToUpd(userDtoToUpd);
        User updatedUser = userDao.updateUser(user, userId);
        return userMapper.toUserDtoToUpd(updatedUser);
    }


    @Override
    public UserDto getUserById(int userId) {
        User foundUser = userDao.getUserById(userId);
        return userMapper.toUserDto(foundUser);
    }

    @Override
    public void deleteUserById(int userId) {
        userDao.deleteUserById(userId);
    }
}