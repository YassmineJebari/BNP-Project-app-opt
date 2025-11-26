package com.recipes.recipe_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientDTO {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private Double quantity;
    private String unit;
}