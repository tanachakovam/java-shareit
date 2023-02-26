package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingServiceImpl bookingService;
    private BookingDto bookingDto;


    @BeforeEach
    void setUp() {
        BookingDto.Booker booker = BookingDto.Booker.builder().id(1).name("booker").build();
        BookingDto.Item item = BookingDto.Item.builder().id(1).name("item").build();
        bookingDto = BookingDto.builder().end(LocalDateTime.now()).start(LocalDateTime.now()).status(BookingStatus.WAITING).itemId(item.getId()).item(item).booker(booker).build();
    }

    @SneakyThrows
    @Test
    void addNewBooking_whenBookingIsValid_thenCreatedBooking() {
        when(bookingService.addNewBooking(bookingDto, 1)).thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void approveBooking_whenBookingIsValid_thenApprovedBooking() {
        when(bookingService.approveBooking(1, true, 1)).thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}?approved=true", 1, true, 1)
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void getBooking_whenInvoked_thenResponseStatusOkWithBookingInBody() {
        when(bookingService.getBooking(1, 1)).thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBooking(1, 1);
        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void getBookingsOfUser_whenInvoked_thenResponseStatusOkWithCollectionInBody() {
        when(bookingService.getBookingsOfUser("ALL", 1, PageRequest.of(0, 20)))
                .thenReturn(List.of(bookingDto));

        String result = mockMvc.perform(get("/bookings")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, times(1)).getBookingsOfUser("ALL", 1, PageRequest.of(0, 20));
        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto)), result);
    }

    @SneakyThrows
    @Test
    void getBookingsOfOwner_whenInvoked_thenResponseStatusOkWithCollectionInBody() {
        when(bookingService.getBookingsOfOwner("ALL", 1, PageRequest.of(0, 20)))
                .thenReturn(List.of(bookingDto));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header(SHARER_USER_ID, 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, times(1)).getBookingsOfOwner("ALL", 1, PageRequest.of(0, 20));
        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto)), result);
    }
}