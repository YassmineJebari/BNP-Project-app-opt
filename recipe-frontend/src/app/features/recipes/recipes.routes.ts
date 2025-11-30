import { Routes } from '@angular/router';

export const RECIPES_ROUTES: Routes = [
      {
        path: '',
        loadComponent: () => import('./pages/list/list')
          .then(m => m.List)
      },
      {
        path: 'add',
        loadComponent: () => import('./pages/add/add')
          .then(m => m.Add)
      },
      {
        path: 'detail/:id',
        loadComponent: () => import('./pages/detail/detail')
          .then(m => m.Detail)
      },
      {
        path: 'edit/:id',
        loadComponent: () => import('./pages/edit/edit')
          .then(m => m.Edit)
      }
    ]