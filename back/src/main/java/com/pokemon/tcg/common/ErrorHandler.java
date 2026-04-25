package com.pokemon.tcg.common;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;

@Slf4j // <- esto se usa para crear logs y poder debugear mas facilmente
@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    // captura errores de DTOs
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers,
                                                               HttpStatusCode status,
                                                               WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "The data entered is incorrect.");
        problemDetail.setTitle("Validation Error");

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        problemDetail.setProperty("errors", errors);

        return createResponseEntity(problemDetail, headers, status, request);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<Object> handleJWTVerificationException(JWTVerificationException ex,
                                                                 WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NON_AUTHORITATIVE_INFORMATION, "The token is invalid or has expired.");
        problemDetail.setTitle("INVALID_JWT");

        return createResponseEntity(problemDetail, headers, HttpStatus.NON_AUTHORITATIVE_INFORMATION, request);
    }

    // Captura todos los errores para que no le llegue al usuario
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleExceptions(Exception ex,
                                                   WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred on the server side, please try again later");
        URI uri = URI.create(request.getDescription(false));
        problemDetail.setTitle("INTERNAL_SERVER_ERROR");
        problemDetail.setInstance(uri);

        log.error("Internal Server Error  - URI: {} - Message: {}",
                uri, ex.getMessage(), ex);

        return createResponseEntity(problemDetail, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
