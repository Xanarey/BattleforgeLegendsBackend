package com.example.backend.services;

import com.example.backend.model.Card;
import com.example.backend.model.User;
import com.example.backend.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    @Autowired
    public CardService(CardRepository cardRepository, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    public List<Card> findUserCards(String username) {
        return cardRepository.findAllByUserId(userService.getUserByUsername(username).getId());
    }

    public List<Card> addCardsForUser(String username, List<Card> cards) {
        User user = userService.getUserByUsername(username);
        for (Card card : cards) {
            card.setUser(user);
        }
        user.getCards().addAll(cards);
        return cardRepository.saveAll(cards);
    }

    public Card addCardForUser(String username, Card card) {
        User user = userService.getUserByUsername(username);
        card.setUser(user);
        user.getCards().add(card);
        return cardRepository.save(card);
    }

    public Card updateCard(Long id, Card updatedCard) {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        existingCard.setAttack(updatedCard.getAttack());
        existingCard.setHealth(updatedCard.getHealth());

        if (updatedCard.getUser() != null && !updatedCard.getUser().equals(existingCard.getUser())) {
            User user = userService.getUserByUsername(updatedCard.getUser().getUsername());
            existingCard.setUser(user);
        }

        return cardRepository.save(existingCard);
    }

    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }
}
