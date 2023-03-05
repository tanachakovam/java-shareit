package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDtoWithItems {
    private Integer id;
    @NotBlank
    private String description;
    private LocalDateTime created;
    private Requestor requestor;
    private List<ItemDto> items;

    @Data
    @Builder
    public static class Requestor {
        private final Integer id;
    }
}