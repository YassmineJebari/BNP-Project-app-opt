package com.recipes.recipe_backend.gestion_recettes.entity;

public enum IngredientType {
    LEGUME("Légume"),
    FRUIT("Fruit"),
    VIANDE("Viande"),
    POISSON("Poisson"),
    PRODUIT_LAITIER("Produit laitier"),
    CEREALE("Céréale"),
    EPICE("Épice"),
    CONDIMENT("Condiment"),
    HUILE("Huile"),
    BOISSON("Boisson"),
    AUTRE("Autre");
    
    private final String displayName;
    
    IngredientType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}