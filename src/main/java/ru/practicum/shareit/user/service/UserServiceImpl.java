package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional
    @Override
    public List<UserDto> getAllUsers() {
        Collection<User> users = userRepository.findAll();
        return userMapper.toUserDto(users);
    }

    @Transactional
    @Override
    public UserDto addNewUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User addedUser = userRepository.save(user);
        return userMapper.toUserDto(addedUser);
    }

    @Transactional
    @Modifying
    @Override
    public UserDto.UserUpdateDto updateUser(UserDto.UserUpdateDto userDtoToUpd, int userId) {
        User user = userMapper.toUserToUpd(userDtoToUpd);
        User userToUpdate = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User " + userId + " is not found"));
        if (user.getEmail() == null) {
            userToUpdate.setEmail(userToUpdate.getEmail());
        } else {
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() == null) {
            userToUpdate.setName(userToUpdate.getName());
        } else {
            userToUpdate.setName(user.getName());
        }
        userRepository.save(userToUpdate);
        return userMapper.toUserDtoToUpd(userToUpdate);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(int userId) {
        User foundUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User " + userId + " is not found"));
        return userMapper.toUserDto(foundUser);
    }

    @Transactional(readOnly = true)
    @Override
    public User findUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User " + userId + " is not found"));
    }

    @Transactional
    @Override
    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }
}