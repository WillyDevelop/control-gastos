import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  
  currentUser = signal<{nombre: string, email: string} | null>(null);

  constructor(private http: HttpClient, private router: Router) {
    this.checkToken();
  }

  private checkToken() {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');
    const nombre = localStorage.getItem('nombre');
    if (token && email && nombre) {
      this.currentUser.set({ email, nombre });
    }
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('email', res.email);
        localStorage.setItem('nombre', res.nombre);
        this.currentUser.set({ email: res.email, nombre: res.nombre });
        this.router.navigate(['/']);
      })
    );
  }

  register(userData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/registrar`, userData);
  }

  confirmarCuenta(token: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/confirmar?token=${token}`);
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/olvido-password`, { email });
  }

  resetPassword(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/restablecer`, data);
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('nombre');
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  updateUser(data: { email?: string, nombre?: string }) {
    const current = this.currentUser();
    if (!current) return;
    const next = { ...current, ...data };
    this.currentUser.set(next);
    if (data.email) localStorage.setItem('email', data.email);
    if (data.nombre) localStorage.setItem('nombre', data.nombre);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
