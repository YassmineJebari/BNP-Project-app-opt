package com.recipes.recipe_backend.gestion_recettes.repository;

import com.recipes.recipe_backend.gestion_recettes.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    List<Favorite> findByUserId(Long userId);
    
    List<Favorite> findByRecipeId(Long recipeId);
    
    Optional<Favorite> findByUserIdAndRecipeId(Long userId, Long recipeId);
    
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
    
    long countByRecipeId(Long recipeId);
}