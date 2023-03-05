package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@Builder
public class ItemDtoFull {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto.BookingDtoForOwner lastBooking;
    private BookingDto.BookingDtoForOwner nextBooking;
    private List<CommentDto> comments;
}