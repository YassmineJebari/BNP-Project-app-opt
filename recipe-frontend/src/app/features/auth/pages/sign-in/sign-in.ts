import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './sign-in.html',
  styleUrls: ['./sign-in.css']
})
export class SignIn {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  // 🔽 nouvelle méthode : choisit le layout en fonction du rôle
  private redirectAfterLogin(user: User | null | undefined): void {
    if (!user) {
      // fallback au cas où, mais normalement on a toujours un user ici
      this.router.navigate(['/recipes']);
      return;
    }

    if (user.role === 'ADMIN') {
      this.router.navigate(['/layouts/admin-layout']);
    } else {
      this.router.navigate(['/layouts/user-layout']);
    }
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Veuillez remplir correctement les champs.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        console.log('Connexion réussie', response);
        this.loading = false;
        // Le token + l'user sont déjà gérés dans AuthService
        this.redirectAfterLogin(response.user);
      },
      error: (error) => {
        this.loading = false;
        const backendError = error.error;

        if (typeof backendError === 'string') {
          this.errorMessage = backendError;
        } else if (backendError?.message) {
          this.errorMessage = backendError.message;
        } else {
          this.errorMessage = 'Email ou mot de passe incorrect';
        }

        console.error('Erreur de connexion', error);
      }
    });
  }

  get email() {
    return this.loginForm.get('email');
  }

  get password() {
    return this.loginForm.get('password');
  }
}
