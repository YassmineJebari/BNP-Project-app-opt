package com.recipes.recipe_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private UserDTO user;   // <-- on renvoie un UserDTO complet

    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}