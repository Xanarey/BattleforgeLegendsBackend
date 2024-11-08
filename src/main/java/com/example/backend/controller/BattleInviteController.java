package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.model.UserStatus;
import com.example.backend.services.BattleService;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/battle")
public class BattleInviteController {

    private final UserService userService;
    private final BattleService battleService;

    @Autowired
    public BattleInviteController(UserService userService, BattleService battleService) {
        this.userService = userService;
        this.battleService = battleService;
    }

    @PostMapping("/invite/{username}")
    public ResponseEntity<String> invitePlayer(@PathVariable String username, @RequestParam String inviterUsername) {
        User inviter = userService.getUserByUsername(inviterUsername);
        User invitee = userService.getUserByUsername(username);

        if (invitee.getStatus() == UserStatus.AWAITING_RESPONSE)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Игрок в игре");

        if (invitee.getStatus() == UserStatus.ONLINE) {
            battleService.sendInvite(inviter);
            return ResponseEntity.ok("Приглашение отправлено");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Игрок не в сети");
        }
    }

    @PostMapping("/invite/response")
    public ResponseEntity<String> respondToInvite(@RequestParam String inviteeUsername,
                                                  @RequestParam String inviterUsername,
                                                  @RequestParam boolean accepted) {
        User invitee = userService.getUserByUsername(inviteeUsername);
        User inviter = userService.getUserByUsername(inviterUsername);

        if (accepted && inviter.getStatus() == UserStatus.AWAITING_RESPONSE) {
            battleService.startBattle(inviter, invitee);
            return ResponseEntity.ok("Бой начался");
        } else {
            battleService.declineInvite(inviter);
            return ResponseEntity.ok("Приглашение отклонено");
        }
    }

    @GetMapping("/invite/status/{inviterUsername}")
    public ResponseEntity<String> checkInviteStatus(@PathVariable String inviterUsername) {
        User inviter = userService.getUserByUsername(inviterUsername);
        if (inviter.getStatus() == UserStatus.AWAITING_RESPONSE) {
            return ResponseEntity.ok("Ожидание ответа");
        } else if (inviter.getStatus() == UserStatus.IN_GAME) {
            return ResponseEntity.ok("Игрок в режиме -Бой- ");
        } else {
            if (inviter.getStatus() == UserStatus.OFFLINE) {
                return ResponseEntity.ok("Не в сети");
            }
            return ResponseEntity.ok("В сети");
        }
    }

    @PostMapping("/end")
    public ResponseEntity<String> endBattle(@RequestParam String inviter, @RequestParam String invitee) {
        User userOne = userService.getUserByUsername(inviter);
        User userTwo = userService.getUserByUsername(invitee);

        battleService.endBattle(userOne, userTwo);
        return ResponseEntity.ok("Бой завершен");
    }

}
