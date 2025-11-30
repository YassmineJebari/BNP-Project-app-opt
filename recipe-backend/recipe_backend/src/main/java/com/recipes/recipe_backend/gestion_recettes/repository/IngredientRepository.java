package com.recipes.recipe_backend.gestion_recettes.repository;

import com.recipes.recipe_backend.gestion_recettes.entity.Ingredient;
import com.recipes.recipe_backend.gestion_recettes.entity.IngredientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
    Optional<Ingredient> findByName(String name);
    
    List<Ingredient> findByIngredientType(IngredientType ingredientType);
    
    boolean existsByName(String name);
    
    List<Ingredient> findByNameContainingIgnoreCase(String name);
}