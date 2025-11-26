package com.recipes.recipe_backend.gestion_recettes.service;

import com.recipes.recipe_backend.dto.IngredientDTO;
import com.recipes.recipe_backend.gestion_recettes.entity.Ingredient;
import com.recipes.recipe_backend.gestion_recettes.entity.IngredientType;
import com.recipes.recipe_backend.gestion_recettes.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IngredientService {
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    public List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public IngredientDTO getIngredientById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));
        return convertToDTO(ingredient);
    }
    
    public List<IngredientDTO> getIngredientsByType(String type) {
        IngredientType ingredientType = IngredientType.valueOf(type.toUpperCase());
        return ingredientRepository.findByIngredientType(ingredientType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<IngredientDTO> searchIngredients(String name) {
        return ingredientRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
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