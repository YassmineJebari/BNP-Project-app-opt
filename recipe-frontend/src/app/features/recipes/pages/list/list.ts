// list.ts
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../auth/services/auth.service';
import { RecipeService } from '../../services/recipe.service';
import { FooterComponent } from '../../../../shared/components/footer/footer';
import { RecipeDTO } from '../../models/recipe.models';

@Component({
  selector: 'app-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, FooterComponent],
  templateUrl: './list.html',
  styleUrls: ['./list.css']
})
export class List implements OnInit {
  recettes: RecipeDTO[] = [];
  filteredRecettes: RecipeDTO[] = [];
  recetteToDelete?: RecipeDTO;

  // Filtres
  searchTerm: string = '';
  selectedCategory: string = '';
  showFavoritesOnly: boolean = false;
  sortBy: string = '';

  // Liste des catégories uniques (pour filtrage)
  categories: string[] = [];

  constructor(
    private recipeService: RecipeService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadRecipes();
    console.log('🔍 User actuel dans list:', this.authService.getCurrentUser());
    console.log('👑 isAdmin dans list:', this.isAdmin());
    console.log('🔗 isLoggedIn dans list:', this.isLoggedIn());
  }

  // Charger toutes les recettes depuis le service
  loadRecipes() {
    this.recipeService.getAll().subscribe((data: RecipeDTO[]) => {
      this.recettes = data;
      // Extraire les catégories uniques pour le filtrage
      //this.categories = Array.from(new Set(data.map(r => r.categoryName).filter(Boolean)));
      this.categories = Array.from(new Set(data.map(r => r.categoryName).filter((c): c is string => !!c)));
      this.filterRecipes();
    });
  }

  // Filtrer les recettes
  filterRecipes() {
    this.filteredRecettes = this.recettes.filter((recette) => {
      const matchesSearch =
        !this.searchTerm ||
        recette.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        recette.description.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchesCategory =
        !this.selectedCategory || recette.categoryName === this.selectedCategory;

      const matchesFavorite =
        !this.showFavoritesOnly || recette.isFavorite;

      return matchesSearch && matchesCategory && matchesFavorite;
    });

    this.sortRecipes();
  }

  // Trier les recettes
  sortRecipes() {
    if (!this.sortBy) return;

    this.filteredRecettes.sort((a, b) => {
      switch (this.sortBy) {
        case 'recent':
          return (new Date(b.createdAt || '').getTime()) - (new Date(a.createdAt || '').getTime());
        case 'rating':
          return (b.favoritesCount || 0) - (a.favoritesCount || 0);
        case 'name':
          return a.title.localeCompare(b.title);
        case 'time':
          return (a.preparationTime || 0) - (b.preparationTime || 0);
        default:
          return 0;
      }
    });
  }

  // Toggle favoris
  toggleFavorites() {
    this.showFavoritesOnly = !this.showFavoritesOnly;
    this.filterRecipes();
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isAdmin(): boolean {
    const user = this.authService.getCurrentUser();
    return user?.role === 'ADMIN';
  }

  // Clear search
  clearSearch() {
    this.searchTerm = '';
    this.filterRecipes();
  }

  // Clear all filters
  clearFilters() {
    this.searchTerm = '';
    this.selectedCategory = '';
    this.showFavoritesOnly = false;
    this.sortBy = '';
    this.filterRecipes();
  }

  // Toggle favorite local
  toggleFavorite(recette: RecipeDTO) {
    recette.isFavorite = !recette.isFavorite;
  }

  // Ouvrir la modal de confirmation
  confirmDelete(recette: RecipeDTO) {
    this.recetteToDelete = recette;
  }

  // Annuler la suppression
  cancelDelete() {
    this.recetteToDelete = undefined;
  }

  // Supprimer la recette
  deleteRecipe() {
    if (this.recetteToDelete?.id) {
      this.recipeService.delete(this.recetteToDelete.id).subscribe(() => {
        this.loadRecipes();
        this.recetteToDelete = undefined;
      });
    }
  }
}
