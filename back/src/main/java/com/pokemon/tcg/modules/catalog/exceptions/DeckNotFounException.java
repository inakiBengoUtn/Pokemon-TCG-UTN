package com.pokemon.tcg.modules.catalog.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
@Setter
public class DeckNotFounException extends RuntimeException{
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    private String codeError = "DECK_NOT_FOUND";

    public DeckNotFounException(UUID id) {
        super("The deck whit id "+id+" not found.");
    }
}
