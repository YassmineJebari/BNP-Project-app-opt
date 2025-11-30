package com.recipes.recipe_backend.gestion_recettes.controller;

import com.recipes.recipe_backend.dto.FavoriteDTO;
import com.recipes.recipe_backend.gestion_recettes.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    // GET : Récupérer tous les favoris de l'utilisateur connecté
    @GetMapping("/my-favorites")
    public ResponseEntity<List<FavoriteDTO>> getMyFavorites() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(favoriteService.getUserFavorites(username));
    }
    
    // GET : Vérifier si une recette est en favoris
    @GetMapping("/check/{recipeId}")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long recipeId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(favoriteService.isFavorite(username, recipeId));
    }
    
    // POST : Ajouter une recette aux favoris
    @PostMapping("/{recipeId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long recipeId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            FavoriteDTO favorite = favoriteService.addFavorite(username, recipeId);
            return ResponseEntity.ok(favorite);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // DELETE : Retirer une recette des favoris
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long recipeId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            favoriteService.removeFavorite(username, recipeId);
            return ResponseEntity.ok("Recette retirée des favoris");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}