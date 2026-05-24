package com.pokemon.tcg.modules.catalog.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiDataException extends RuntimeException{
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private String codeError = "API_DATA_ERROR";

    public ApiDataException() {
        super("The API data could not be obtained");
    }
}
