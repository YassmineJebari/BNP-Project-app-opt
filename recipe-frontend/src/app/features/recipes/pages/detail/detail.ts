// details.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RecipeService } from '../../services/recipe.service';
import { interval, Subscription } from 'rxjs';
import { FooterComponent } from '../../../../shared/components/footer/footer';
import { RecipeDTO } from '../../models/recipe.models';

export interface RecipeComment {
  author: string;
  text: string;
  rating: number;
  date: Date;
}

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, FooterComponent],
  templateUrl: './detail.html',
  styleUrls: ['./detail.css']
})
export class Detail implements OnInit, OnDestroy {
  recette?: RecipeDTO;
  showDeleteModal = false;

  // Timer
  timerRunning = false;
  timeLeft = 0; // en secondes
  timerSubscription?: Subscription;

  // Shopping List
  shoppingList: { name: string; checked: boolean }[] = [];

  // Comments
  comments: RecipeComment[] = [];
  newComment: RecipeComment = {
    author: '',
    text: '',
    rating: 0,
    date: new Date()
  };

  constructor(
    private recipeService: RecipeService,
    private route: ActivatedRoute,
    private router: Router,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.recipeService.getRecipeById(id).subscribe(r => {
      this.recette = r;
      if (r && r.preparationTime) {
        this.timeLeft = r.preparationTime * 60;
      }

      this.generateShoppingList()

      // Charger les commentaires (simulés ici)
      this.comments = []; // À remplacer par un appel API si nécessaire
    });
  }

  ngOnDestroy() {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
  }

  // === VIDEO ===
  getSafeVideoUrl(url: string): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  // === FAVORITE ===
  toggleFavorite() {
    if (this.recette) {
      this.recette.isFavorite = !this.recette.isFavorite;
    }
  }

  // === TIMER ===
  startTimer() {
    if (!this.recette?.preparationTime) return;

    this.timerRunning = true;
    this.timerSubscription = interval(1000).subscribe(() => {
      if (this.timeLeft > 0) {
        this.timeLeft--;
      } else {
        this.pauseTimer();
        this.playAlertSound();
        alert('⏰ Temps de cuisson terminé !');
      }
    });
  }

  pauseTimer() {
    this.timerRunning = false;
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
  }

  resetTimer() {
    this.pauseTimer();
    if (this.recette?.preparationTime) {
      this.timeLeft = this.recette.preparationTime * 60;
    }
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }

  playAlertSound() {
    const audio = new Audio();
    audio.src =
      'data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmwhBTGH0fPTgjMGHm7A7+OZURE';
  }

  // === SHOPPING LIST ===
  generateShoppingList() {
    if (!this.recette) return;
    this.shoppingList = this.recette.ingredients.map(ing => ({
      name: `${ing.ingredientName || 'Ingredient'} - ${ing.quantity} ${ing.unit}`,
      checked: false
    }));
  }

  printShoppingList() {
    const printContent = this.shoppingList.map((item, i) => `${i + 1}. ${item.name}`).join('\n');

    const printWindow = window.open('', '', 'height=600,width=800');
    if (printWindow) {
      printWindow.document.write('<html><head><title>Liste de courses</title>');
      printWindow.document.write(
        '<style>body { font-family: Arial; padding: 20px; } h1 { color: #00915A; }</style>'
      );
      printWindow.document.write('</head><body>');
      printWindow.document.write(`<h1>🛒 Liste de courses - ${this.recette?.title}</h1>`);
      printWindow.document.write('<pre>' + printContent + '</pre>');
      printWindow.document.write('</body></html>');
      printWindow.document.close();
      printWindow.print();
    }
  }

  // === COMMENTS ===
  setRating(rating: number) {
    this.newComment.rating = rating;
  }

  addComment() {
    if (!this.newComment.author || !this.newComment.text || this.newComment.rating === 0) return;
    this.comments.push({ ...this.newComment });
    this.newComment = { author: '', text: '', rating: 0, date: new Date() };
  }

  formatDate(date: Date | string): string {
    const d = new Date(date);
    return d.toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  // === DELETE ===
  confirmDelete() {
    this.showDeleteModal = true;
  }

  cancelDelete() {
    this.showDeleteModal = false;
  }

  deleteRecipe() {
    if (this.recette && this.recette.id) {
      this.recipeService.delete(this.recette.id).subscribe(() => {
        this.router.navigate(['/recipes']);
      });
    }
  }
}
