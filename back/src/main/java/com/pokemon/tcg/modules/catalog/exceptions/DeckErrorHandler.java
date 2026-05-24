package com.pokemon.tcg.modules.catalog.exceptions;

import com.pokemon.tcg.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice()
@Order(2)
public class DeckErrorHandler {

    @ExceptionHandler(DeckNotFounException.class)
    public ResponseEntity<ErrorResponse> handleDeckNotFound(DeckNotFounException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getCodeError(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(ex.getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(ApiDataException.class)
    public ResponseEntity<ErrorResponse> handleApiData(ApiDataException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getCodeError(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(ex.getHttpStatus())
                .body(errorResponse);
    }
}
