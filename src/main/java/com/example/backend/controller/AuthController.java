package com.example.backend.controller;

import com.example.backend.model.StatusMessage;
import com.example.backend.model.User;
import com.example.backend.model.UserStatus;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public AuthController(UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username) {
        Optional<User> user = Optional.ofNullable(userService.getUserByUsername(username));
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setStatus(UserStatus.ONLINE);
            userService.saveUser(existingUser);

            messagingTemplate.convertAndSend("/topic/status", new StatusMessage(username, "ONLINE"));

            return ResponseEntity.ok("User found");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @MessageMapping("/update-status")
    public StatusMessage updateStatus(StatusMessage statusMessage) {
        User user = userService.getUserByUsername(statusMessage.getUsername());
        if (user != null) {
            user.setStatus(UserStatus.valueOf(statusMessage.getStatus()));
            userService.saveUser(user);
            messagingTemplate.convertAndSend("/topic/status", new StatusMessage(user.getUsername(), statusMessage.getStatus()));
        }
        return statusMessage;
    }


}
