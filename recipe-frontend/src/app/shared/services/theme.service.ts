import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly DARK_MODE_KEY = 'darkMode';
  private isBrowser: boolean;

  constructor(@Inject(PLATFORM_ID) platformId: Object) {
    // Vérifie si on est dans le navigateur
    this.isBrowser = isPlatformBrowser(platformId);
    
    // Charge la préférence seulement si on est dans le navigateur
    if (this.isBrowser) {
      this.loadThemeFromStorage();
    }
  }

  /**
   * Active ou désactive le mode sombre
   */
  toggleDarkMode(): void {
    if (!this.isBrowser) return;
    
    const isDark = document.body.classList.toggle('dark-mode');
    localStorage.setItem(this.DARK_MODE_KEY, String(isDark));
  }

  /**
   * Vérifie si le mode sombre est actif
   */
  isDarkMode(): boolean {
    if (!this.isBrowser) return false;
    return document.body.classList.contains('dark-mode');
  }

  /**
   * Active le mode sombre
   */
  enableDarkMode(): void {
    if (!this.isBrowser) return;
    
    document.body.classList.add('dark-mode');
    localStorage.setItem(this.DARK_MODE_KEY, 'true');
  }

  /**
   * Désactive le mode sombre
   */
  disableDarkMode(): void {
    if (!this.isBrowser) return;
    
    document.body.classList.remove('dark-mode');
    localStorage.setItem(this.DARK_MODE_KEY, 'false');
  }

  /**
   * Charge le thème depuis localStorage
   */
  private loadThemeFromStorage(): void {
    if (!this.isBrowser) return;
    
    try {
      const savedTheme = localStorage.getItem(this.DARK_MODE_KEY);
      
      if (savedTheme === 'true') {
        document.body.classList.add('dark-mode');
      }
    } catch (error) {
      console.warn('Impossible de charger le thème depuis localStorage', error);
    }
  }
}