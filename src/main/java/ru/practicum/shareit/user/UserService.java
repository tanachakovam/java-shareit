package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;


public interface UserService {
    User addNewUser(User user) throws UserValidationException;

    User updateUser(User user, int userId);

    Collection<User> getAllUsers();

    User getUserById(int id);

    void deleteUserById(int id);
}
