package com.pokemon.tcg.modules.game.dto.request;

import com.pokemon.tcg.common.enums.ActionType;
import lombok.*;

/**
 * Generic request envelope for every in-game action.
 * The meaningful fields depend on the action type:
 *
 * <ul>
 *   <li>PLACE_BASIC       — cardInstanceId (from hand), targetPosition ("ACTIVE"|"BENCH")</li>
 *   <li>ATTACH_ENERGY     — cardInstanceId (energy from hand), targetPokemonInstanceId</li>
 *   <li>EVOLVE            — cardInstanceId (evolution card from hand), targetPokemonInstanceId</li>
 *   <li>PLAY_ITEM         — cardInstanceId (item from hand)</li>
 *   <li>PLAY_SUPPORTER    — cardInstanceId (supporter from hand)</li>
 *   <li>PLAY_STADIUM      — cardInstanceId (stadium from hand)</li>
 *   <li>ATTACH_TOOL       — cardInstanceId (tool from hand), targetPokemonInstanceId</li>
 *   <li>RETREAT           — newActivePokemonInstanceId (bench pokemon to promote)</li>
 *   <li>ATTACK            — attackIndex (0-based index of the attack to use)</li>
 *   <li>END_TURN          — no extra fields</li>
 *   <li>SETUP_ACTIVE      — cardInstanceId (basic from hand to place as active)</li>
 *   <li>SETUP_BENCH       — cardInstanceId (basic from hand to place on bench)</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameActionRequest {

    private ActionType type;

    // The card being played from hand (instanceId)
    private String cardInstanceId;

    // Target Pokémon in play (instanceId)
    private String targetPokemonInstanceId;

    // For PLACE_BASIC: "ACTIVE" or "BENCH"
    private String targetPosition;

    // For RETREAT: the bench Pokémon to promote to active
    private String newActivePokemonInstanceId;

    // For ATTACK: 0-based index of the attack on the active Pokémon's card
    private int attackIndex;
}
