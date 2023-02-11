package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    UserDto.UserUpdateDto toUserDtoToUpd(User user);

    User toUserToUpd(UserDto.UserUpdateDto userDtoToUpd);
}
