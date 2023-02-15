package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.mapping.Collection;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;

import javax.persistence.*;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */


@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private Integer request;
}
