package ru.practicum.shareit.booking.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_day")
    private LocalDateTime start;
    @Column(name = "end_day")
    private LocalDateTime end;
    @Column(name = "item")
    private Integer itemId;
    @Column(name = "booker")
    private Integer bookerId;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    private String itemName;
}