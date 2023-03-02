package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingDto bookingDto);

    @Mapping(target = "itemId", source = "booking.item.id")
    BookingDto toBookingDto(Booking booking);

    List<BookingDto> toBookingDtoCollection(List<Booking> bookings);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingDto.BookingDtoForOwner toBookingDtoForOwner(Booking booking);
}
