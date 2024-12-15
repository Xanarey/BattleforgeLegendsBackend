package com.example.backend.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Game {

    private String battleId;
    private String playerOne;
    private String playerTwo;

    private List<Card> playerOneCards;
    private List<Card> playerTwoCards;

    private LocalDateTime startTime;
}
