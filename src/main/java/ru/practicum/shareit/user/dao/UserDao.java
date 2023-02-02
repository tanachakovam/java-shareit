package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao {
    private Map<Integer, User> users = new HashMap<>();
    private int id;

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User addNewUser(User user) throws UserValidationException {
        checkEmail(user);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user, int userId) {
        if (users.get(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        checkEmail(user);
        if (user.getEmail() == null) {
            user.setEmail(users.get(userId).getEmail());
        }
        if (user.getName() == null) {
            user.setName(users.get(userId).getName());
        }
        users.put(userId, user);
        user.setId(userId);
        return users.get(userId);
    }

    private void checkEmail(User user) throws UserValidationException {
        for (User addedUser : users.values()) {
            if (addedUser.getEmail().equals(user.getEmail())) {
                throw new UserValidationException("User with this email already exists.");
            }

        }
    }

    public User getUserById(int userId) {
        if (users.get(userId) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        return users.get(userId);
    }

    public void deleteUserById(int userId) {
        users.remove(userId);
    }
}
