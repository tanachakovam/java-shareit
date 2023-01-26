package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }


    public static UserDto.UserUpdateDto toUserDtoToUpd(User user) {
        return new UserDto.UserUpdateDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static User toUserToUpd(UserDto.UserUpdateDto userDtoToUpd) {
        return new User(
                userDtoToUpd.getId(),
                userDtoToUpd.getName(),
                userDtoToUpd.getEmail()
        );
    }

}
