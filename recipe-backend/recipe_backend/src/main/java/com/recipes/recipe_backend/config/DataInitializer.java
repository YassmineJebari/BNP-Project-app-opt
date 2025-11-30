package com.recipes.recipe_backend.config;

import com.recipes.recipe_backend.gestion_recettes.entity.*;
import com.recipes.recipe_backend.gestion_recettes.repository.*;
import com.recipes.recipe_backend.gestion_utilisateur.entity.Role;
import com.recipes.recipe_backend.gestion_utilisateur.entity.User;
import com.recipes.recipe_backend.gestion_utilisateur.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        
        // Créer un admin si n'existe pas
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@recipes.com");
            admin.setPassword(passwordEncoder.encode("Admin123"));
            admin.setRole(Role.ADMIN);
            admin.setFirstName("Admin");
            admin.setLastName("System");
            userRepository.save(admin);
            System.out.println("✅ Admin créé : admin / Admin123");
        }
        
        // Créer un utilisateur de test
        if (!userRepository.existsByUsername("testuser")) {
            User user = new User();
            user.setUsername("testuser");
            user.setEmail("test@recipes.com");
            user.setPassword(passwordEncoder.encode("Test123"));
            user.setRole(Role.USER);
            user.setFirstName("Test");
            user.setLastName("User");
            userRepository.save(user);
            System.out.println("✅ Utilisateur test créé : testuser / Test123");
        }
        
        // Créer des catégories si elles n'existent pas
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category(null, "Entrées", "Plats d'entrée"));
            categoryRepository.save(new Category(null, "Plats principaux", "Plats de résistance"));
            categoryRepository.save(new Category(null, "Desserts", "Plats sucrés"));
            categoryRepository.save(new Category(null, "Boissons", "Boissons chaudes et froides"));
            categoryRepository.save(new Category(null, "Salades", "Salades composées"));
            categoryRepository.save(new Category(null, "Soupes", "Soupes et potages"));
            System.out.println("✅ Catégories créées");
        }
        
        // Créer des ingrédients si ils n'existent pas
        if (ingredientRepository.count() == 0) {
            ingredientRepository.save(new Ingredient(null, "Tomate", IngredientType.LEGUME));
            ingredientRepository.save(new Ingredient(null, "Oignon", IngredientType.LEGUME));
            ingredientRepository.save(new Ingredient(null, "Ail", IngredientType.LEGUME));
            ingredientRepository.save(new Ingredient(null, "Pomme de terre", IngredientType.LEGUME));
            ingredientRepository.save(new Ingredient(null, "Carotte", IngredientType.LEGUME));
            ingredientRepository.save(new Ingredient(null, "Poulet", IngredientType.VIANDE));
            ingredientRepository.save(new Ingredient(null, "Bœuf", IngredientType.VIANDE));
            ingredientRepository.save(new Ingredient(null, "Saumon", IngredientType.POISSON));
            ingredientRepository.save(new Ingredient(null, "Lait", IngredientType.PRODUIT_LAITIER));
            ingredientRepository.save(new Ingredient(null, "Fromage", IngredientType.PRODUIT_LAITIER));
            ingredientRepository.save(new Ingredient(null, "Beurre", IngredientType.PRODUIT_LAITIER));
            ingredientRepository.save(new Ingredient(null, "Farine", IngredientType.CEREALE));
            ingredientRepository.save(new Ingredient(null, "Riz", IngredientType.CEREALE));
            ingredientRepository.save(new Ingredient(null, "Pâtes", IngredientType.CEREALE));
            ingredientRepository.save(new Ingredient(null, "Sel", IngredientType.CONDIMENT));
            ingredientRepository.save(new Ingredient(null, "Poivre", IngredientType.EPICE));
            ingredientRepository.save(new Ingredient(null, "Huile d'olive", IngredientType.HUILE));
            ingredientRepository.save(new Ingredient(null, "Sucre", IngredientType.CONDIMENT));
            ingredientRepository.save(new Ingredient(null, "Œuf", IngredientType.AUTRE));
            ingredientRepository.save(new Ingredient(null, "Basilic", IngredientType.EPICE));
            System.out.println("✅ Ingrédients créés");
        }
        
        // Créer une recette d'exemple
        if (recipeRepository.count() == 0) {
            User admin = userRepository.findByUsername("admin").orElse(null);
            Category category = categoryRepository.findByName("Plats principaux").orElse(null);
            
            if (admin != null && category != null) {
                Recipe recipe = new Recipe();
                recipe.setTitle("Pâtes à la carbonara");
                recipe.setDescription("Un classique italien simple et délicieux");
                recipe.setPreparationTime(10);
                recipe.setCookingTime(15);
                recipe.setDifficulty(Difficulty.FACILE);
                recipe.setSteps("1. Faire cuire les pâtes\n2. Faire revenir les lardons\n3. Mélanger avec les œufs et le fromage\n4. Servir chaud");
                recipe.setServings(4);
                recipe.setUser(admin);
                recipe.setCategory(category);
                recipe.setImageUrl("https://example.com/carbonara.jpg");
                
                Recipe savedRecipe = recipeRepository.save(recipe);
                
                // Ajouter des ingrédients
                Ingredient pates = ingredientRepository.findByName("Pâtes").orElse(null);
                Ingredient oeuf = ingredientRepository.findByName("Œuf").orElse(null);
                Ingredient fromage = ingredientRepository.findByName("Fromage").orElse(null);
                
                if (pates != null) {
                    RecipeIngredient ri1 = new RecipeIngredient();
                    ri1.setRecipe(savedRecipe);
                    ri1.setIngredient(pates);
                    ri1.setQuantity(400.0);
                    ri1.setUnit("g");
                    recipeIngredientRepository.save(ri1);
                }
                
                if (oeuf != null) {
                    RecipeIngredient ri2 = new RecipeIngredient();
                    ri2.setRecipe(savedRecipe);
                    ri2.setIngredient(oeuf);
                    ri2.setQuantity(4.0);
                    ri2.setUnit("unités");
                    recipeIngredientRepository.save(ri2);
                }
                
                if (fromage != null) {
                    RecipeIngredient ri3 = new RecipeIngredient();
                    ri3.setRecipe(savedRecipe);
                    ri3.setIngredient(fromage);
                    ri3.setQuantity(100.0);
                    ri3.setUnit("g");
                    recipeIngredientRepository.save(ri3);
                }
                
                // Mettre à jour le compteur
                //admin.setRecipesCount(1);
                //userRepository.save(admin);
                
                System.out.println("✅ Recette exemple créée");
            }
        }
    }
}