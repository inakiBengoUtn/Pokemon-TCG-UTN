package com.pokemon.tcg.modules.deck.service;

import com.pokemon.tcg.modules.card.dto.CardResponse;
import com.pokemon.tcg.modules.card.model.Card;
import com.pokemon.tcg.modules.card.repo.CardRepo;
import com.pokemon.tcg.modules.deck.dto.request.DeckCardRequest;
import com.pokemon.tcg.modules.deck.dto.request.SaveDeckRequest;
import com.pokemon.tcg.modules.deck.dto.response.DeckCardResponse;
import com.pokemon.tcg.modules.deck.dto.response.DeckResponse;
import com.pokemon.tcg.modules.deck.dto.response.DeckSummaryResponse;
import com.pokemon.tcg.modules.deck.dto.response.DeckValidationResponse;
import com.pokemon.tcg.modules.deck.model.Deck;
import com.pokemon.tcg.modules.deck.model.DeckCard;
import com.pokemon.tcg.modules.deck.repo.DeckCardRepo;
import com.pokemon.tcg.modules.deck.repo.DeckRepo;
import com.pokemon.tcg.modules.user.models.User;
import com.pokemon.tcg.modules.user.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepo deckRepo;
    private final DeckCardRepo deckCardRepo;
    private final CardRepo cardRepo;
    private final UserRepo userRepo;
    private final DeckValidationService validationService;

    public List<DeckSummaryResponse> getDecks(String username) {
        User user = findUser(username);
        return deckRepo.findByOwnerId(user.getId()).stream()
                .map(deck -> DeckSummaryResponse.builder()
                        .id(deck.getId())
                        .name(deck.getName())
                        .totalCards(deck.totalCards())
                        .valid(validationService.validate(deck).isValid())
                        .updatedAt(deck.getUpdatedAt())
                        .build())
                .toList();
    }

    public DeckResponse getDeck(String username, UUID deckId) {
        Deck deck = findDeckOwned(username, deckId);
        return toResponse(deck);
    }

    @Transactional
    public DeckResponse createDeck(String username, SaveDeckRequest request) {
        User user = findUser(username);

        Deck deck = Deck.builder()
                .owner(user)
                .name(request.getName())
                .cards(new ArrayList<>())
                .build();

        populateCards(deck, request.getCards());
        deckRepo.save(deck);
        return toResponse(deck);
    }

    @Transactional
    public DeckResponse updateDeck(String username, UUID deckId, SaveDeckRequest request) {
        Deck deck = findDeckOwned(username, deckId);

        deck.setName(request.getName());
        deck.getCards().clear();
        populateCards(deck, request.getCards());

        deckRepo.save(deck);
        return toResponse(deck);
    }

    @Transactional
    public void deleteDeck(String username, UUID deckId) {
        Deck deck = findDeckOwned(username, deckId);
        deckRepo.delete(deck);
    }

    public DeckValidationResponse validateDeck(String username, UUID deckId) {
        Deck deck = findDeckOwned(username, deckId);
        return validationService.validate(deck);
    }

    // --- helpers ---

    private void populateCards(Deck deck, List<DeckCardRequest> requests) {
        for (DeckCardRequest req : requests) {
            Card card = cardRepo.findById(req.getCardId())
                    .orElseThrow(() -> new EntityNotFoundException("Card not found: " + req.getCardId()));

            DeckCard deckCard = DeckCard.builder()
                    .deck(deck)
                    .card(card)
                    .quantity(req.getQuantity())
                    .build();
            deck.getCards().add(deckCard);
        }
    }

    private DeckResponse toResponse(Deck deck) {
        DeckValidationResponse validation = validationService.validate(deck);

        List<DeckCardResponse> cardResponses = deck.getCards().stream()
                .map(dc -> DeckCardResponse.builder()
                        .card(toCardResponse(dc.getCard()))
                        .quantity(dc.getQuantity())
                        .build())
                .toList();

        return DeckResponse.builder()
                .id(deck.getId())
                .name(deck.getName())
                .ownerUsername(deck.getOwner().getUsername())
                .totalCards(deck.totalCards())
                .cards(cardResponses)
                .validation(validation)
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .build();
    }

    private CardResponse toCardResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .name(card.getName())
                .supertype(card.getSupertype())
                .subtypes(card.getSubtypes())
                .hp(card.getHp())
                .types(card.getTypes())
                .evolvesFrom(card.getEvolvesFrom())
                .retreatCost(card.getRetreatCost())
                .weaknessType(card.getWeaknessType())
                .weaknessValue(card.getWeaknessValue())
                .resistanceType(card.getResistanceType())
                .resistanceValue(card.getResistanceValue())
                .attacksJson(card.getAttacksJson())
                .abilitiesJson(card.getAbilitiesJson())
                .imageUrlSmall(card.getImageUrlSmall())
                .imageUrlLarge(card.getImageUrlLarge())
                .setId(card.getSetId())
                .setName(card.getSetName())
                .number(card.getNumber())
                .rarity(card.getRarity())
                .aceTactico(card.isAceTactico())
                .basicEnergy(card.isBasicEnergy())
                .build();
    }

    private User findUser(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    private Deck findDeckOwned(String username, UUID deckId) {
        User user = findUser(username);
        Deck deck = deckRepo.findById(deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found: " + deckId));
        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Deck does not belong to the authenticated user");
        }
        return deck;
    }
}
