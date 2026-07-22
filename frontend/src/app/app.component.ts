import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet } from '@angular/router';
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

  showConfigMenu = false;
  showAboutModal = false;

  toggleConfigMenu() {
    this.showConfigMenu = !this.showConfigMenu;
  }

  openAboutModal() {
    this.showConfigMenu = false;
    this.showAboutModal = true;
  }

  closeAboutModal() {
    this.showAboutModal = false;
  }
}
