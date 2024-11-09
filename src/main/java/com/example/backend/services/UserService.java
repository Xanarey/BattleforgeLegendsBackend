package com.example.backend.services;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllExceptCurrentUser(String currentUsername) {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getUsername().equals(currentUsername))
                .toList();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(String username, User user) {
        User oldUser = getUserByUsername(username);
        oldUser.setUsername(user.getUsername());
        return userRepository.save(oldUser);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        userRepository.delete(getUserByUsername(username));
    }
}
