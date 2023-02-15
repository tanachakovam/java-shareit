package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;


@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingDto bookingDto);

    BookingDto toBookingDto(Booking booking);

    Collection<BookingDto> toBookingDtoCollection(Collection<Booking> booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingDto.BookingDtoForOwner toBookingDtoForOwner(Booking booking);
}
