package com.pokemon.tcg.common;

public record ErrorResponse (
    String code,
    String message,
    long timestamp
) {}
