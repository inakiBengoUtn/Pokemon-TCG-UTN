package com.pokemon.tcg.modules.user.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadCredentialsException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private String codeError = "BAD_CREDENTIALS";

    public BadCredentialsException() {
        super("The credentials are incorrect.");
    }
}

