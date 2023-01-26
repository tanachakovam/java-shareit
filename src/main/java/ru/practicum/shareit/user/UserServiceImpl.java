package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;


import java.util.*;

@Service
@Component
@Slf4j
public class UserServiceImpl implements UserService {
    private Map<Integer, User> users = new HashMap<>();
    private int id;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addNewUser(User user) throws UserValidationException {
        checkEmail(user);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user, int userId) {
        if (!users.containsKey(userId)) {
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


    @Override
    public User getUserById(int userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        return users.get(userId);
    }

    @Override
    public void deleteUserById(int userId) {
        users.remove(userId);
    }
}