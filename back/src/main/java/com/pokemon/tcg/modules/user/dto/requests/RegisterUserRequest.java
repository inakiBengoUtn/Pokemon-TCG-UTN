package com.pokemon.tcg.modules.user.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequest {
    private String username;
    private String password;
}
