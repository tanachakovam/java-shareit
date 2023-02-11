package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;


import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerId(int id);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByIdDesc(int id, BookingStatus status);

    List<Booking> findAllByBookerIdAndStartAfterOrderByIdDesc(int id, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByIdDesc(int id, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndBeforeAndStartAfterOrderByIdDesc(int id, LocalDateTime start, LocalDateTime end);

    @Query(" select b from Booking b, Item i " +
            "where i.owner = ?1 " +
            "order by b.id desc ")
    List<Booking> findAllByOwner(int id);

    @Query(" select b from Booking b, Item i " +
            "where i.owner = ?1 " +
            "and b.status = ?2  " +
            "order by b.id desc ")
    List<Booking> findAllByOwnerAndStatusAndOrderByIdDesc(int id, BookingStatus status);

    @Query(" select b from Booking b, Item i " +
            "where i.owner = ?1 " +
            "and b.start > ?2  " +
            "order by b.id desc ")
    List<Booking> findAllByOwnerAndStartAfterOrderByIdDesc(int id, LocalDateTime now);

    @Query(" select b from Booking b, Item i " +
            "where i.owner = ?1 " +
            "and b.end < ?2  " +
            "order by b.id desc ")
    List<Booking> findAllByOwnerAndEndBeforeOrderByIdDesc(int id, LocalDateTime now);

    @Query(" select b from Booking b, Item i " +
            "where i.owner = ?1 " +
            "and b.start < ?2  " +
            "and b.end > ?3 " +
            "order by b.id desc ")
    List<Booking> findAllByOwnerAndEndBeforeAndStartAfterOrderByIdDesc(int id, LocalDateTime start, LocalDateTime end);
}