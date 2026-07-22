import { Component, OnInit, inject } from '@angular/core';
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
export class AppComponent implements OnInit {
  title = 'frontend';
  authService = inject(AuthService);
  themeService = inject(ThemeService);
  private gastoService = inject(GastoService);
  private router = inject(Router);

  showConfigMenu = false;
  showAboutModal = false;
  showNotificacionModal = false;
  isMobileMenuOpen = false;
  isNotificationMuted = false;
  private muteTimer: any;

  ngOnInit() {
    this.checkMuteStatus();
  }

  private checkMuteStatus() {
    const mutedUntil = localStorage.getItem('notif_muted_until');
    if (mutedUntil) {
      const remainingMs = parseInt(mutedUntil, 10) - Date.now();
      if (remainingMs > 0) {
        this.isNotificationMuted = true;
        if (this.muteTimer) clearTimeout(this.muteTimer);
        this.muteTimer = setTimeout(() => {
          this.isNotificationMuted = false;
          localStorage.removeItem('notif_muted_until');
        }, remainingMs);
      } else {
        localStorage.removeItem('notif_muted_until');
        this.isNotificationMuted = false;
      }
    }
  }

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

  openNotificacionModal() {
    this.showConfigMenu = false;
    this.isMobileMenuOpen = false;
    this.showNotificacionModal = true;

    // Pausar animación y punto rojo durante 10 minutos (600.000 ms)
    this.isNotificationMuted = true;
    const mutedUntilTime = Date.now() + (10 * 60 * 1000);
    localStorage.setItem('notif_muted_until', mutedUntilTime.toString());

    if (this.muteTimer) clearTimeout(this.muteTimer);
    this.muteTimer = setTimeout(() => {
      this.isNotificationMuted = false;
      localStorage.removeItem('notif_muted_until');
    }, 10 * 60 * 1000);
  }

  closeNotificacionModal() {
    this.showNotificacionModal = false;
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

