package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemDto itemDto);

    ItemDto toItemDto(Item item);

    @Mapping(target = "end", source = "booking.end")
    @Mapping(target = "start", source = "booking.start")
    @Mapping(target = "id", source = "item.id")
    ItemDto.ItemDtoForOwner toItemDtoForOwner(Item item, Booking booking);
}
