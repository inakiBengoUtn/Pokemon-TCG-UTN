package com.pokemon.tcg.modules.user.exceptions;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RuntimeException{
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public UserAlreadyExistsException() {
        super("This user is already registered, please try another username");
    }
}
