import { Routes } from '@angular/router';
import { SignIn } from './features/auth/pages/sign-in/sign-in';
import { SignUp } from './features/auth/pages/sign-up/sign-up';
import { List } from './features/recipes/pages/list/list';
import { Add } from './features/recipes/pages/add/add';
import { Edit } from './features/recipes/pages/edit/edit';
import { Detail } from './features/recipes/pages/detail/detail';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes')
      .then(m => m.AUTH_ROUTES)
  },
  
  {
    path: 'recipes',
    loadChildren: () => import('./features/recipes/recipes.routes')
      .then(m => m.RECIPES_ROUTES)
  },
  {
    path: 'layouts',
    loadChildren: () => import('./features/layouts/layouts.routes')
      .then(m => m.LAYOUT_ROUTES)
  },
  
  /*{
    path: 'chatbot',
    loadChildren: () => import('./features/chatbot/chatbot.routes')
      .then(m => m.CHATBOT_ROUTES)
  },*/
  
  // Route par défaut
  { 
    path: '', 
    redirectTo: 'auth/sign-in', 
    pathMatch: 'full' 
  },
  
  // Routes invalides
  { 
    path: '**', 
    redirectTo: 'auth/sign-in'
  }
];
