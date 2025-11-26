import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent {
  isMenuOpen = false;
  isLoggedIn = false; // Tu pourras connecter ça à ton AuthService plus tard

  constructor(private router: Router) {
    // Vérifie si l'utilisateur est connecté (à adapter selon ton AuthService)
    this.checkLoginStatus();
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  checkLoginStatus() {
    // TODO: Remplacer par ton AuthService
    // this.isLoggedIn = this.authService.isLoggedIn();
    this.isLoggedIn = true; // Pour l'instant, on simule un user connecté
  }

  logout() {
    // TODO: Implémenter la déconnexion avec ton AuthService
    // this.authService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/auth/sign-in']);
  }
}