package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check-user")
    public ResponseEntity<String> checkUser(@RequestParam String username) {
        System.out.println("Получен запрос на проверку пользователя: " + username); // Логирование
        Optional<User> user = Optional.ofNullable(userService.getUserByUsername(username));
        if (user.isPresent()) {
            System.out.println("Пользователь найден: " + username);
            return ResponseEntity.ok("User found");
        } else {
            System.out.println("Пользователь не найден: " + username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/exceptCurrentUser/{username}")
    public ResponseEntity<List<User>> getAllUsersExceptCurrentUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getAllExceptCurrentUser(username));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateUser(username, updatedUser));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
