package com.recipes.recipe_backend.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecipeRequest {
    
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String title;
    
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    @Positive(message = "Le temps de préparation doit être positif")
    private Integer preparationTime;
    
    @Positive(message = "Le temps de cuisson doit être positif")
    private Integer cookingTime;
    
    private String difficulty;
    
    private String imageUrl;
    
    private String steps;
    
    @Positive(message = "Le nombre de portions doit être positif")
    private Integer servings;
    
    private Long categoryId;
    
    private List<RecipeIngredientDTO> ingredients;
}