package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@RequiredArgsConstructor
public class Item {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Integer owner;
    private Integer request;

    public Item(String name, String description, Boolean available, Integer owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
