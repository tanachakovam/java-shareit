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
    private String itemName;
    private BookingStatus status;
    private Integer bookerId;


}
