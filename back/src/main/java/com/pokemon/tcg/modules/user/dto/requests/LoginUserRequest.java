package com.pokemon.tcg.modules.user.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserRequest {
    private String password;
    private String username;
}
