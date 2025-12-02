package com.recipes.recipe_backend.gestion_recettes.service;

import com.recipes.recipe_backend.dto.CreateRecipeRequest;
import com.recipes.recipe_backend.dto.RecipeDTO;
import com.recipes.recipe_backend.dto.RecipeIngredientDTO;
import com.recipes.recipe_backend.dto.RecipeSummaryDTO;
import com.recipes.recipe_backend.dto.UpdateRecipeRequest;
import com.recipes.recipe_backend.gestion_recettes.entity.*;
import com.recipes.recipe_backend.gestion_recettes.repository.*;
import com.recipes.recipe_backend.gestion_utilisateur.entity.User;
import com.recipes.recipe_backend.gestion_utilisateur.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeService {
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    
    // 1. Optimiser getAllRecipes()
    public List<RecipeSummaryDTO> getAllRecipes() {
        return recipeRepository.findAllWithDetails().stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }
    
    
    // ✅ CACHE : Met en cache le résultat
    @Cacheable(value = "recipes", key = "#id")
    // 2. Optimiser getRecipeById()
    public RecipeDTO getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Recette non trouvée"));
        
        // Incrémenter le nombre de vues
        recipe.setViewsCount(recipe.getViewsCount() + 1);
        recipeRepository.save(recipe);
        
        return convertToDTO(recipe);
    }
    
    public List<RecipeDTO> getRecipesByUserId(Long userId) {
        return recipeRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RecipeDTO> getRecipesByCategoryId(Long categoryId) {
        return recipeRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RecipeDTO> getRecipesByDifficulty(String difficulty) {
        Difficulty diff = Difficulty.valueOf(difficulty.toUpperCase());
        return recipeRepository.findByDifficulty(diff).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RecipeDTO> searchRecipes(String keyword) {
        return recipeRepository.searchByKeyword(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RecipeDTO> getPopularRecipes() {
        return recipeRepository.findTop10ByOrderByViewsCountDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RecipeDTO> getMostFavoritedRecipes() {
        return recipeRepository.findTop10ByOrderByFavoritesCountDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RecipeDTO> getRecentRecipes() {
        return recipeRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RecipeDTO> getQuickRecipes(Integer maxTime) {
        return recipeRepository.findByMaxTotalTime(maxTime).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public RecipeDTO createRecipe(CreateRecipeRequest request, String email) {
        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Créer la recette
        Recipe recipe = new Recipe();
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setPreparationTime(request.getPreparationTime());
        recipe.setCookingTime(request.getCookingTime());
        recipe.setDifficulty(Difficulty.valueOf(request.getDifficulty().toUpperCase()));
        recipe.setImageUrl(request.getImageUrl());
        recipe.setSteps(request.getSteps());
        recipe.setUser(user);
        
        // Associer la catégorie si fournie
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            recipe.setCategory(category);
        }
        
        // Sauvegarder la recette
        Recipe savedRecipe = recipeRepository.save(recipe);
        
        // Ajouter les ingrédients
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            for (RecipeIngredientDTO ingredientDTO : request.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(ingredientDTO.getIngredientId())
                        .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé: " + ingredientDTO.getIngredientId()));
                
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setRecipe(savedRecipe);
                recipeIngredient.setIngredient(ingredient);
                recipeIngredient.setQuantity(ingredientDTO.getQuantity());
                recipeIngredient.setUnit(ingredientDTO.getUnit());
                
                recipeIngredientRepository.save(recipeIngredient);
            }
        }
        
        return convertToDTO(savedRecipe);
    }
    
    // ✅ CACHE : Invalide le cache quand on modifie
    @CacheEvict(value = "recipes", key = "#id")
    public RecipeDTO updateRecipe(Long id, UpdateRecipeRequest request, String email) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recette non trouvée"));
        
        // Vérifier que l'utilisateur est l'auteur
        if (!recipe.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cette recette");
        }
        
        // Mettre à jour les champs
        if (request.getTitle() != null) recipe.setTitle(request.getTitle());
        if (request.getDescription() != null) recipe.setDescription(request.getDescription());
        if (request.getPreparationTime() != null) recipe.setPreparationTime(request.getPreparationTime());
        if (request.getCookingTime() != null) recipe.setCookingTime(request.getCookingTime());
        if (request.getDifficulty() != null) {
            recipe.setDifficulty(Difficulty.valueOf(request.getDifficulty().toUpperCase()));
        }
        if (request.getImageUrl() != null) recipe.setImageUrl(request.getImageUrl());
        if (request.getSteps() != null) recipe.setSteps(request.getSteps());
        if (request.getServings() != null) recipe.setServings(request.getServings());
        
        // Mettre à jour la catégorie
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            recipe.setCategory(category);
        }
        
        // Mettre à jour les ingrédients si fournis
        if (request.getIngredients() != null) {
            // Supprimer les anciens ingrédients
            recipeIngredientRepository.deleteByRecipeId(id);
            
            // Ajouter les nouveaux
            for (RecipeIngredientDTO ingredientDTO : request.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(ingredientDTO.getIngredientId())
                        .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));
                
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setRecipe(recipe);
                recipeIngredient.setIngredient(ingredient);
                recipeIngredient.setQuantity(ingredientDTO.getQuantity());
                recipeIngredient.setUnit(ingredientDTO.getUnit());
                
                recipeIngredientRepository.save(recipeIngredient);
            }
        }
        
        Recipe updated = recipeRepository.save(recipe);
        return convertToDTO(updated);
    }
    
    // ✅ CACHE : Invalide le cache quand l'admin modifie
    @CacheEvict(value = "recipes", key = "#id")
    public RecipeDTO updateRecipeByAdmin(Long id, UpdateRecipeRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recette non trouvée"));
        
        // Mettre à jour les champs
        if (request.getTitle() != null) {
            recipe.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            recipe.setDescription(request.getDescription());
        }
        if (request.getPreparationTime() != null) {
            recipe.setPreparationTime(request.getPreparationTime());
        }
        if (request.getCookingTime() != null) {
            recipe.setCookingTime(request.getCookingTime());
        }
        if (request.getDifficulty() != null) {
            recipe.setDifficulty(Difficulty.valueOf(request.getDifficulty().toUpperCase()));
        }
        if (request.getImageUrl() != null) {
            recipe.setImageUrl(request.getImageUrl());
        }
        if (request.getSteps() != null) {
            recipe.setSteps(request.getSteps());
        }
        if (request.getServings() != null) {
            recipe.setServings(request.getServings());
        }

        // Mettre à jour la catégorie
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            recipe.setCategory(category);
        }

        // Mettre à jour les ingrédients si fournis
        if (request.getIngredients() != null) {
            recipeIngredientRepository.deleteByRecipeId(id);

            for (RecipeIngredientDTO ingredientDTO : request.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(ingredientDTO.getIngredientId())
                        .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));

                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setRecipe(recipe);
                recipeIngredient.setIngredient(ingredient);
                recipeIngredient.setQuantity(ingredientDTO.getQuantity());
                recipeIngredient.setUnit(ingredientDTO.getUnit());

                recipeIngredientRepository.save(recipeIngredient);
            }
        }

        Recipe updated = recipeRepository.save(recipe);
        return convertToDTO(updated);
    }
    
    // ✅ CACHE : Invalide le cache quand on supprime
    @CacheEvict(value = "recipes", key = "#id")
    public void deleteRecipe(Long id, String username) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recette non trouvée"));
        
        // Vérifier que l'utilisateur est l'auteur
        if (!recipe.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cette recette");
        }
        
        recipeRepository.deleteById(id);
    }
    
    // ✅ CACHE : Invalide le cache quand l'admin supprime
    @CacheEvict(value = "recipes", key = "#id")
    public void deleteRecipeByAdmin(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recette non trouvée"));
        
        recipeRepository.deleteById(id);
    }

    // ===================== PAGINATION =====================

    public Page<RecipeDTO> getAllRecipesPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return recipeRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    // 3. Simplifier convertToDTO (plus besoin de charger les ingrédients)
    private RecipeDTO convertToDTO(Recipe recipe) {
        RecipeDTO dto = new RecipeDTO();
        dto.setId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setPreparationTime(recipe.getPreparationTime());
        dto.setCookingTime(recipe.getCookingTime());
        dto.setDifficulty(recipe.getDifficulty().name());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setSteps(recipe.getSteps());
        dto.setServings(recipe.getServings());
        dto.setViewsCount(recipe.getViewsCount());
        dto.setFavoritesCount(recipe.getFavoritesCount());
        dto.setCreatedAt(recipe.getCreatedAt());
        dto.setUpdatedAt(recipe.getUpdatedAt());
        
        // Informations utilisateur (déjà chargées avec fetch join)
        dto.setUserId(recipe.getUser().getId());
        dto.setUsername(recipe.getUser().getUsername());
        
        // Catégorie (déjà chargée avec fetch join)
        if (recipe.getCategory() != null) {
            dto.setCategoryId(recipe.getCategory().getId());
            dto.setCategoryName(recipe.getCategory().getName());
        }
        
        // Ingrédients (déjà chargés avec fetch join)
        List<RecipeIngredientDTO> ingredientsDTO = new ArrayList<>();
        if (recipe.getRecipeIngredients() != null) {
            for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
                RecipeIngredientDTO ingredientDTO = new RecipeIngredientDTO();
                ingredientDTO.setId(ri.getId());
                ingredientDTO.setIngredientId(ri.getIngredient().getId());
                ingredientDTO.setIngredientName(ri.getIngredient().getName());
                ingredientDTO.setQuantity(ri.getQuantity());
                ingredientDTO.setUnit(ri.getUnit());
                ingredientsDTO.add(ingredientDTO);
            }
        }
        dto.setIngredients(ingredientsDTO);
        
        return dto;
    }

    private RecipeSummaryDTO convertToSummaryDTO(Recipe recipe) {
        RecipeSummaryDTO dto = new RecipeSummaryDTO();

        dto.setId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setPreparationTime(recipe.getPreparationTime());
        dto.setCookingTime(recipe.getCookingTime());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setServings(recipe.getServings());
        dto.setViewsCount(recipe.getViewsCount());
        dto.setFavoritesCount(recipe.getFavoritesCount());
        dto.setCreatedAt(recipe.getCreatedAt());
        
        // Informations utilisateur (déjà chargées avec fetch join)
        
        // Catégorie (déjà chargée avec fetch join)
        if (recipe.getCategory() != null) {
            dto.setCategoryName(recipe.getCategory().getName());
        }
        
        // Ingrédients (déjà chargés avec fetch join)
        List<RecipeIngredientDTO> ingredientsDTO = new ArrayList<>();
        if (recipe.getRecipeIngredients() != null) {
            for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
                RecipeIngredientDTO ingredientDTO = new RecipeIngredientDTO();
                ingredientDTO.setId(ri.getId());
                ingredientDTO.setIngredientId(ri.getIngredient().getId());
                ingredientDTO.setIngredientName(ri.getIngredient().getName());
                ingredientDTO.setQuantity(ri.getQuantity());
                ingredientDTO.setUnit(ri.getUnit());
                ingredientsDTO.add(ingredientDTO);
            }
        }
        dto.setIngredients(ingredientsDTO);

        return dto; 
    }
}