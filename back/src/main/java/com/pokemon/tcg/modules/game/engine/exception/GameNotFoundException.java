package com.pokemon.tcg.modules.game.engine.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String matchId) {
        super("Partida no encontrada: " + matchId);
    }
}
