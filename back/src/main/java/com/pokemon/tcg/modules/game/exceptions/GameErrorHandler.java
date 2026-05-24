package com.pokemon.tcg.modules.game.exceptions;

import com.pokemon.tcg.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice()
@Order(3)
public class GameErrorHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(GameNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getCodeError(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(ex.getHttpStatus())
                .body(errorResponse);
    }
}
