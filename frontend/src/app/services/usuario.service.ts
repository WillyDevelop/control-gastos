import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface User {
  id?: number;
  email: string;
  nombre: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private apiUrl = `${environment.apiUrl}/usuario`;

  constructor(private http: HttpClient) {}

  actualizarPerfil(datos: { nombre: string }): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/perfil`, datos);
  }

  actualizarSeguridad(datos: any): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/seguridad`, datos);
  }
}
