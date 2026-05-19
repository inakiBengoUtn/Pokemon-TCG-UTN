package com.pokemon.tcg.modules.game.dto.requests;

import com.pokemon.tcg.modules.game.domain.ActionType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerActionRequest {
    private ActionType type;
    private String playerId;
    private String cardId;
    private String targetId;
}
