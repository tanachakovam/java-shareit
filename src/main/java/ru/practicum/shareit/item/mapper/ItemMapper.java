package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFull;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemDto itemDto);

    ItemDto toItemDto(Item item);

    @Mapping(target = "id", source = "item.id")
    ItemDtoFull toItemDtoFull(Item item, BookingDto.BookingDtoForOwner lastBooking, BookingDto.BookingDtoForOwner nextBooking, List<CommentDto> comments);

    Collection<ItemDto> toItemDtoCollection(Collection<Item> item);

}
