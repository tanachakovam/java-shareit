package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public UserDto addNewUser(@Valid @RequestBody UserDto userDto) {
        return userService.addNewUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto.UserUpdateDto updateUser(@Valid @RequestBody UserDto.UserUpdateDto userDtoToUpd, @PathVariable String userId) {
        return userService.updateUser(userDtoToUpd, Integer.parseInt(userId));
    }

    @GetMapping
    public Collection<UserDto> returnUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable String id) {
        return userService.getUserById(Integer.parseInt(id));
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable String id) {
        userService.deleteUserById(Integer.parseInt(id));
    }
}
