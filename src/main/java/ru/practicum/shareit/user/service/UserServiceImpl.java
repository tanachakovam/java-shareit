package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional
    @Override
    public List<UserDto> getAllUsers() {
        Collection<User> users = userRepository.findAll();
        List<UserDto> foundUsers = new ArrayList<>();
        for (User user : users) {
            foundUsers.add(userMapper.toUserDto(user));
        }
        return foundUsers;
    }

    @Transactional
    @Override
    public UserDto addNewUser(UserDto userDto) throws UserValidationException {
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