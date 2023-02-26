package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryIT {

    @Autowired
    UserRepository userRepository;
    User user1;
    Item item1;
    User user2;
    Item item2;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1, "user 1", "user1@email"));
        item1 = itemRepository.save(new Item(1, "item1", "item 1 description", false, user1.getId(), null));

        user2 = userRepository.save(new User(2, "user 2", "user2@email"));
        item2 = itemRepository.save(new Item(2, "item2", "item 2 descr", true, user2.getId(), null));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void search_whenNotFoundByText_thenEmptyList() {
        List<Item> actualItems = itemRepository.search("name");

        assertTrue(actualItems.isEmpty());
    }

    @Test
    void search_whenFoundByText_thenNotEmptyList() {
        List<Item> actualItems = itemRepository.search("item2");

        assertFalse(actualItems.isEmpty());
        assertEquals(1, actualItems.size());
        assertEquals(item2, actualItems.get(0));
    }

    @Test
    void findAllByOwner_whenNotFound_thenEmptyList() {
        List<Item> actualItems = itemRepository.findAllByOwner(999);

        assertTrue(actualItems.isEmpty());
    }

    @Test
    void findAllByRequestId_whenNotFound_thenEmptyList() {
        List<Item> actualItems = itemRepository.findAllByRequestId(1);

        assertTrue(actualItems.isEmpty());
    }
}