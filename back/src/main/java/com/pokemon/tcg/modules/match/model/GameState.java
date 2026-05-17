package com.pokemon.tcg.modules.match.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Persiste el estado completo del tablero después de cada acción relevante.
 * boardStateJson contiene la serialización del BoardState (manos, mazos,
 * Activo, Banca, pila de descarte, cartas de Premio, contadores, condiciones,
 * flags del turno).
 * Diseñado para reconstruir la partida ante cualquier desconexión.
 */
@Entity
@Table(name = "game_states")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameState {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    private Match match;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String boardStateJson;

    @UpdateTimestamp
    private LocalDateTime lastUpdatedAt;
}
