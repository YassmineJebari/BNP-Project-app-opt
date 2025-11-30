package com.recipes.recipe_backend.gestion_recettes.repository;

import com.recipes.recipe_backend.gestion_recettes.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    
    List<RecipeIngredient> findByRecipeId(Long recipeId);
    
    List<RecipeIngredient> findByIngredientId(Long ingredientId);
    
    void deleteByRecipeId(Long recipeId);
}