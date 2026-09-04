import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, AdminUsuario } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  usuarios = signal<AdminUsuario[]>([]);
  cargando = signal<boolean>(true);
  errorMensaje = signal<string | null>(null);
  exitoMensaje = signal<string | null>(null);
  filtroTexto = signal<string>('');

  // Dropdown de acción activo
  openActionMenuId = signal<number | null>(null);

  // Modal de confirmación para eliminar
  usuarioAEliminar = signal<AdminUsuario | null>(null);
  procesandoEliminacion = signal<boolean>(false);

  constructor(
    private adminService: AdminService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.cargando.set(true);
    this.errorMensaje.set(null);
    this.adminService.obtenerUsuarios().subscribe({
      next: (data) => {
        this.usuarios.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.errorMensaje.set(err.error?.message || 'Error al cargar los usuarios.');
        this.cargando.set(false);
      }
    });
  }

  get usuariosFiltrados(): AdminUsuario[] {
    const query = this.filtroTexto().toLowerCase().trim();
    if (!query) return this.usuarios();
    return this.usuarios().filter(u =>
      u.nombre?.toLowerCase().includes(query) ||
      u.email?.toLowerCase().includes(query) ||
      u.rol?.toLowerCase().includes(query)
    );
  }

  get totalUsuarios(): number { return this.usuarios().length; }
  get totalActivos(): number { return this.usuarios().filter(u => u.activo).length; }
  get totalSuspendidos(): number { return this.usuarios().filter(u => !u.activo).length; }
  get totalAdmins(): number { return this.usuarios().filter(u => u.rol === 'ROLE_ADMIN').length; }

  toggleActionMenu(id: number, event: Event): void {
    event.stopPropagation();
    if (this.openActionMenuId() === id) {
      this.openActionMenuId.set(null);
    } else {
      this.openActionMenuId.set(id);
    }
  }

  closeActionMenu(): void {
    this.openActionMenuId.set(null);
  }

  toggleEstado(usuario: AdminUsuario, event: Event): void {
    event.stopPropagation();
    this.closeActionMenu();
    this.errorMensaje.set(null);
    this.exitoMensaje.set(null);

    this.adminService.toggleEstadoUsuario(usuario.id).subscribe({
      next: (actualizado) => {
        this.usuarios.update(lista =>
          lista.map(u => u.id === actualizado.id ? actualizado : u)
        );
        const accion = actualizado.activo ? 'activada' : 'suspendida';
        this.exitoMensaje.set(`La cuenta de ${actualizado.email} ha sido ${accion} con éxito.`);
        setTimeout(() => this.exitoMensaje.set(null), 4000);
      },
      error: (err) => {
        this.errorMensaje.set(err.error?.message || 'Error al cambiar el estado del usuario.');
        setTimeout(() => this.errorMensaje.set(null), 5000);
      }
    });
  }

  confirmarEliminar(usuario: AdminUsuario, event: Event): void {
    event.stopPropagation();
    this.closeActionMenu();
    this.usuarioAEliminar.set(usuario);
  }

  cancelarEliminar(): void {
    this.usuarioAEliminar.set(null);
  }

  eliminarDefinitivo(): void {
    const target = this.usuarioAEliminar();
    if (!target) return;

    this.procesandoEliminacion.set(true);
    this.adminService.eliminarUsuario(target.id).subscribe({
      next: () => {
        this.usuarios.update(lista => lista.filter(u => u.id !== target.id));
        this.usuarioAEliminar.set(null);
        this.procesandoEliminacion.set(false);
        this.exitoMensaje.set(`El usuario ${target.email} ha sido eliminado definitivamente.`);
        setTimeout(() => this.exitoMensaje.set(null), 4000);
      },
      error: (err) => {
        this.usuarioAEliminar.set(null);
        this.procesandoEliminacion.set(false);
        this.errorMensaje.set(err.error?.message || 'Error al eliminar el usuario.');
        setTimeout(() => this.errorMensaje.set(null), 5000);
      }
    });
  }
}
