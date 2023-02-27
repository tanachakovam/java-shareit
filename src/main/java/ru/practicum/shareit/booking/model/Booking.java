package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Entity
@Table(name = "bookings")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_day")
    private LocalDateTime start;
    @Column(name = "end_day")
    private LocalDateTime end;
    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @JoinColumn(name = "booker_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

}