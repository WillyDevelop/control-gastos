import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';

const passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const nueva = control.get('nuevaPassword');
  const confirmar = control.get('confirmarPassword');
  if (!nueva || !confirmar) return null;
  return nueva.value !== confirmar.value ? { passwordMismatch: true } : null;
};

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './reset-password.component.html'
})
export class ResetPasswordComponent implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  token: string | null = null;
  error: string = '';
  success: string = '';
  loading: boolean = false;

  resetForm = this.fb.group({
    nuevaPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmarPassword: ['', [Validators.required]]
  }, { validators: passwordMatchValidator });

  ngOnInit() {
    this.token = this.route.snapshot.queryParamMap.get('token');
    if (!this.token) {
      this.error = 'Token inválido o faltante';
    }
  }

  onSubmit() {
    if (this.resetForm.invalid || !this.token) return;
    this.loading = true;
    this.error = '';
    this.success = '';
    
    const payload = {
      token: this.token,
      nuevaPassword: this.resetForm.value.nuevaPassword,
      confirmarPassword: this.resetForm.value.confirmarPassword
    };

    this.authService.resetPassword(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.success = res.message || 'Contraseña restablecida correctamente.';
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.error || 'Error al restablecer la contraseña';
      }
    });
  }
}
