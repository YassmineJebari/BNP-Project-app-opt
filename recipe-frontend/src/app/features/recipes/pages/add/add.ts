import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecipeService } from '../../services/recipe.service';
import { CreateRecipeRequest, RecipeIngredientDTO } from '../../models/recipe.models';

interface Recette {
  titre: string;
  description: string;
  ingredients: string[];
  imageUrl: string;
  videoUrl?: string;
  category: string;
  preparationTime: number;
  portions: number;
  difficulty: 'FACILE' | 'MOYEN' | 'DIFFICILE';
  steps: string;
}

@Component({
  selector: 'app-add',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './add.html',
  styleUrls: ['./add.css']
})
export class Add {
  recette: Recette = {
    titre: '',
    description: '',
    ingredients: [],
    imageUrl: '',
    videoUrl: '',
    category: '',
    preparationTime: 30,
    portions: 4,
    difficulty: 'FACILE',
    steps: ''
  };

  ingredientInput: string = '';

  constructor(private recipeService: RecipeService, private router: Router) {}

  addIngredient() {
    const trimmed = this.ingredientInput.trim();
    if (trimmed) {
      this.recette.ingredients.push(trimmed);
      this.ingredientInput = '';
    }
  }

  removeIngredient(index: number) {
    this.recette.ingredients.splice(index, 1);
  }

  private mapRecetteToRequest(): CreateRecipeRequest {
    return {
      title: this.recette.titre,
      description: this.recette.description,
      preparationTime: this.recette.preparationTime,
      cookingTime: 30, // Valeur par défaut si non spécifiée
      difficulty: this.recette.difficulty.toUpperCase(),
      imageUrl: this.recette.imageUrl,
      steps: this.recette.steps,
      servings: this.recette.portions,
      categoryId: this.mapCategoryToId(this.recette.category),
      ingredients: this.recette.ingredients.map(this.mapIngredient)
    };
  }

  save() {
    // 1. Validation simple côté front
    if (!this.recette.titre || !this.recette.titre.trim()) {
      alert('Le titre de la recette est obligatoire.');
      return;
    }

    if (!this.recette.description || !this.recette.description.trim()) {
      alert('La description est obligatoire.');
      return;
    }

    if (!this.recette.steps || !this.recette.steps.trim()) {
      alert('Les étapes sont obligatoires.');
      return;
    }
    
    if (!this.recette.ingredients || this.recette.ingredients.length === 0) {
      alert('Ajoute au moins un ingrédient.');
      return;
    }

    // 2. Construire l'objet attendu par le backend
    const request: CreateRecipeRequest = this.mapRecetteToRequest();

    // Optionnel : sécuriser la catégorie
    if (!request.categoryId) {
      // Soit tu choisis une catégorie par défaut :
      // request.categoryId = 1;
      // soit tu bloques :
      alert('Sélectionne une catégorie de recette.');
      return;
    }

    // 3. Appeler le backend via le service
    this.recipeService.create(request).subscribe({
      next: (createdRecipe) => {
        console.log('Recette créée :', createdRecipe);
        // Redirection après succès : liste des recettes
        this.router.navigate(['/layouts/admin-layout']);
        // ou vers le détail :
        // this.router.navigate(['/recipes/detail', createdRecipe.id]);
      },
      error: (err) => {
        console.error('Erreur lors de la création de la recette', err);
        alert("Une erreur s'est produite lors de l'enregistrement de la recette.");
      },
    });
  }

  private mapIngredient(name: string, index: number): RecipeIngredientDTO {
    return {
      ingredientId: index + 1, // ID fictif si pas disponible
      ingredientName: name,
      quantity: 1, // Valeur par défaut
      unit: 'unit' // Valeur par défaut
    };
  }

  private mapCategoryToId(category: string): number | undefined {
    const categories: { [key: string]: number } = {
      'Entrée': 1,
      'Plat': 2,
      'Dessert': 3
    };
    return categories[category];
  }
}
