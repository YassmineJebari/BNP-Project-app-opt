package com.recipes.recipe_backend.gestion_recettes.entity;

public enum Difficulty {
    FACILE("Facile"),
    MOYEN("Moyen"),
    DIFFICILE("Difficile");
    
    private final String displayName;
    
    Difficulty(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}