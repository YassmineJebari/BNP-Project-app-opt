// src/app/features/recipes/models/recipe.models.ts

export interface RecipeIngredientDTO {
  id?: number;
  ingredientId: number;
  ingredientName?: string;
  quantity: number;
  unit: string;
}

export interface RecipeDTO {
  id?: number;
  title: string;
  description: string;
  preparationTime: number;
  cookingTime: number;
  difficulty: 'FACILE' | 'MOYEN' | 'DIFFICILE';
  imageUrl?: string;
  steps: string;
  servings: number;
  viewsCount?: number;
  favoritesCount?: number;
  userId?: number;
  username?: string;
  categoryId?: number;
  categoryName?: string;
  ingredients: RecipeIngredientDTO[];
  createdAt?: string;
  updatedAt?: string;
  isFavorite?: boolean; // Pour usage local côté frontend
  videoUrl?: string; // Uniquement front
}

export interface CreateRecipeRequest {
  title: string;
  description: string;
  preparationTime: number;
  cookingTime: number;
  difficulty: string;
  imageUrl?: string;
  steps: string;
  servings: number;
  categoryId?: number;
  ingredients: RecipeIngredientDTO[];
}

export interface UpdateRecipeRequest {
  title?: string;
  description?: string;
  preparationTime?: number;
  cookingTime?: number;
  difficulty?: string;
  imageUrl?: string;
  steps?: string;
  servings?: number;
  categoryId?: number;
  ingredients?: RecipeIngredientDTO[];
}

export interface CategoryDTO {
  id: number;
  name: string;
  description?: string;
}

export interface UserDTO {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
}