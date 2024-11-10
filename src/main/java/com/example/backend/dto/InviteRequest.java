package com.example.backend.dto;

import lombok.Data;

@Data
public class InviteRequest {
    private String inviterUsername;
    private String inviteeUsername;
    private boolean chose;
}
