package com.pokemon.tcg.modules.match.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthenticatedUserException extends RuntimeException{
    private HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
    private String codeError = "NOT_AUTHENTICATED";

    public UnauthenticatedUserException() {
        super("The user is not authenticated.");
    }
}
