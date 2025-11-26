package com.recipes.recipe_backend.gestion_recettes.service;

import com.recipes.recipe_backend.dto.CreateRecipeRequest;
import com.recipes.recipe_backend.dto.RecipeDTO;
import com.recipes.recipe_backend.dto.RecipeIngredientDTO;
import com.recipes.recipe_backend.dto.UpdateRecipeRequest;
import com.recipes.recipe_backend.gestion_recettes.entity.*;
import com.recipes.recipe_backend.gestion_recettes.repository.*;
import com.recipes.recipe_backend.gestion_utilisateur.entity.User;
import com.recipes.recipe_backend.gestion_utilisateur.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public RecipeDTO getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
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
        //recipe.setServings(request.getServings());
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
        
        // Mettre à jour le compteur de recettes de l'utilisateur
        //user.setRecipesCount(user.getRecipesCount() + 1);
        //userRepository.save(user);
        
        return convertToDTO(savedRecipe);
    }
    
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

    public RecipeDTO updateRecipeByAdmin(Long id, UpdateRecipeRequest request) {
        // Récupérer la recette
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recette non trouvée"));
        
        // ⚠️ Ici PAS de vérification de l'auteur : l'admin peut tout modifier

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

        // Sauvegarder et retourner le DTO
        Recipe updated = recipeRepository.save(recipe);
        return convertToDTO(updated);
    }


    public void deleteRecipe(Long id, String username) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recette non trouvée"));
        
        // Vérifier que l'utilisateur est l'auteur
        if (!recipe.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cette recette");
        }
        
        // Décrémenter le compteur de recettes de l'utilisateur
        //User user = recipe.getUser();
        //user.setRecipesCount(Math.max(0, user.getRecipesCount() - 1));
        //userRepository.save(user);
        
        recipeRepository.deleteById(id);
    }
    
    public void deleteRecipeByAdmin(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recette non trouvée"));
        
        // Décrémenter le compteur de recettes de l'utilisateur
        //User user = recipe.getUser();
        //user.setRecipesCount(Math.max(0, user.getRecipesCount() - 1));
        //userRepository.save(user);
        
        recipeRepository.deleteById(id);
    }
    
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
        
        // Informations utilisateur
        dto.setUserId(recipe.getUser().getId());
        dto.setUsername(recipe.getUser().getUsername());
        
        // Catégorie
        if (recipe.getCategory() != null) {
            dto.setCategoryId(recipe.getCategory().getId());
            dto.setCategoryName(recipe.getCategory().getName());
        }
        
        // Ingrédients
        List<RecipeIngredientDTO> ingredientsDTO = new ArrayList<>();
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findByRecipeId(recipe.getId());
        for (RecipeIngredient ri : recipeIngredients) {
            RecipeIngredientDTO ingredientDTO = new RecipeIngredientDTO();
            ingredientDTO.setId(ri.getId());
            ingredientDTO.setIngredientId(ri.getIngredient().getId());
            ingredientDTO.setIngredientName(ri.getIngredient().getName());
            ingredientDTO.setQuantity(ri.getQuantity());
            ingredientDTO.setUnit(ri.getUnit());
            ingredientsDTO.add(ingredientDTO);
        }
        dto.setIngredients(ingredientsDTO);
        
        return dto;
    }
}