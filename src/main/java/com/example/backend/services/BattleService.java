package com.example.backend.services;

import com.example.backend.model.User;
import com.example.backend.model.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BattleService {

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public BattleService(UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendInvite(User inviter, User invitee) {
        System.out.println("Отправка приглашения пользователю: " + invitee.getUsername());
        System.out.println("Имя пригласившего: " + inviter.getUsername());
        Map<String, String> inviteData = new HashMap<>();
        inviteData.put("inviterUsername", inviter.getUsername());

        messagingTemplate.convertAndSend("/topic/test", "Тестовое сообщение для проверки");
        messagingTemplate.convertAndSendToUser(invitee.getUsername(), "/queue/invite", inviteData);
        System.out.println("Отправка сообщения через convertAndSendToUser выполнена.");

    }




    public void startBattle(User inviter, User invitee) {
        inviter.setStatus(UserStatus.IN_GAME);
        invitee.setStatus(UserStatus.IN_GAME);
        userService.saveUser(inviter);
        userService.saveUser(invitee);
    }

    public void declineInvite(User inviter) {
        inviter.setStatus(UserStatus.ONLINE);
        userService.saveUser(inviter);
    }

    public void endBattle(User inviter, User invitee) {
        inviter.setStatus(UserStatus.ONLINE);
        invitee.setStatus(UserStatus.ONLINE);
        userService.saveUser(inviter);
        userService.saveUser(invitee);
    }
}
