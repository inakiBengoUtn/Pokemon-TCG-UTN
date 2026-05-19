package com.pokemon.tcg.modules.game.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class GameNotFoundException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    private String codeError = "GAME_NOT_FOUND";

    public GameNotFoundException(String id) {
        super("The game with the id "+id+" was not found");
    }
}
