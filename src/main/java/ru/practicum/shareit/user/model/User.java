package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class User {
    private Integer id;
    private String name;
    @NotBlank
    @Email
    private String email;
}
