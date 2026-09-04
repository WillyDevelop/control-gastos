import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { ThemeService } from '../../../services/theme.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  public themeService = inject(ThemeService);
  private route = inject(ActivatedRoute);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  error: string = '';
  successMessage: string = '';
  loading: boolean = false;
  showPassword: boolean = false;

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['creado'] === 'true') {
        this.successMessage = '¡Cuenta creada con éxito!';
      }
    });
  }

  toggleShowPassword() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    if (this.loginForm.invalid) return;
    this.loading = true;
    this.error = '';
    
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 0 || err.status === 502 || err.status === 503 || err.status === 504) {
          this.error = 'El servidor se está iniciando o no responde (Render tarda ~1 minuto al despertar). Por favor espera un momento y reintenta.';
        } else if (err.status === 401 || err.status === 400) {
          this.error = err.error?.error || 'Credenciales incorrectas o usuario no registrado.';
        } else {
          this.error = err.error?.error || 'Error al iniciar sesión. Intenta nuevamente.';
        }
      }
    });
  }
}

