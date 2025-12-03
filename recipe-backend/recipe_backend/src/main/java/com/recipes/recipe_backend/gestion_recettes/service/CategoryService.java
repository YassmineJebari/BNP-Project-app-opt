package com.recipes.recipe_backend.gestion_recettes.service;

import com.recipes.recipe_backend.dto.CategoryDTO;
import com.recipes.recipe_backend.gestion_recettes.entity.Category;
import com.recipes.recipe_backend.gestion_recettes.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // ✅ CACHE : Met en cache la liste complète
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // ✅ CACHE : Met en cache par ID
    @Cacheable(value = "categories", key = "#id")
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        return convertToDTO(category);
    }
    
    // ✅ CACHE : Met en cache par nom
    @Cacheable(value = "categories", key = "'name_' + #name")
    public CategoryDTO getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        return convertToDTO(category);
    }
    
    // ✅ CACHE : Invalide le cache de la liste lors de la création
    @CacheEvict(value = "categories", key = "'all'")
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà");
        }
        
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        
        Category saved = categoryRepository.save(category);
        return convertToDTO(saved);
    }
    
    // ✅ CACHE : Invalide le cache lors de la modification
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        
        if (categoryDTO.getName() != null && !categoryDTO.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(categoryDTO.getName())) {
                throw new RuntimeException("Une catégorie avec ce nom existe déjà");
            }
            category.setName(categoryDTO.getName());
        }
        
        if (categoryDTO.getDescription() != null) {
            category.setDescription(categoryDTO.getDescription());
        }
        
        Category updated = categoryRepository.save(category);
        return convertToDTO(updated);
    }
    
    // ✅ CACHE : Invalide le cache lors de la suppression
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Catégorie non trouvée");
        }
        categoryRepository.deleteById(id);
    }
    
    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(
            category.getId(),
            category.getName(),
            category.getDescription()
        );
    }
}