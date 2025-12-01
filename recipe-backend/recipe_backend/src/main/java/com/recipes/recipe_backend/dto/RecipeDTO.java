package com.recipes.recipe_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
// Pour les détails complets (toutes les données)
public class RecipeDTO extends RecipeSummaryDTO {
    private String steps;
    private String difficulty;

    // Informations de l'auteur
    private Long userId;
    private String username;

    // Catégorie
    private Long categoryId;

    // Dates
    private LocalDateTime updatedAt;
}
