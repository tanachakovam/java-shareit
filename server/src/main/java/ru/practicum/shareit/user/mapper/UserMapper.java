package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDto userDto);

    List<User> toUser(Collection<UserDto> userDto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDto(Collection<User> user);

    UserDto.UserUpdateDto toUserDtoToUpd(User user);

    User toUserToUpd(UserDto.UserUpdateDto userDtoToUpd);
}
