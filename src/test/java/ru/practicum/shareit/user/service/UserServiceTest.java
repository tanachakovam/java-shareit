package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapperImpl userMapper;

    @Captor
    private ArgumentCaptor<User> argumentCaptor;

    @BeforeEach
    void beforeEach() {
        userMapper = new UserMapperImpl();
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkWithUserCollection() {
        User user = new User(1, "user 1", "user1@email");
        final List<User> users = List.of(user);
        when(userRepository.findAll())
                .thenReturn(users);

        final Collection<UserDto> userDtos = userService.getAllUsers();
        assertNotNull(userDtos);
        assertEquals(1, userDtos.size());
        assertEquals(users, userMapper.toUser(userDtos));
    }

    @Test
    void addNewUser_whenUserEmailValid_thenSavedUser() {
        User userToSave = new User();
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUserDto = userService.addNewUser(userMapper.toUserDto(userToSave));
        User actualUser = userMapper.toUser(actualUserDto);

        assertEquals(userToSave, actualUser);
        verify(userRepository).save(userToSave);
    }

    @Test
    void updateUser_whenUserFound_thenUpdateFields() {
        int userId = 0;
        User oldUser = new User();
        oldUser.setName("user 1");
        oldUser.setEmail("user1@email");

        User newUser = new User();
        newUser.setName("user 2");
        newUser.setEmail("user2@email");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.updateUser(userMapper.toUserDtoToUpd(newUser), userId);

        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();

        assertEquals("user 2", savedUser.getName());
        assertEquals("user2@email", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserFound_thenUpdateOnlyEmail() {
        int userId = 0;
        User oldUser = new User();
        oldUser.setName("user 1");
        oldUser.setEmail("user1@email");

        User newUser = new User();
        newUser.setEmail("user2@email");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.updateUser(userMapper.toUserDtoToUpd(newUser), userId);

        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();

        assertEquals("user 1", savedUser.getName());
        assertEquals("user2@email", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserFound_thenUpdateOnlyName() {
        int userId = 0;
        User oldUser = new User();
        oldUser.setName("user 1");
        oldUser.setEmail("user1@email");

        User newUser = new User();
        newUser.setName("user 2");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.updateUser(userMapper.toUserDtoToUpd(newUser), userId);

        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();

        assertEquals("user 2", savedUser.getName());
        assertEquals("user1@email", savedUser.getEmail());
    }


    @Test
    void getUserById_whenUserFound_thenReturnedUser() {
        int userId = 0;
        User expectedUser = new User(1, "user 1", "user1@email");

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userMapper.toUser(userService.getUserById(userId));
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserById_whenUserNotFound_thenUserNotFoundExceptionThrown() {

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(anyInt()));
    }

    @Test
    void findUserById_whenUserFound_thenReturnedUser() {
        int userId = 0;
        User expectedUser = new User(1, "user 1", "user1@email");

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.findUserById(userId);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void findUserById_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        int userId = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.findUserById(userId));
    }

    @Test
    void deleteUserById_whenUserFound_thenDeletedUser() {
        int userId = 0;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
