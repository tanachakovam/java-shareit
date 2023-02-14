package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    @Column(name = "item_id")
    private Integer itemId;
    @JoinColumn(name = "author_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    private LocalDateTime created;
}
