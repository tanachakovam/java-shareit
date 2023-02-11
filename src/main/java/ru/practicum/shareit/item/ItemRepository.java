package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository  extends JpaRepository<Item, Integer> {

    @Query(" select i from Item i " +
            "where i.available is true" +
            " and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);


    List<Item> findAllByOwner(int id);



}