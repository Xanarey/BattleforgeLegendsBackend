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

    public void leaveBattle(Map<String, String> payload) {
        String username = payload.get("username");
        String opponentUsername = payload.get("opponentUsername");
        String battleId = payload.get("battleId");

        User user = userService.getUserByUsername(username);
        User opponent = userService.getUserByUsername(opponentUsername);

        user.setStatus(UserStatus.ONLINE);
        opponent.setStatus(UserStatus.ONLINE);

        userService.saveUser(user);
        userService.saveUser(opponent);

        updateUserStatus(user);
        updateUserStatus(opponent);

        sendLeaveNotification(opponentUsername, "Ваш соперник покинул бой. Вы победили!", "/menu", false);
        sendLeaveNotification(username, "Вы покинули бой.", null, true); // Для покидающего игрока установим reloadPage = true


    }

    private void updateUserStatus(User user) {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("username", user.getUsername());
        statusUpdate.put("status", user.getStatus().toString());
        messagingTemplate.convertAndSend("/topic/status", statusUpdate);
    }

    private void sendLeaveNotification(String username, String message, String redirectUrl, boolean refreshPage) {
        Map<String, String> notification = new HashMap<>();
        notification.put("message", message);
        if (redirectUrl != null) {
            notification.put("redirectUrl", redirectUrl);
        }
        if (refreshPage) {
            notification.put("refreshPage", "true");
        }
        messagingTemplate.convertAndSendToUser(username, "/queue/leave", notification);
    }


    public void startBattle(User inviter, User invitee) {
        inviter.setStatus(UserStatus.IN_GAME);
        invitee.setStatus(UserStatus.IN_GAME);
        userService.saveUser(inviter);
        userService.saveUser(invitee);

        updateUserStatus(inviter);
        updateUserStatus(invitee);
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

    public void declineInvite(User inviter) {
        inviter.setStatus(UserStatus.ONLINE);
        userService.saveUser(inviter);
    }
}
