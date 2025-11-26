package com.recipes.recipe_backend.gestion_recettes.entity;

import com.recipes.recipe_backend.gestion_utilisateur.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    @Column(nullable = false, length = 200)
    private String title;
    
    @NotBlank(message = "La description est obligatoire")
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    @Column(nullable = false, length = 1000)
    private String description;
    
    @NotNull(message = "Le temps de préparation est obligatoire")
    @Positive(message = "Le temps de préparation doit être positif")
    @Column(name = "preparation_time", nullable = false)
    private Integer preparationTime; // en minutes
    
    @NotNull(message = "Le temps de cuisson est obligatoire")
    @Positive(message = "Le temps de cuisson doit être positif")
    @Column(name = "cooking_time", nullable = false)
    private Integer cookingTime; // en minutes
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty = Difficulty.MOYEN;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String steps; // Étapes de préparation (peut être stocké en JSON ou texte)
    
    // Relation avec User (auteur de la recette)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Relation avec Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    // Relation avec RecipeIngredient (ingrédients de la recette)
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();
    
    // Nombre de portions
    @Positive(message = "Le nombre de portions doit être positif")
    @Column(name = "servings")
    private Integer servings = 1;
    
    // Statistiques
    @Column(name = "views_count")
    private Integer viewsCount = 0;
    
    @Column(name = "favorites_count")
    private Integer favoritesCount = 0;
    
    // Dates
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Méthodes utilitaires pour gérer les ingrédients
    public void addRecipeIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredients.add(recipeIngredient);
        recipeIngredient.setRecipe(this);
    }
    
    public void removeRecipeIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredients.remove(recipeIngredient);
        recipeIngredient.setRecipe(null);
    }
}