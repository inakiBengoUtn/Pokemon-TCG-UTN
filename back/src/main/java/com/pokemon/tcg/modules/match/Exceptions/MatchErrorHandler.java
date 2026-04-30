package com.pokemon.tcg.modules.match.Exceptions;

import com.pokemon.tcg.common.ErrorResponse;
import com.pokemon.tcg.modules.user.exceptions.BadCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice()
@Order(1)
public class MatchErrorHandler {

    @ExceptionHandler(UnauthenticatedUserException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(UnauthenticatedUserException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getCodeError(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(ex.getHttpStatus())
                .body(errorResponse);
    }
}
