package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */


@Entity
@Table(name = "items")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private Integer requestId;
}
