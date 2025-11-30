package com.recipes.recipe_backend.gestion_recettes.controller;

import com.recipes.recipe_backend.dto.CreateRecipeRequest;
import com.recipes.recipe_backend.dto.RecipeDTO;
import com.recipes.recipe_backend.dto.UpdateRecipeRequest;
import com.recipes.recipe_backend.gestion_recettes.service.RecipeService;
import com.recipes.recipe_backend.gestion_utilisateur.entity.User;
import com.recipes.recipe_backend.gestion_utilisateur.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "http://localhost:4200")
public class RecipeController {
    
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserRepository userRepository;
    
    // GET : Toutes les recettes (accessible à tous)
    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }
    
    // GET : Une recette par ID
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }
    
    // GET : Recettes d'un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDTO>> getRecipesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(recipeService.getRecipesByUserId(userId));
    }
    
    // GET : Mes recettes (utilisateur connecté)
    @GetMapping("/my-recipes")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<RecipeDTO>> getMyRecipes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Get-my-recipe : Utilisateur non trouvé"));
        
        // Récupérer l'ID de l'utilisateur via le username
        // Pour simplifier, on peut utiliser directement le service
        return ResponseEntity.ok(recipeService.getRecipesByUserId(user.getId()));
    }
    
    // GET : Recettes par catégorie
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<RecipeDTO>> getRecipesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(recipeService.getRecipesByCategoryId(categoryId));
    }
    
    // GET : Recettes par difficulté
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<RecipeDTO>> getRecipesByDifficulty(@PathVariable String difficulty) {
        try {
            return ResponseEntity.ok(recipeService.getRecipesByDifficulty(difficulty));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // GET : Recherche de recettes
    @GetMapping("/search")
    public ResponseEntity<List<RecipeDTO>> searchRecipes(@RequestParam String keyword) {
        return ResponseEntity.ok(recipeService.searchRecipes(keyword));
    }
    
    // GET : Recettes populaires
    @GetMapping("/popular")
    public ResponseEntity<List<RecipeDTO>> getPopularRecipes() {
        return ResponseEntity.ok(recipeService.getPopularRecipes());
    }
    
    // GET : Recettes les plus aimées
    @GetMapping("/most-favorited")
    public ResponseEntity<List<RecipeDTO>> getMostFavoritedRecipes() {
        return ResponseEntity.ok(recipeService.getMostFavoritedRecipes());
    }
    
    // GET : Recettes récentes
    @GetMapping("/recent")
    public ResponseEntity<List<RecipeDTO>> getRecentRecipes() {
        return ResponseEntity.ok(recipeService.getRecentRecipes());
    }
    
    // GET : Recettes rapides (temps total <= maxTime)
    @GetMapping("/quick")
    public ResponseEntity<List<RecipeDTO>> getQuickRecipes(@RequestParam Integer maxTime) {
        return ResponseEntity.ok(recipeService.getQuickRecipes(maxTime));
    }
    
    // POST : Créer une recette (utilisateur connecté)
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> createRecipe(@Valid @RequestBody CreateRecipeRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            RecipeDTO created = recipeService.createRecipe(request, email);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // PUT : Mettre à jour une recette (auteur uniquement)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateRecipe(@PathVariable Long id, @Valid @RequestBody UpdateRecipeRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            RecipeDTO updated = recipeService.updateRecipe(id, request, email);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // PUT : Mettre à jour n'importe quelle recette (admin uniquement)
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRecipeByAdmin(@PathVariable Long id,
                                                @Valid @RequestBody UpdateRecipeRequest request) {
        try {
            RecipeDTO updated = recipeService.updateRecipeByAdmin(id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE : Supprimer une recette (auteur uniquement)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            recipeService.deleteRecipe(id, username);
            return ResponseEntity.ok("Recette supprimée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // DELETE : Supprimer n'importe quelle recette (admin uniquement)
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRecipeByAdmin(@PathVariable Long id) {
        try {
            recipeService.deleteRecipeByAdmin(id);
            return ResponseEntity.ok("Recette supprimée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API Recipes fonctionne !");
    }
}