package com.pokemon.tcg.modules.game.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.tcg.common.enums.ActionType;
import com.pokemon.tcg.common.enums.MatchStatus;
import com.pokemon.tcg.common.enums.TurnPhase;
import com.pokemon.tcg.modules.deck.repo.DeckCardRepo;
import com.pokemon.tcg.modules.game.dto.request.GameActionRequest;
import com.pokemon.tcg.modules.game.engine.chain.AttackContext;
import com.pokemon.tcg.modules.game.engine.exception.GameNotFoundException;
import com.pokemon.tcg.modules.game.engine.exception.InvalidActionException;
import com.pokemon.tcg.modules.game.engine.model.*;
import com.pokemon.tcg.modules.match.model.ActionLog;
import com.pokemon.tcg.modules.match.model.GameState;
import com.pokemon.tcg.modules.match.model.Match;
import com.pokemon.tcg.modules.match.repo.ActionLogRepo;
import com.pokemon.tcg.modules.match.repo.GameStateRepo;
import com.pokemon.tcg.modules.match.repo.MatchRepo;
import com.pokemon.tcg.modules.user.models.User;
import com.pokemon.tcg.modules.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Facade that exposes the game engine to controllers.
 * All public methods load/save {@link BoardState} as JSON via {@link GameState}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameEngine {

    private final MatchRepo matchRepo;
    private final GameStateRepo gameStateRepo;
    private final DeckCardRepo deckCardRepo;
    private final ActionLogRepo actionLogRepo;
    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;
    private final BoardStateMapper boardStateMapper;
    private final TurnManager turnManager;
    private final RuleValidator ruleValidator;
    private final DamageCalculator damageCalculator;
    private final VictoryConditionChecker victoryChecker;

    // -------------------------------------------------------------------------
    // Game initialization
    // -------------------------------------------------------------------------

    /**
     * Called once both players are matched and have chosen decks.
     * Shuffles decks, deals 7 cards, places prizes, picks first-turn player by coin flip.
     */
    @Transactional
    public BoardState initializeGame(UUID matchId) {
        Match match = loadMatch(matchId);

        if (match.getPlayer1Deck() == null || match.getPlayer2Deck() == null) {
            throw new InvalidActionException("Ambos jugadores deben seleccionar un mazo antes de iniciar.");
        }

        Map<String, CardSnapshot> cache = new HashMap<>();

        PlayerBoard p1Board = boardStateMapper.buildInitialBoard(
                match.getPlayer1().getUsername(),
                deckCardRepo.findByDeckId(match.getPlayer1Deck().getId()),
                cache);

        PlayerBoard p2Board = boardStateMapper.buildInitialBoard(
                match.getPlayer2().getUsername(),
                deckCardRepo.findByDeckId(match.getPlayer2Deck().getId()),
                cache);

        // Coin flip decides who goes first
        String firstPlayer = new Random().nextBoolean()
                ? match.getPlayer1().getUsername()
                : match.getPlayer2().getUsername();

        BoardState state = BoardState.builder()
                .matchId(matchId.toString())
                .player1Id(match.getPlayer1().getUsername())
                .player2Id(match.getPlayer2().getUsername())
                .player1Board(p1Board)
                .player2Board(p2Board)
                .currentTurnPlayerId(firstPlayer)
                .firstTurnPlayerId(firstPlayer)
                .turnNumber(1)
                .turnPhase(TurnPhase.DRAW.name())
                .cardCache(cache)
                .build();

        // Update match entity
        match.setStatus(MatchStatus.SETUP);
        match.setStartedAt(LocalDateTime.now());
        matchRepo.save(match);

        persistState(match, state);
        logAction(match, null, ActionType.GAME_START, null, "Partida iniciada. Primer turno: " + firstPlayer);

        return state;
    }

    // -------------------------------------------------------------------------
    // Action dispatch
    // -------------------------------------------------------------------------

    @Transactional
    public BoardState performAction(UUID matchId, String playerId, GameActionRequest req) {
        Match match = loadMatch(matchId);
        BoardState state = loadState(match);

        validateTurn(state, playerId);

        switch (req.getType()) {
            case SETUP_ACTIVE  -> handleSetupActive(state, playerId, req);
            case SETUP_BENCH   -> handleSetupBench(state, playerId, req);
            case DRAW_CARD     -> handleDrawCard(state, playerId);
            case PLACE_BASIC   -> handlePlaceBasic(state, playerId, req);
            case ATTACH_ENERGY -> handleAttachEnergy(state, playerId, req);
            case EVOLVE        -> handleEvolve(state, playerId, req);
            case PLAY_ITEM     -> handlePlayItem(state, playerId, req);
            case PLAY_SUPPORTER-> handlePlaySupporter(state, playerId, req);
            case RETREAT       -> handleRetreat(state, playerId, req);
            case ATTACK        -> handleAttack(state, playerId, req, match);
            case END_TURN      -> handleEndTurn(state, playerId, match);
            default -> throw new InvalidActionException("Acción no soportada: " + req.getType());
        }

        checkAndFinalizeIfOver(state, match);
        persistState(match, state);
        logAction(match, playerId, req.getType(), null, req.getType().name());

        return state;
    }

    // -------------------------------------------------------------------------
    // State access
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public BoardState getGameState(UUID matchId) {
        Match match = loadMatch(matchId);
        return loadState(match);
    }

    // -------------------------------------------------------------------------
    // Setup phase handlers
    // -------------------------------------------------------------------------

    private void handleSetupActive(BoardState state, String playerId, GameActionRequest req) {
        PlayerBoard board = state.getBoardFor(playerId);
        if (board.getActivePokemon() != null) {
            throw new InvalidActionException("Ya colocaste tu Pokémon activo.");
        }
        CardInstance ci = requireFromHand(board, req.getCardInstanceId());
        CardSnapshot card = requireSnapshot(state, ci.getCardId());
        if (!card.isBasicPokemon()) throw new InvalidActionException("El Pokémon activo debe ser Básico.");

        board.getHand().remove(ci);
        PokemonInPlay pip = buildPokemonInPlay(ci, card, state.getTurnNumber());
        board.setActivePokemon(pip);
        board.setSetupComplete(true);

        checkBothSetupComplete(state);
    }

    private void handleSetupBench(BoardState state, String playerId, GameActionRequest req) {
        PlayerBoard board = state.getBoardFor(playerId);
        CardInstance ci = requireFromHand(board, req.getCardInstanceId());
        CardSnapshot card = requireSnapshot(state, ci.getCardId());
        if (!card.isBasicPokemon()) throw new InvalidActionException("Solo puedes colocar Pokémon Básicos en el banco durante la configuración.");
        if (!board.hasBenchSpace()) throw new InvalidActionException("El banco está lleno.");

        board.getHand().remove(ci);
        PokemonInPlay pip = buildPokemonInPlay(ci, card, state.getTurnNumber());
        board.getBench().add(pip);
    }

    private void checkBothSetupComplete(BoardState state) {
        boolean p1Done = state.getPlayer1Board().isSetupComplete();
        boolean p2Done = state.getPlayer2Board().isSetupComplete();
        if (p1Done && p2Done) {
            state.setTurnPhase(TurnPhase.DRAW.name());
        }
    }

    // -------------------------------------------------------------------------
    // Main-phase action handlers
    // -------------------------------------------------------------------------

    private void handleDrawCard(BoardState state, String playerId) {
        // Usually automatic at turn start, but exposed as an explicit action for the first draw
        String winner = victoryChecker.checkCannotDraw(state, playerId);
        if (winner != null) return; // game over handled outside

        PlayerBoard board = state.getBoardFor(playerId);
        board.drawCard();
        state.setTurnPhase(TurnPhase.MAIN.name());
        board.resetTurnFlags();
    }

    private void handlePlaceBasic(BoardState state, String playerId, GameActionRequest req) {
        PlayerBoard board = state.getBoardFor(playerId);
        CardInstance ci = requireFromHand(board, req.getCardInstanceId());
        CardSnapshot card = requireSnapshot(state, ci.getCardId());

        ruleValidator.validatePlaceBasic(state, playerId, card);
        board.getHand().remove(ci);
        PokemonInPlay pip = buildPokemonInPlay(ci, card, state.getTurnNumber());

        if (board.getActivePokemon() == null) {
            board.setActivePokemon(pip);
        } else {
            board.getBench().add(pip);
        }
    }

    private void handleAttachEnergy(BoardState state, String playerId, GameActionRequest req) {
        PlayerBoard board = state.getBoardFor(playerId);
        ruleValidator.validateAttachEnergy(state, playerId);

        CardInstance energyCi = requireFromHand(board, req.getCardInstanceId());
        CardSnapshot energyCard = requireSnapshot(state, energyCi.getCardId());
        if (!"ENERGY".equals(energyCard.getSupertype())) {
            throw new InvalidActionException("La carta seleccionada no es una Energía.");
        }

        PokemonInPlay target = requirePokemonInPlay(board, req.getTargetPokemonInstanceId());
        board.getHand().remove(energyCi);
        target.getAttachedEnergies().add(energyCi);
        board.setHasPlayedEnergy(true);
    }

    private void handleEvolve(BoardState state, String playerId, GameActionRequest req) {
        PlayerBoard board = state.getBoardFor(playerId);
        CardInstance evoCi = requireFromHand(board, req.getCardInstanceId());
        CardSnapshot evoCard = requireSnapshot(state, evoCi.getCardId());
        PokemonInPlay target = requirePokemonInPlay(board, req.getTargetPokemonInstanceId());

        ruleValidator.validateEvolve(state, playerId, evoCard, target);

        // Push current evolution instanceId onto the stack and replace with new card
        target.getEvolutionStack().add(target.getInstanceId());
        target.setInstanceId(evoCi.getInstanceId());
        target.setCardId(evoCi.getCardId());

        // Heal/reset HP to new card's HP (evolutions reset HP)
        int newMaxHp = evoCard.getHp() != null ? evoCard.getHp() : target.getMaxHp();
        target.setMaxHp(newMaxHp);
        target.setCurrentHp(newMaxHp);

        // Clear volatile conditions on evolution
        target.clearVolatileConditions();
        target.setBurned(false);
        target.setPoisoned(false);

        target.setTurnPlacedOrEvolved(state.getTurnNumber());
        board.getHand().remove(evoCi);
    }

    private void handlePlayItem(BoardState state, String playerId, GameActionRequest req) {
        PlayerBoard board = state.getBoardFor(playerId);
        CardInstance ci = requireFromHand(board, req.getCardInstanceId());
        CardSnapshot card = requireSnapshot(state, ci.getCardId());

        ruleValidator.validatePlayItem(state, playerId, card);
        board.getHand().remove(ci);
        board.getDiscard().add(ci);
        // Item effects are simplified — full simulation would require individual handlers
    }

    private void handlePlaySupporter(BoardState state, String playerId, GameActionRequest req) {
        PlayerBoard board = state.getBoardFor(playerId);
        CardInstance ci = requireFromHand(board, req.getCardInstanceId());
        CardSnapshot card = requireSnapshot(state, ci.getCardId());

        ruleValidator.validatePlaySupporter(state, playerId);
        List<String> subtypes = card.getSubtypes();
        if (subtypes == null || !subtypes.contains("Supporter")) {
            throw new InvalidActionException("Esa carta no es un Entrenador Seguidor.");
        }

        board.getHand().remove(ci);
        board.getDiscard().add(ci);
        board.setHasPlayedSupporter(true);
    }

    private void handleRetreat(BoardState state, String playerId, GameActionRequest req) {
        PlayerBoard board = state.getBoardFor(playerId);
        PokemonInPlay newActive = requirePokemonInPlay(board, req.getNewActivePokemonInstanceId());

        ruleValidator.validateRetreat(state, playerId, newActive);

        PokemonInPlay oldActive = board.getActivePokemon();
        CardSnapshot oldCard = state.getCardCache().get(oldActive.getCardId());
        int cost = oldCard != null && oldCard.getRetreatCost() != null ? oldCard.getRetreatCost() : 0;

        // Discard retreat-cost energies (discard the required number, starting from the attached list)
        for (int i = 0; i < cost && !oldActive.getAttachedEnergies().isEmpty(); i++) {
            CardInstance discarded = oldActive.getAttachedEnergies().remove(0);
            board.getDiscard().add(discarded);
        }

        // Swap active with bench pokemon
        board.getBench().remove(newActive);
        board.getBench().add(oldActive);
        board.setActivePokemon(newActive);

        // Clear volatile conditions after retreating
        newActive.clearVolatileConditions();
        board.setHasRetreated(true);
    }

    private void handleAttack(BoardState state, String playerId, GameActionRequest req, Match match) {
        // Transition to attack phase if still in MAIN
        if (TurnPhase.MAIN.name().equals(state.getTurnPhase())) {
            turnManager.enterAttackPhase(state);
        }

        ruleValidator.validateAttack(state, playerId, req.getAttackIndex());

        PlayerBoard attackerBoard = state.getBoardFor(playerId);
        PlayerBoard defenderBoard = state.getOpponentBoard(playerId);

        PokemonInPlay attacker = attackerBoard.getActivePokemon();
        PokemonInPlay defender = defenderBoard.getActivePokemon();
        CardSnapshot attackerCard = state.getCardCache().get(attacker.getCardId());
        AttackSnapshot attack = attackerCard.getAttacks().get(req.getAttackIndex());

        AttackContext ctx = damageCalculator.calculate(state, playerId, attack, attacker, defender);

        if (ctx.isDefenderKnockedOut()) {
            processKnockout(state, defenderBoard, defender, attackerBoard, match);
        }

        attackerBoard.setHasAttacked(true);
    }

    private void processKnockout(BoardState state, PlayerBoard defenderBoard,
                                  PokemonInPlay fainted, PlayerBoard attackerBoard, Match match) {
        // Move the KO'd Pokémon and its attached cards to discard
        List<CardInstance> toDiscard = new ArrayList<>(fainted.getAttachedEnergies());
        if (fainted.getAttachedTool() != null) toDiscard.add(fainted.getAttachedTool());

        // Add the Pokémon card itself and all evolution cards under it
        // (simplified: just discard the top instanceId as a CardInstance with the cardId)
        CardInstance faintedCi = CardInstance.builder()
                .instanceId(fainted.getInstanceId())
                .cardId(fainted.getCardId())
                .build();
        toDiscard.add(faintedCi);
        defenderBoard.getDiscard().addAll(toDiscard);
        defenderBoard.setActivePokemon(null);

        // The attacking player takes a prize card
        CardSnapshot faintedCard = state.getCardCache().get(fainted.getCardId());
        int prizesToTake = (faintedCard != null && isExPokemon(faintedCard)) ? 2 : 1;
        for (int i = 0; i < prizesToTake; i++) {
            attackerBoard.takePrize();
        }

        logAction(match, state.getCurrentTurnPlayerId(), ActionType.KNOCKOUT,
                null, fainted.getCardId() + " fue derrotado.");

        // If bench has pokemon, it auto-promotes; otherwise game-over checked next
        if (!defenderBoard.getBench().isEmpty()) {
            // Defender will need to choose which bench pokemon to promote
            // For simplicity, auto-promote the first bench pokemon
            PokemonInPlay promoted = defenderBoard.getBench().remove(0);
            promoted.clearVolatileConditions();
            defenderBoard.setActivePokemon(promoted);
        }

        victoryChecker.checkAfterAction(state);
    }

    private void handleEndTurn(BoardState state, String playerId, Match match) {
        ruleValidator.validateEndTurn(state, playerId);
        String winner = turnManager.endTurn(state);
        if (winner != null) {
            state.setWinnerId(winner);
        } else {
            // Auto-begin next player's draw
            turnManager.beginTurn(state);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void validateTurn(BoardState state, String playerId) {
        if (!playerId.equals(state.getCurrentTurnPlayerId())) {
            throw new InvalidActionException("No es tu turno.");
        }
        if (state.isGameOver()) {
            throw new InvalidActionException("La partida ya terminó.");
        }
    }

    private CardInstance requireFromHand(PlayerBoard board, String instanceId) {
        CardInstance ci = board.findInHand(instanceId);
        if (ci == null) throw new InvalidActionException("Carta no encontrada en la mano: " + instanceId);
        return ci;
    }

    private CardSnapshot requireSnapshot(BoardState state, String cardId) {
        CardSnapshot cs = state.getCardCache().get(cardId);
        if (cs == null) throw new InvalidActionException("Datos de carta no encontrados: " + cardId);
        return cs;
    }

    private PokemonInPlay requirePokemonInPlay(PlayerBoard board, String instanceId) {
        PokemonInPlay pip = board.findPokemonInPlay(instanceId);
        if (pip == null) throw new InvalidActionException("Pokémon en juego no encontrado: " + instanceId);
        return pip;
    }

    private PokemonInPlay buildPokemonInPlay(CardInstance ci, CardSnapshot card, int turn) {
        int hp = card.getHp() != null ? card.getHp() : 0;
        return PokemonInPlay.builder()
                .instanceId(ci.getInstanceId())
                .cardId(ci.getCardId())
                .currentHp(hp)
                .maxHp(hp)
                .turnPlacedOrEvolved(turn)
                .build();
    }

    private boolean isExPokemon(CardSnapshot card) {
        return card.getSubtypes() != null &&
               (card.getSubtypes().contains("EX") || card.getSubtypes().contains("GX"));
    }

    private void checkAndFinalizeIfOver(BoardState state, Match match) {
        if (state.isGameOver()) {
            match.setStatus(MatchStatus.FINISHED);
            match.setFinishedAt(LocalDateTime.now());
            User winner = userRepo.findByUsername(state.getWinnerId()).orElse(null);
            match.setWinner(winner);
            matchRepo.save(match);
            logAction(match, state.getWinnerId(), ActionType.GAME_END, null,
                    "Partida terminada. Ganador: " + state.getWinnerId());
        }
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    private Match loadMatch(UUID matchId) {
        return matchRepo.findById(matchId)
                .orElseThrow(() -> new GameNotFoundException(matchId.toString()));
    }

    private BoardState loadState(Match match) {
        GameState gs = gameStateRepo.findByMatchId(match.getId())
                .orElseThrow(() -> new GameNotFoundException(match.getId().toString()));
        try {
            return objectMapper.readValue(gs.getBoardStateJson(), BoardState.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al deserializar el estado del tablero", e);
        }
    }

    private void persistState(Match match, BoardState state) {
        try {
            String json = objectMapper.writeValueAsString(state);
            GameState gs = gameStateRepo.findByMatchId(match.getId())
                    .orElseGet(() -> GameState.builder().match(match).build());
            gs.setBoardStateJson(json);
            gameStateRepo.save(gs);

            // Keep match entity in sync for auditing
            match.setTurnNumber(state.getTurnNumber());
            matchRepo.save(match);
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar el estado del tablero", e);
        }
    }

    private void logAction(Match match, String playerId, ActionType type, String detailsJson, String message) {
        try {
            User player = playerId != null ? userRepo.findByUsername(playerId).orElse(null) : null;
            ActionLog log = ActionLog.builder()
                    .match(match)
                    .player(player)
                    .turnNumber(match.getTurnNumber())
                    .actionType(type)
                    .detailsJson(detailsJson)
                    .resultMessage(message != null && message.length() > 500
                            ? message.substring(0, 500) : message)
                    .build();
            actionLogRepo.save(log);
        } catch (Exception e) {
            log.warn("Failed to log action: {}", e.getMessage());
        }
    }
}
