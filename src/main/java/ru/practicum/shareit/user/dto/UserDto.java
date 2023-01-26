package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String name;
    @NotBlank
    @Email
    private String email;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserUpdateDto{

        private Integer id;
        private String name;
        @Email
        private String email;

    }
}
