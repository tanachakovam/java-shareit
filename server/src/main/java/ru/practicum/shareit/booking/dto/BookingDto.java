package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Builder
public class BookingDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer itemId;
    private Item item;
    private BookingState status;
    private Booker booker;

    @Builder
    @Data
    public static class Booker {
        private final Integer id;
        private final String name;
    }

    @Builder
    @Data
    public static class Item {
        private final Integer id;
        private final String name;
    }

    @Builder
    @Data
    public static class BookingDtoForOwner {
        private Integer id;
        private Integer bookerId;
    }
}