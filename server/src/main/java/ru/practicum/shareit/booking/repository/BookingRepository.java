package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdOrderByStartDesc(int id, Pageable pageable);

    List<Booking> findAllByBookerId(int id);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(int id, BookingState status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int id, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int id, LocalDateTime now, Pageable pageable);

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int id, LocalDateTime now);


    List<Booking> findAllByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(int id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.id = ?1 ")
    List<Booking> findAllByItemId(int id);

    @Query(" select b from Booking b " +
            "where b.item.owner = ?1 " +
            "order by b.start desc ")
    List<Booking> findAllByOwner(int id, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.owner = ?1 " +
            "order by b.start desc ")
    Collection<Booking> findAllByOwner(int id);

    @Query(" select b from Booking b " +
            "where b.item.owner = ?1 " +
            "and b.status = ?2  " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerAndStatusAndOrderByStartDesc(int id, BookingState status, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.owner = ?1 " +
            "and b.start > ?2  " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerAndStartAfterOrderByStartDesc(int id, LocalDateTime now, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.owner = ?1 " +
            "and b.end < ?2  " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerAndEndBeforeOrderByStartDesc(int id, LocalDateTime now, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.owner = ?1 " +
            "and b.start < ?2  " +
            "and b.end > ?3 " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerAndEndAfterAndStartBeforeOrderByStartDesc(int id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(" select distinct b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.end < ?2 " +
            "and b.item.owner = ?3 " +
            "order by b.start desc ")
    List<Booking> findLastBooking(int itemId, LocalDateTime now, int userId);

    @Query(" select distinct b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.start >  ?2 " +
            "and b.item.owner = ?3 " +
            "order by b.start desc ")
    List<Booking> findNextBooking(int itemId, LocalDateTime now, int userId);
}
