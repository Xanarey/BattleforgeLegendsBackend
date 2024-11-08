package com.example.backend.controller;

import com.example.backend.model.PlayerStatusUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/status")
public class StatusController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public StatusController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/update")
    public void updateStatus(@RequestBody PlayerStatusUpdate update) {
        messagingTemplate.convertAndSend("/topic/status", update);
    }
}
