package com.pokemon.tcg.modules.user.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserRequest {
    @NotBlank
    private String password;
    @NotBlank
    private String username;
}
