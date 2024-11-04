package com.example.backend.controller;

import com.example.backend.model.Card;
import com.example.backend.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<List<Card>> getAllCards() {
        return ResponseEntity.ok(cardService.findAll());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Card>> getUserCards(@PathVariable String username) {
        return ResponseEntity.ok(cardService.findUserCards(username));
    }

    @PostMapping("/user/{username}/batch")
    public ResponseEntity<List<Card>> addCardsForUser(@PathVariable String username, @RequestBody List<Card> cards) {
        return ResponseEntity.ok(cardService.addCardsForUser(username, cards));
    }

    @PostMapping("/user/{username}")
    public ResponseEntity<Card> addCardForUser(@PathVariable String username, @RequestBody Card card) {
        return ResponseEntity.ok(cardService.addCardForUser(username, card));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<Card> updateCard(@PathVariable Long cardId, @RequestBody Card updatedCard) {
        return ResponseEntity.ok(cardService.updateCard(cardId, updatedCard));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}
