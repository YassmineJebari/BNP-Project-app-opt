package com.recipes.recipe_backend.gestion_recettes.repository;

import com.recipes.recipe_backend.gestion_recettes.entity.Difficulty;
import com.recipes.recipe_backend.gestion_recettes.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    // Recherche par utilisateur
    List<Recipe> findByUserId(Long userId);
    
    // Recherche par catégorie
    List<Recipe> findByCategoryId(Long categoryId);
    
    // Recherche par difficulté
    List<Recipe> findByDifficulty(Difficulty difficulty);
    
    // Recherche par titre (contient)
    List<Recipe> findByTitleContainingIgnoreCase(String title);
    
    // Recherche par description (contient)
    List<Recipe> findByDescriptionContainingIgnoreCase(String description);
    
    // Recherche par titre ou description
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recipe> searchByKeyword(@Param("keyword") String keyword);
    
    // Recettes les plus vues
    List<Recipe> findTop10ByOrderByViewsCountDesc();
    
    // Recettes les plus aimées
    List<Recipe> findTop10ByOrderByFavoritesCountDesc();
    
    // Recettes récentes
    List<Recipe> findTop10ByOrderByCreatedAtDesc();
    
    // Temps de préparation total inférieur à X minutes
    @Query("SELECT r FROM Recipe r WHERE (r.preparationTime + r.cookingTime) <= :maxTime")
    List<Recipe> findByMaxTotalTime(@Param("maxTime") Integer maxTime);
}