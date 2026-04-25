package com.pokemon.tcg.modules.user.exceptions;

import com.pokemon.tcg.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice()
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserErrorHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getCodeError(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(ex.getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getCodeError(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(ex.getHttpStatus())
                .body(errorResponse);
    }
}
