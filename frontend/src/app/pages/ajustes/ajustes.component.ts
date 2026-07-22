import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { UsuarioService } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';
import { CategoriaService } from '../../services/categoria.service';

@Component({
  selector: 'app-ajustes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './ajustes.component.html'
})
export class AjustesComponent implements OnInit {
  perfilForm!: FormGroup;
  seguridadForm!: FormGroup;

  perfilMensaje = '';
  perfilError = '';

  seguridadMensaje = '';
  seguridadError = '';

  // Tab signal for settings page navigation
  seccionActiva = signal<'perfil' | 'seguridad' | 'categorias'>('perfil');

  // Signal to track if currentPassword is required
  requiresCurrentPassword = signal(false);

  // Category management
  showGestionCategoriasModal = false;
  showNuevaCategoriaModal = false;
  categoriaEnEdicion: any = null;
  nuevaCategoria: any = { nombre: '', tipo: 'GASTO', limiteMensual: null };

  private fb = inject(FormBuilder);
  private usuarioService = inject(UsuarioService);
  authService = inject(AuthService);
  categoriaService = inject(CategoriaService);

  ngOnInit() {
    const currentUser = this.authService.currentUser();
    this.categoriaService.cargarCategorias();

    this.perfilForm = this.fb.group({
      nombre: [currentUser?.nombre || '', [Validators.required]]
    });

    this.seguridadForm = this.fb.group({
      newEmail: [currentUser?.email || '', [Validators.email]],
      newPassword: [''],
      confirmPassword: [''],
      currentPassword: ['']
    });

    this.seguridadForm.valueChanges.subscribe(values => {
      const isEmailChanged = values.newEmail !== currentUser?.email;
      const isPasswordChanged = !!values.newPassword;
      const needed = isEmailChanged || isPasswordChanged;

      this.requiresCurrentPassword.set(needed);

      const currentPasswordControl = this.seguridadForm.get('currentPassword');
      if (needed) {
        currentPasswordControl?.setValidators([Validators.required]);
      } else {
        currentPasswordControl?.clearValidators();
      }
      currentPasswordControl?.updateValueAndValidity({ emitEvent: false });
    });
  }

  guardarPerfil() {
    if (this.perfilForm.invalid) return;
    this.perfilMensaje = '';
    this.perfilError = '';

    this.usuarioService.actualizarPerfil(this.perfilForm.value).subscribe({
      next: (user) => {
        this.authService.updateUser({ nombre: user.nombre });
        this.perfilMensaje = 'Perfil actualizado correctamente.';
        setTimeout(() => this.perfilMensaje = '', 3000);
      },
      error: () => {
        this.perfilError = 'Error al actualizar el perfil.';
      }
    });
  }

  guardarSeguridad() {
    if (this.seguridadForm.invalid) return;
    const values = this.seguridadForm.value;

    if (values.newPassword && values.newPassword !== values.confirmPassword) {
      this.seguridadError = 'Las contraseñas no coinciden.';
      return;
    }

    this.seguridadMensaje = '';
    this.seguridadError = '';

    const currentUser = this.authService.currentUser();
    const isEmailChanged = values.newEmail !== currentUser?.email;
    const isPasswordChanged = !!values.newPassword;

    if (!isEmailChanged && !isPasswordChanged) {
      this.seguridadMensaje = 'No hay cambios para guardar.';
      return;
    }

    this.usuarioService.actualizarSeguridad(values).subscribe({
      next: () => {
        this.seguridadMensaje = isEmailChanged
          ? 'Se ha enviado un correo para confirmar el cambio de email.'
          : 'Seguridad actualizada correctamente.';
        this.seguridadForm.patchValue({ newPassword: '', confirmPassword: '', currentPassword: '' });
        setTimeout(() => this.seguridadMensaje = '', 4000);
      },
      error: (err) => {
        this.seguridadError = err.error?.message || 'Error al actualizar la seguridad. Verifica tu contraseña actual.';
      }
    });
  }

  // ---- Gestión de Categorías ----
  abrirGestionCategorias() {
    this.showGestionCategoriasModal = true;
  }

  cerrarGestionCategorias() {
    this.showGestionCategoriasModal = false;
    this.categoriaEnEdicion = null;
  }

  iniciarEdicionCategoria(cat: any) {
    this.categoriaEnEdicion = { ...cat };
  }

  cancelarEdicionCategoria() {
    this.categoriaEnEdicion = null;
  }

  guardarCategoriaEditada() {
    if (!this.categoriaEnEdicion) return;
    this.categoriaService.actualizarCategoria(this.categoriaEnEdicion.id, this.categoriaEnEdicion).subscribe({
      next: () => { this.categoriaEnEdicion = null; },
      error: () => alert('Error al actualizar la categoría')
    });
  }

  eliminarCategoria(id: number) {
    if (!confirm('¿Estás seguro de eliminar esta categoría?')) return;
    this.categoriaService.eliminarCategoria(id).subscribe({
      error: () => alert('No se puede eliminar la categoría porque está siendo usada por uno o más movimientos.')
    });
  }

  abrirNuevaCategoriaModal() {
    this.nuevaCategoria = { nombre: '', tipo: 'GASTO', limiteMensual: null };
    this.showNuevaCategoriaModal = true;
  }

  cerrarNuevaCategoriaModal() {
    this.showNuevaCategoriaModal = false;
  }

  guardarNuevaCategoria() {
    if (!this.nuevaCategoria.nombre) return;
    this.categoriaService.crearCategoria(this.nuevaCategoria).subscribe({
      next: () => { this.cerrarNuevaCategoriaModal(); },
      error: () => alert('Error al crear la categoría')
    });
  }
}
