package com.pokemon.tcg.modules.user.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserAlreadyExistsException extends RuntimeException{
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private String codeError = "USER_TAKEN";

    public UserAlreadyExistsException() {
        super("This user is already registered, please try another username");
    }
}
