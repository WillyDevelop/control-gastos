import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface AdminUsuario {
  id: number;
  email: string;
  nombre: string;
  activo: boolean;
  rol: string;
  totalGastos: number;
  totalTarjetas: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin/usuarios`;

  constructor(private http: HttpClient) {}

  obtenerUsuarios(): Observable<AdminUsuario[]> {
    return this.http.get<AdminUsuario[]>(this.apiUrl);
  }

  toggleEstadoUsuario(id: number): Observable<AdminUsuario> {
    return this.http.patch<AdminUsuario>(`${this.apiUrl}/${id}/toggle-activo`, {});
  }

  eliminarUsuario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
