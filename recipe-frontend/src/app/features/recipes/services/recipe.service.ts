// src/app/features/recipes/services/recipe.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  RecipeDTO, 
  CreateRecipeRequest, 
  UpdateRecipeRequest,
  CategoryDTO,
} from '../models/recipe.models';

@Injectable({
  providedIn: 'root'
})

export class RecipeService {
  private apiUrl = 'http://localhost:8080/api/recipes';

  constructor(private http: HttpClient) {}

  // GET : Toutes les recettes
  getAll(): Observable<RecipeDTO[]> {
    return this.http.get<RecipeDTO[]>(this.apiUrl);
  }

  // GET : Une recette par ID
  getRecipeById(id: number): Observable<RecipeDTO> {
    return this.http.get<RecipeDTO>(`${this.apiUrl}/${id}`);
  }

  // GET : Recettes d'un utilisateur spécifique
  getRecipesByUserId(userId: number): Observable<RecipeDTO[]> {
    return this.http.get<RecipeDTO[]>(`${this.apiUrl}/user/${userId}`);
  }

  // GET : Mes recettes (utilisateur connecté)
  getMyRecipes(): Observable<RecipeDTO[]> {
    return this.http.get<RecipeDTO[]>(`${this.apiUrl}/my-recipes`);
  }

  // GET : Recettes par catégorie
  getRecipesByCategory(categoryId: number): Observable<RecipeDTO[]> {
    return this.http.get<RecipeDTO[]>(`${this.apiUrl}/category/${categoryId}`);
  }

  // GET : Recettes par difficulté
  getRecipesByDifficulty(difficulty: string): Observable<RecipeDTO[]> {
    return this.http.get<RecipeDTO[]>(`${this.apiUrl}/difficulty/${difficulty}`);
  }

  // GET : Recherche de recettes
  searchRecipes(keyword: string): Observable<RecipeDTO[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<RecipeDTO[]>(`${this.apiUrl}/search`, { params });
  }

  // GET : Recettes populaires
  getPopularRecipes(): Observable<RecipeDTO[]> {
    return this.http.get<RecipeDTO[]>(`${this.apiUrl}/popular`);
  }

  // GET : Recettes les plus aimées
  getMostFavoritedRecipes(): Observable<RecipeDTO[]> {
    return this.http.get<RecipeDTO[]>(`${this.apiUrl}/most-favorited`);
  }

  // GET : Recettes récentes
  getRecentRecipes(): Observable<RecipeDTO[]> {
    return this.http.get<RecipeDTO[]>(`${this.apiUrl}/recent`);
  }

  // GET : Recettes rapides
  getQuickRecipes(maxTime: number): Observable<RecipeDTO[]> {
    const params = new HttpParams().set('maxTime', maxTime.toString());
    return this.http.get<RecipeDTO[]>(`${this.apiUrl}/quick`, { params });
  }

  // POST : Créer une recette
  create(request: CreateRecipeRequest): Observable<RecipeDTO> {
    return this.http.post<RecipeDTO>(this.apiUrl, request);
  }

  // PUT : Mettre à jour une recette
  updateRecipe(id: number, request: UpdateRecipeRequest): Observable<RecipeDTO> {
    return this.http.put<RecipeDTO>(`${this.apiUrl}/${id}`, request);
  }

  // PUT : Mettre à jour une recette (admin)
  updateRecipeByAdmin(id: number, request: UpdateRecipeRequest): Observable<RecipeDTO> {
    return this.http.put<RecipeDTO>(`${this.apiUrl}/admin/${id}`, request);
  }

  // DELETE : Supprimer une recette (auteur uniquement)
  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }

  // DELETE : Supprimer une recette (admin)
  deleteRecipeByAdmin(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/admin/${id}`, { responseType: 'text' });
  }

  // Test de connexion API
  testConnection(): Observable<string> {
    return this.http.get(`${this.apiUrl}/test`, { responseType: 'text' });
  }
}