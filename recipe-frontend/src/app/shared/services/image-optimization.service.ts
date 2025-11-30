import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ImageOptimizationService {

  /**
   * Convertit les URLs d'images en format WebP
   * @param url URL de l'image originale
   * @returns URL optimisée en WebP
   */
  getOptimizedImageUrl(url: string | null | undefined): string {
    // Image par défaut si pas d'URL
    if (!url || url === '') {
      return 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=800&fm=webp';
    }
    
    // Si c'est déjà du WebP, on retourne tel quel
    if (url.endsWith('.webp')) {
      return url;
    }
    
    // Pour les images Unsplash, on ajoute &fm=webp pour forcer WebP
    if (url.includes('unsplash.com')) {
      return url.includes('?') ? url + '&fm=webp' : url + '?fm=webp';
    }
    
    // Pour les autres, on remplace l'extension par .webp
    return url.replace(/\.(jpg|jpeg|png|gif)$/i, '.webp');
  }

  /**
   * Génère une URL responsive selon la taille
   * @param url URL de base
   * @param size Taille souhaitée
   */
  getResponsiveImageUrl(
    url: string, 
    size: 'small' | 'medium' | 'large' = 'medium'
  ): string {
    const optimizedUrl = this.getOptimizedImageUrl(url);
    
    // Pour Unsplash, on peut ajouter w= pour la largeur
    if (optimizedUrl.includes('unsplash.com')) {
      const widths = {
        small: 400,
        medium: 800,
        large: 1200
      };
      return optimizedUrl + `&w=${widths[size]}`;
    }
    
    return optimizedUrl;
  }
}