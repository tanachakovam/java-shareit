package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    private Integer id;
    @NotBlank
    private String description;
    private LocalDateTime created;
    private Requestor requestor;

    @Data
    @Builder
    public static class Requestor {
        private final Integer id;
    }
}
