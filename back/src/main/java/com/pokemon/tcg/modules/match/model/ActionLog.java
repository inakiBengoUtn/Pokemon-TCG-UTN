package com.pokemon.tcg.modules.match.model;

import com.pokemon.tcg.common.enums.ActionType;
import com.pokemon.tcg.modules.user.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro inmutable de cada acción ocurrida en la partida.
 * Sirve para auditoría, revisión y reconexión de jugadores.
 */
@Entity
@Table(name = "action_logs", indexes = {
    @Index(name = "idx_action_logs_match", columnList = "match_id"),
    @Index(name = "idx_action_logs_match_turn", columnList = "match_id, turn_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "turn_number", nullable = false)
    private int turnNumber;

    // null para eventos del sistema (inicio/fin de partida, efectos entre turnos)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private User player;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    // JSON con detalles específicos de la acción (cardId, targetId, damage, etc.)
    @Column(columnDefinition = "TEXT")
    private String detailsJson;

    // Descripción legible del resultado (para mostrar en el log del tablero)
    @Column(length = 500)
    private String resultMessage;

    @CreationTimestamp
    private LocalDateTime timestamp;
}
