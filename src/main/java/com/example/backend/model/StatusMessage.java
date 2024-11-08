package com.example.backend.model;

import lombok.Data;

@Data
public class StatusMessage {
    private String username;
    private String status;

    public StatusMessage() {}

    public StatusMessage(String username, String status) {
        this.username = username;
        this.status = status;
    }


}
