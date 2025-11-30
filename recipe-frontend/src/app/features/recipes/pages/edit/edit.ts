// edit.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecipeService } from '../../services/recipe.service';
import { RecipeDTO, RecipeIngredientDTO, UpdateRecipeRequest } from '../../models/recipe.models';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './edit.html',
  styleUrls: ['./edit.css']
})
export class Edit implements OnInit {
  recette: RecipeDTO = {
    title: '',
    description: '',
    preparationTime: 30,
    cookingTime: 30,
    difficulty: 'FACILE',
    servings: 4,
    steps: '',
    ingredients: []
  };

  ingredientInput: string = '';
  id!: number;
  isAdmin = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private recipeService: RecipeService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.isAdmin = this.authService.isAdmin(); 
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.recipeService.getRecipeById(this.id).subscribe(r => {
      if (r) {
        this.recette = { ...r };
      }
    });
  }

  addIngredient() {
    const trimmed = this.ingredientInput.trim();
    if (trimmed) {
      // Ajouter un ingredient minimal (sans id et quantity par défaut)
      const newIngredient: RecipeIngredientDTO = {
        ingredientId: 0,
        ingredientName: trimmed,
        quantity: 1,
        unit: ''
      };
      this.recette.ingredients.push(newIngredient);
      this.ingredientInput = '';
    }
  }

  removeIngredient(index: number) {
    this.recette.ingredients.splice(index, 1);
  }

  save() {
    const updateRequest: UpdateRecipeRequest = {
      title: this.recette.title,
      description: this.recette.description,
      preparationTime: this.recette.preparationTime,
      cookingTime: this.recette.cookingTime,
      difficulty: this.recette.difficulty,
      servings: this.recette.servings,
      steps: this.recette.steps,
      imageUrl: this.recette.imageUrl,
      categoryId: this.recette.categoryId,
      ingredients: this.recette.ingredients
    };

    const update$ = this.isAdmin
      ? this.recipeService.updateRecipeByAdmin(this.id, updateRequest)  // 🔹 admin
      : this.recipeService.updateRecipe(this.id, updateRequest);        // 🔹 auteur

    update$.subscribe({
      next: () => {
        this.router.navigate(['/layouts/admin-layout']);
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour de la recette', err);
        alert(err.error || "Impossible de mettre à jour la recette.");
      }
    });
  }

}
