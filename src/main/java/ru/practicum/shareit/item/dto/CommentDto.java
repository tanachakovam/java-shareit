package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private int id;
    @NotBlank
    private String text;
    private Integer authorName;
    private LocalDateTime created;
}
