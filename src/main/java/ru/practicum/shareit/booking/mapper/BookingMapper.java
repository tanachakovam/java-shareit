package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;


//@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingDto bookingDto);

    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingDto.BookingDtoForOwner toBookingDtoForOwner(Booking booking);
}
