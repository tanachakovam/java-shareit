package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public UserDto addNewUser(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User addedUser = userService.addNewUser(user);
        return UserMapper.toUserDto(addedUser);
    }

    @PatchMapping("/{userId}")
    public UserDto.UserUpdateDto updateUser(@Valid @RequestBody UserDto.UserUpdateDto userDtoToUpd, @PathVariable String userId) {
        User user = UserMapper.toUserToUpd(userDtoToUpd);
        User updatedUser = userService.updateUser(user, Integer.parseInt(userId));
        return UserMapper.toUserDtoToUpd(updatedUser);
    }

    @GetMapping
    public Collection<UserDto> returnUsers() {
        Collection<User> users = userService.getAllUsers();
        List<UserDto> foundUsers = new ArrayList<>();
        for (User user : users) {
            foundUsers.add(UserMapper.toUserDto(user));
        }
        return foundUsers;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable String id) {
        User foundUser = userService.getUserById(Integer.parseInt(id));
        return UserMapper.toUserDto(foundUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable String id) {
        userService.deleteUserById(Integer.parseInt(id));
    }
}
