package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
public class BookingDto {
    private Integer id;
    @Column(name = "start_day")
    private LocalDateTime start;
    @Column(name = "end_day")
    private LocalDateTime end;
    private Integer itemId;
    private Item item;
    private BookingStatus status;
    private Booker booker;

    @Data
    public static class Booker {
        private final Integer id;
        private final String name;
    }

    @Data
    public static class Item {
        private final Integer id;
        private final String name;
    }


    @Data
    public static class BookingDtoForOwner {
        private Integer id;
        private Integer bookerId;
    }

}