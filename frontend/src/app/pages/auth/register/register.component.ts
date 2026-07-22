import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { ThemeService } from '../../../services/theme.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  public themeService = inject(ThemeService);

  registerForm = this.fb.group({
    nombre: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]]
  }, {
    validators: (group) => {
      const password = group.get('password')?.value;
      const confirmPassword = group.get('confirmPassword')?.value;
      return password === confirmPassword ? null : { passwordMismatch: true };
    }
  });

  error: string = '';
  success: string = '';
  loading: boolean = false;

  onSubmit() {
    if (this.registerForm.invalid) return;
    this.loading = true;
    this.error = '';
    this.success = '';
    
    const { nombre, email, password } = this.registerForm.value;
    
    this.authService.register({ nombre, email, password }).subscribe({
      next: (res) => {
        this.loading = false;
        this.router.navigate(['/login'], { queryParams: { creado: 'true' } });
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.error || 'Error en el registro';
      }
    });
  }
}
