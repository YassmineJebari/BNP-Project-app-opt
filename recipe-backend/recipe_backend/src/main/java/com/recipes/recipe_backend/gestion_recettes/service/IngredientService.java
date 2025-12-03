package com.recipes.recipe_backend.gestion_recettes.service;

import com.recipes.recipe_backend.dto.IngredientDTO;
import com.recipes.recipe_backend.gestion_recettes.entity.Ingredient;
import com.recipes.recipe_backend.gestion_recettes.entity.IngredientType;
import com.recipes.recipe_backend.gestion_recettes.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IngredientService {
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    // ✅ CACHE : Met en cache la liste complète
    @Cacheable(value = "ingredients", key = "'all'")
    public List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // ✅ CACHE : Met en cache par ID
    @Cacheable(value = "ingredients", key = "#id")
    public IngredientDTO getIngredientById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));
        return convertToDTO(ingredient);
    }
    
    // ✅ CACHE : Met en cache par type
    @Cacheable(value = "ingredients", key = "'type_' + #type")
    public List<IngredientDTO> getIngredientsByType(String type) {
        IngredientType ingredientType = IngredientType.valueOf(type.toUpperCase());
        return ingredientRepository.findByIngredientType(ingredientType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // ✅ CACHE : Met en cache les recherches
    @Cacheable(value = "ingredients", key = "'search_' + #name")
    public List<IngredientDTO> searchIngredients(String name) {
        return ingredientRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // ✅ CACHE : Invalide tout le cache lors de la création
    @CacheEvict(value = "ingredients", allEntries = true)
    public IngredientDTO createIngredient(IngredientDTO ingredientDTO) {
        if (ingredientRepository.existsByName(ingredientDTO.getName())) {
            throw new RuntimeException("Un ingrédient avec ce nom existe déjà");
        }
        
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientDTO.getName());
        ingredient.setIngredientType(IngredientType.valueOf(ingredientDTO.getIngredientType().toUpperCase()));
        
        Ingredient saved = ingredientRepository.save(ingredient);
        return convertToDTO(saved);
    }
    
    // ✅ CACHE : Invalide tout le cache lors de la modification
    @CacheEvict(value = "ingredients", allEntries = true)
    public IngredientDTO updateIngredient(Long id, IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));
        
        if (ingredientDTO.getName() != null && !ingredientDTO.getName().equals(ingredient.getName())) {
            if (ingredientRepository.existsByName(ingredientDTO.getName())) {
                throw new RuntimeException("Un ingrédient avec ce nom existe déjà");
            }
            ingredient.setName(ingredientDTO.getName());
        }
        
        if (ingredientDTO.getIngredientType() != null) {
            ingredient.setIngredientType(IngredientType.valueOf(ingredientDTO.getIngredientType().toUpperCase()));
        }
        
        Ingredient updated = ingredientRepository.save(ingredient);
        return convertToDTO(updated);
    }
    
    // ✅ CACHE : Invalide tout le cache lors de la suppression
    @CacheEvict(value = "ingredients", allEntries = true)
    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new RuntimeException("Ingrédient non trouvé");
        }
        ingredientRepository.deleteById(id);
    }
    
    private IngredientDTO convertToDTO(Ingredient ingredient) {
        return new IngredientDTO(
            ingredient.getId(),
            ingredient.getName(),
            ingredient.getIngredientType().name()
        );
    }
}