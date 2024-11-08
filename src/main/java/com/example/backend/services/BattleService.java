package com.example.backend.services;

import com.example.backend.model.User;
import com.example.backend.model.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BattleService {

    private final UserService userService;

    @Autowired
    public BattleService(UserService userService) {
        this.userService = userService;
    }

    public void sendInvite(User inviter) {
        inviter.setStatus(UserStatus.AWAITING_RESPONSE);
        userService.saveUser(inviter);
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
