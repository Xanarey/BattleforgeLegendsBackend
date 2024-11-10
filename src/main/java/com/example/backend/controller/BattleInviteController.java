package com.example.backend.controller;

import com.example.backend.dto.InviteRequest;
import com.example.backend.model.User;
import com.example.backend.model.UserStatus;
import com.example.backend.services.BattleService;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/battle")
public class BattleInviteController {

    private final UserService userService;
    private final BattleService battleService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public BattleInviteController(UserService userService, BattleService battleService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.battleService = battleService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/invite")
    public ResponseEntity<String> invitePlayer(@RequestBody InviteRequest inviteRequest) {
        User inviter = userService.getUserByUsername(inviteRequest.getInviterUsername());
        User invitee = userService.getUserByUsername(inviteRequest.getInviteeUsername());

        System.out.println("BattleInviteController.invitePlayer : " + inviter.getUsername() + " приглашает в бой " + invitee.getUsername() + "'a");

        battleService.sendInvite(inviter, invitee);

        return ResponseEntity.ok("Приглашение отправлено");
    }


    @PostMapping("/invite/response")
    public ResponseEntity<?> respondToInvite(@RequestBody InviteRequest inviteRequest) {
        User inviter = userService.getUserByUsername(inviteRequest.getInviterUsername());
        User invitee = userService.getUserByUsername(inviteRequest.getInviteeUsername());

        String battleId = UUID.randomUUID().toString();

        if (inviteRequest.isChose()) {
            battleService.startBattle(inviter, invitee);

            Map<String, String> responseMessageStartBattle = new HashMap<>();
            responseMessageStartBattle.put("message", "Бой начат!");
            responseMessageStartBattle.put("battleId", battleId);
            responseMessageStartBattle.put("redirectUrl", "/battle/" + battleId);

            messagingTemplate.convertAndSendToUser(inviter.getUsername(), "/queue/start", responseMessageStartBattle);
            messagingTemplate.convertAndSendToUser(invitee.getUsername(), "/queue/start", responseMessageStartBattle);
        } else {
            battleService.declineInvite(inviter);

            Map<String, String> responseMessageDeclineInvite = new HashMap<>();
            responseMessageDeclineInvite.put("message", "Игрок " + invitee.getUsername() + " отклонил ваше приглашение.");
            messagingTemplate.convertAndSendToUser(inviter.getUsername(), "/queue/decline", responseMessageDeclineInvite);
        }



        return new ResponseEntity<>(HttpStatus.OK);
    }

}
