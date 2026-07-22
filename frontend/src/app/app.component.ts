import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';
import { ThemeService } from './services/theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet],
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'frontend';
  authService = inject(AuthService);
  themeService = inject(ThemeService);
  private router = inject(Router);

  showConfigMenu = false;
  showAboutModal = false;
  isMobileMenuOpen = false;

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu() {
    this.isMobileMenuOpen = false;
  }

  toggleConfigMenu() {
    this.showConfigMenu = !this.showConfigMenu;
  }

  openAboutModal() {
    this.showConfigMenu = false;
    this.isMobileMenuOpen = false;
    this.showAboutModal = true;
  }

  closeAboutModal() {
    this.showAboutModal = false;
  }

  abrirNuevoGastoMovil() {
    this.closeMobileMenu();
    this.router.navigate(['/dashboard'], { queryParams: { action: 'nuevoGasto' } });
  }
}

