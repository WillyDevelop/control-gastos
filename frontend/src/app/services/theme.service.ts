import { Injectable, inject, signal } from '@angular/core';
import { DOCUMENT } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private document = inject(DOCUMENT);
  isDarkMode = signal<boolean>(false);

  constructor() {
    this.cargarTheme();
  }

  private cargarTheme() {
    const savedTheme = localStorage.getItem('theme');
    const isDark = savedTheme === 'dark';
    this.isDarkMode.set(isDark);
    this.aplicarTheme(isDark);
  }

  toggleTheme() {
    const newVal = !this.isDarkMode();
    this.isDarkMode.set(newVal);
    localStorage.setItem('theme', newVal ? 'dark' : 'light');
    this.aplicarTheme(newVal);
  }

  private aplicarTheme(isDark: boolean) {
    const root = this.document.documentElement;
    if (isDark) {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
  }
}
