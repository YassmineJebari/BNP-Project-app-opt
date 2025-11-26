package com.recipes.recipe_backend.gestion_recettes.service;

import com.recipes.recipe_backend.dto.FavoriteDTO;
import com.recipes.recipe_backend.gestion_recettes.entity.Favorite;
import com.recipes.recipe_backend.gestion_recettes.entity.Recipe;
import com.recipes.recipe_backend.gestion_recettes.repository.FavoriteRepository;
import com.recipes.recipe_backend.gestion_recettes.repository.RecipeRepository;
import com.recipes.recipe_backend.gestion_utilisateur.entity.User;
import com.recipes.recipe_backend.gestion_utilisateur.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    public List<FavoriteDTO> getUserFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        return favoriteRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public boolean isFavorite(String username, Long recipeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        return favoriteRepository.existsByUserIdAndRecipeId(user.getId(), recipeId);
    }
    
    public FavoriteDTO addFavorite(String username, Long recipeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recette non trouvée"));
        
        // Vérifier si déjà en favoris
        if (favoriteRepository.existsByUserIdAndRecipeId(user.getId(), recipeId)) {
            throw new RuntimeException("Cette recette est déjà dans vos favoris");
        }
        
        // Créer le favori
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setRecipe(recipe);
        
        Favorite saved = favoriteRepository.save(favorite);
        
        // Incrémenter le compteur de favoris de la recette
        recipe.setFavoritesCount(recipe.getFavoritesCount() + 1);
        recipeRepository.save(recipe);
        
        return convertToDTO(saved);
    }
    
    public void removeFavorite(String username, Long recipeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recette non trouvée"));
        
        if (!favoriteRepository.existsByUserIdAndRecipeId(user.getId(), recipeId)) {
            throw new RuntimeException("Cette recette n'est pas dans vos favoris");
        }
        
        favoriteRepository.deleteByUserIdAndRecipeId(user.getId(), recipeId);
        
        // Décrémenter le compteur de favoris de la recette
        recipe.setFavoritesCount(Math.max(0, recipe.getFavoritesCount() - 1));
        recipeRepository.save(recipe);
    }
    
    private FavoriteDTO convertToDTO(Favorite favorite) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(favorite.getId());
        dto.setUserId(favorite.getUser().getId());
        dto.setUsername(favorite.getUser().getUsername());
        dto.setRecipeId(favorite.getRecipe().getId());
        dto.setRecipeTitle(favorite.getRecipe().getTitle());
        dto.setCreatedAt(favorite.getCreatedAt());
        return dto;
    }
}