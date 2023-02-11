package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query(" select b from Booking b " +
            "where b.itemId = ?1 " +
            " and b.bookerId = ?2 " +
            " and b.status = ?4 " +
            " and b.end < ?3 " )
    List<Item> checkIfUserBookedItem(int itemId, int userId, LocalDateTime end, BookingStatus status);
}
