import { Routes } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

export const LAYOUT_ROUTES: Routes = [
  {
    path: 'admin-layout',
    loadComponent: () => import('./admin-layout/admin-layout')
      .then(m => m.AdminLayoutComponent)
  },
  {
    path: 'user-layout',
    loadComponent: () => import('./user-layout/user-layout')
      .then(m => m.UserLayoutComponent)
  },
  { 
    path: '', 
    redirectTo: 'sign-in',
    pathMatch: 'full' 
  }
];