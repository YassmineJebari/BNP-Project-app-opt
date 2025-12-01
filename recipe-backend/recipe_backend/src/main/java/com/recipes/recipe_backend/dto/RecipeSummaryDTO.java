package com.recipes.recipe_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Pour les listes (données minimales)
public class RecipeSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Integer servings;
    private Integer preparationTime;
    private Integer cookingTime;
    private String categoryName;
    private Integer viewsCount;
    private Integer favoritesCount;

    // Ingrédients
    private List<RecipeIngredientDTO> ingredients;
    
    // Dates
    private LocalDateTime createdAt;
}


/*
public class RecipeDTO {
    private Long id;
    private String title;
    private String description;
    private Integer preparationTime;
    private Integer cookingTime;
    private String difficulty;
    private String imageUrl;
    private String steps;
    private Integer servings;
    private Integer viewsCount;
    private Integer favoritesCount;
    
    // Informations de l'auteur
    private Long userId;
    private String username;
    
    // Catégorie
    private Long categoryId;
    private String categoryName;
    
    // Ingrédients
    private List<RecipeIngredientDTO> ingredients;
    
    // Dates
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}*/