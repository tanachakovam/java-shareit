package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private Integer id;
    private String name;
    @NotBlank
    @Email
    private String email;

    @Data
    public static class UserUpdateDto {

        private Integer id;
        private String name;
        @Email
        private String email;

    }
}
