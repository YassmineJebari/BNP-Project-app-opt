import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.html',
  styleUrls: ['./footer.css']
})
export class FooterComponent {
  currentYear = new Date().getFullYear();

  socialLinks = [
    { name: 'Facebook', url: '#', icon: 'f' },
    { name: 'Instagram', url: '#', icon: '📷' },
    { name: 'Pinterest', url: '#', icon: 'P' },
    { name: 'YouTube', url: '#', icon: '▶' }
  ];

  navigationLinks = [
    { label: 'Toutes les recettes', route: '/recipes' },
    { label: 'Catégories', route: '/categories' },
    { label: 'Recettes du moment', route: '/trending' },
    { label: 'À propos', route: '/about' }
  ];

  categoryLinks = [
    { label: 'Entrées', route: '/categories/entrees' },
    { label: 'Plats principaux', route: '/categories/plats' },
    { label: 'Desserts', route: '/categories/desserts' },
    { label: 'Végétarien', route: '/categories/vegetarian' }
  ];

  constructor() {}
}