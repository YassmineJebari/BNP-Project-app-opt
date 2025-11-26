package com.recipes.recipe_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecipeRequest {
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String title;
    
    @NotBlank(message = "La description est obligatoire")
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    @NotNull(message = "Le temps de préparation est obligatoire")
    @Positive(message = "Le temps de préparation doit être positif")
    private Integer preparationTime;
    
    @NotNull(message = "Le temps de cuisson est obligatoire")
    @Positive(message = "Le temps de cuisson doit être positif")
    private Integer cookingTime;
    
    @NotBlank(message = "La difficulté est obligatoire")
    private String difficulty; // FACILE, MOYEN, DIFFICILE
    
    private String imageUrl;
    
    @NotBlank(message = "Les étapes sont obligatoires")
    private String steps;
    
    @Positive(message = "Le nombre de portions doit être positif")
    private Integer servings;
    
    private Long categoryId;
    
    @NotNull(message = "Les ingrédients sont obligatoires")
    @Size(min = 1, message = "Au moins un ingrédient est requis")
    private List<RecipeIngredientDTO> ingredients;
}