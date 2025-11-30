import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/services/auth.service';
import { RecipeService } from '../../recipes/services/recipe.service';
import { User } from '../../auth/models/user.model';
import { RecipeDTO } from '../../recipes/models/recipe.models';
import { UserAdminService } from '../services/user-admin.service';

type TabType = 'recettes' | 'utilisateurs' | null;

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-layout.html',
  styleUrls: ['./admin-layout.css']
})
export class AdminLayoutComponent implements OnInit {
  user: User | null = null;
  totalRecipes = 0;
  totalUsers = 0;
  
  // Gestion des onglets
  activeTab: TabType = 'recettes';

  // Données pour l'onglet Recettes
  recipes: RecipeDTO[] = [];
  isLoadingRecipes = false;
  recipesError = '';

  // Données pour l'onglet Utilisateurs
  users: User[] = [];
  isLoadingUsers = false;
  usersError = '';

  constructor(
    private authService: AuthService,
    private recipeService: RecipeService,
    private userAdminService: UserAdminService,  
    public router: Router
  ) {}

  ngOnInit() {
    const currentUser = this.authService.getCurrentUser();

    // Sécurité : si ce n'est pas un admin, on le redirige
    if (!currentUser || currentUser.role !== 'ADMIN') {
      this.router.navigate(['/layouts/user-layout']);
      return;
    }

    this.user = currentUser;

    // On peut charger directement les recettes au démarrage
    this.loadRecipes();
    // Et éventuellement les utilisateurs si tu veux les avoir tout de suite :
    // this.loadUsers();
  }

  // Getter pour la compatibilité avec votre template
  get showRecette(): boolean {
    return this.activeTab === 'recettes';
  }

  private loadRecipes(): void {
    this.isLoadingRecipes = true;
    this.recipesError = '';

    this.recipeService.getAll().subscribe({
      next: (recipes) => {
        this.recipes = recipes;
        this.totalRecipes = recipes.length;
        this.isLoadingRecipes = false;
      },
      error: (error) => {
        console.error('Erreur chargement recettes', error);
        this.recipesError = 'Impossible de charger les recettes';
        this.isLoadingRecipes = false;
      }
    });
  }

  private loadUsers(): void {
    this.isLoadingUsers = true;
    this.usersError = '';

    this.userAdminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.totalUsers = users.length;
        this.isLoadingUsers = false;
      },
      error: (error) => {
        console.error('Erreur chargement utilisateurs', error);
        this.usersError = 'Impossible de charger les utilisateurs';
        this.isLoadingUsers = false;
      }
    });
  }


  // Toggle des sections
  toggleRecette(): void {
    const wasActive = this.activeTab === 'recettes';
    this.activeTab = wasActive ? null : 'recettes';

    // Si on vient d'ouvrir l'onglet et qu'on n'a pas encore chargé les données :
    if (!wasActive && this.recipes.length === 0) {
      this.loadRecipes();
    }
  }

  toggleUtilisateurs(): void {
    const wasActive = this.activeTab === 'utilisateurs';
    this.activeTab = wasActive ? null : 'utilisateurs';

    if (!wasActive && this.users.length === 0) {
      this.loadUsers();
    }
  }

  onDeleteRecipe(recipeId: number | undefined): void {
    if (!recipeId) return;
    if (!confirm('Supprimer cette recette ?')) return;

    this.recipeService.deleteRecipeByAdmin(recipeId).subscribe({
      next: () => {
        this.recipes = this.recipes.filter(r => r.id !== recipeId);
        this.totalRecipes = this.recipes.length;
      },
      error: (error) => {
        console.error('Erreur suppression recette', error);
        this.recipesError = 'Erreur lors de la suppression de la recette';
      }
    });
  }

  onDeleteUser(userId: number): void {
    if (!confirm('Supprimer cet utilisateur ?')) return;

    this.userAdminService.deleteUser(userId).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== userId);
        this.totalUsers = this.users.length;
      },
      error: (error) => {
        console.error('Erreur suppression utilisateur', error);
        this.usersError = 'Erreur lors de la suppression de l\'utilisateur';
      }
    });
  }

  logout() {
    this.authService.logout();
  }
}