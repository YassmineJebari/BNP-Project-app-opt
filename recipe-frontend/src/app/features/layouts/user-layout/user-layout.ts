import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../auth/services/auth.service';
import { RecipeService } from '../../recipes/services/recipe.service';

import { User } from '../../auth/models/user.model';
import { RecipeDTO } from '../../recipes/models/recipe.models';

@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './user-layout.html',
  styleUrls: ['./user-layout.css']
})
export class UserLayoutComponent implements OnInit {

  // ======== UTILISATEUR ========
  user: User = {
    id: 0,
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    role: 'USER',
    createdAt: new Date()
  };

  private originalUser: User = { ...this.user };

  // ======== RECETTE ACTUELLE ========
  recette: RecipeDTO = {
    id: 0,
    title: '',
    description: '',
    preparationTime: 0,
    cookingTime: 0,
    difficulty: 'FACILE',
    imageUrl: '',
    steps: '',
    servings: 0,
    ingredients: [],
    categoryId: undefined,
    categoryName: '',
    favoritesCount: 0,
    viewsCount: 0,
    createdAt: new Date().toISOString(),
    isFavorite: false
  };

  // ======== LISTE DES RECETTES ========
  recettes: RecipeDTO[] = [];
  filteredRecettes: RecipeDTO[] = [];
  recetteToDelete?: RecipeDTO;

  searchTerm = '';
  selectedCategory: string | number = '';
  showFavoritesOnly = false;
  sortBy = '';

  // ======== ÉTATS ========
  favoritesCount = 0;
  isEditing = false;
  isLoading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private recipeService: RecipeService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadUserData();
    this.loadFavorites();
    this.loadRecipes();
  }

  // ================= PROFIL =================

  loadUserData() {
    this.isLoading = true;
    this.authService.getProfile().subscribe({
      next: (user) => {
        this.user = { ...user };
        this.originalUser = { ...user };
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Impossible de charger le profil';
        this.isLoading = false;
      }
    });
  }

  toggleEdit() {
    if (this.isEditing) {
      this.user = { ...this.originalUser };
    }
    this.isEditing = !this.isEditing;
    this.successMessage = '';
    this.errorMessage = '';
  }

  save() {
    this.isLoading = true;

    // Simulation — tu peux remplacer par updateUser()
    setTimeout(() => {
      this.originalUser = { ...this.user };
      this.isEditing = false;
      this.isLoading = false;
      this.successMessage = 'Profil mis à jour avec succès !';
      setTimeout(() => (this.successMessage = ''), 3000);
    }, 500);
  }

  cancel() {
    this.user = { ...this.originalUser };
    this.isEditing = false;
  }

  logout() {
    this.authService.logout();
  }

  // ================= FAVORIS =================

  loadFavorites() {
    this.recipeService.getAll().subscribe(recipes => {
      this.favoritesCount = recipes.filter(r => r.isFavorite).length;
    });
  }

  // ================= LISTE DES RECETTES =================

  loadRecipes() {
    this.recipeService.getAll().subscribe(data => {
      this.recettes = data.map(r => ({
        ...r,
        isFavorite: r.isFavorite ?? false
      }));
      this.filterRecipes();
    });
  }

  filterRecipes() {
    this.filteredRecettes = this.recettes.filter(recette => {
      const search = this.searchTerm.toLowerCase();

      const matchesSearch =
        !this.searchTerm ||
        recette.title.toLowerCase().includes(search) ||
        recette.description.toLowerCase().includes(search);

      const matchesCategory =
        !this.selectedCategory || recette.categoryId === Number(this.selectedCategory);

      const matchesFavorite =
        !this.showFavoritesOnly || recette.isFavorite === true;

      return matchesSearch && matchesCategory && matchesFavorite;
    });

    this.sortRecipes();
  }

  sortRecipes() {
    if (!this.sortBy) return;

    this.filteredRecettes.sort((a, b) => {
      switch (this.sortBy) {
        case 'recent':
          return new Date(b.createdAt || '').getTime() -
                 new Date(a.createdAt || '').getTime();

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

  toggleFavoritesFilter() {
    this.showFavoritesOnly = !this.showFavoritesOnly;
    this.filterRecipes();
  }

  toggleFavorite(recette: RecipeDTO) {
    if (!this.isLoggedIn()) return;
    recette.isFavorite = !recette.isFavorite;
  }

  clearFilters() {
    this.searchTerm = '';
    this.selectedCategory = '';
    this.showFavoritesOnly = false;
    this.sortBy = '';
    this.filterRecipes();
  }

  confirmDelete(recette: RecipeDTO) {
    if (this.isAdmin()) {
      this.recetteToDelete = recette;
    }
  }

  cancelDelete() {
    this.recetteToDelete = undefined;
  }

  deleteRecipe() {
    if (this.isAdmin() && this.recetteToDelete?.id) {
      this.recipeService.delete(this.recetteToDelete.id).subscribe(() => {
        this.loadRecipes();
        this.recetteToDelete = undefined;
      });
    }
  }

  // ================= UTILITAIRES =================

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isAdmin(): boolean {
    const user = this.authService.getCurrentUser();
    return user?.role === 'ADMIN';
  }

  getUserInitials(): string {
    if (!this.user.firstName || !this.user.lastName) return '?';
    return (this.user.firstName[0] + this.user.lastName[0]).toUpperCase();
  }

  getUserFullName(): string {
    return `${this.user.firstName} ${this.user.lastName}`;
  }

  getMemberSince(): string {
    if (!this.user?.createdAt) return 'Récent';
    return new Date(this.user.createdAt)
      .toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' });
  }
}
