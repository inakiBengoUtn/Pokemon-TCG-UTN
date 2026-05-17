package com.pokemon.tcg.modules.match.model;

import com.pokemon.tcg.common.enums.MatchStatus;
import com.pokemon.tcg.common.enums.TurnPhase;
import com.pokemon.tcg.modules.deck.model.Deck;
import com.pokemon.tcg.modules.user.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id", nullable = false)
    private User player1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id")
    private User player2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_deck_id")
    private Deck player1Deck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_deck_id")
    private Deck player2Deck;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MatchStatus status = MatchStatus.WAITING;

    // Jugador cuyo turno es actualmente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_turn_player_id")
    private User currentTurnPlayer;

    @Column(nullable = false)
    @Builder.Default
    private int turnNumber = 0;

    @Enumerated(EnumType.STRING)
    private TurnPhase turnPhase;

    // Ganador (null hasta que la partida termine)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    // true si es una partida de Muerte Súbita
    @Column(nullable = false)
    @Builder.Default
    private boolean suddenDeath = false;
}
