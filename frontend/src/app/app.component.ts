import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';
import { ThemeService } from './services/theme.service';
import { GastoService } from './services/gasto.service';

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
  private gastoService = inject(GastoService);
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
    if (!this.router.url.includes('/dashboard')) {
      this.router.navigate(['/dashboard']).then(() => {
        this.gastoService.triggerAbrirModal('GASTO');
      });
    } else {
      this.gastoService.triggerAbrirModal('GASTO');
    }
  }
}

