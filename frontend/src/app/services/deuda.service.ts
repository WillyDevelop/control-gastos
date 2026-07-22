import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, tap } from 'rxjs/operators';
import { of, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DeudaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/deudas`;

  deudas = signal<any[]>([]);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  limpiarEstado(): void {
    this.deudas.set([]);
    this.error.set(null);
  }

  cargarDeudasPorCobrar(): void {
    this.loading.set(true);
    this.http.get<any[]>(`${this.apiUrl}/por-cobrar`)
      .pipe(
        tap(data => {
          this.deudas.set(data);
          this.loading.set(false);
          this.error.set(null);
        }),
        catchError(err => {
          this.error.set('Error al cargar deudas');
          this.loading.set(false);
          return of([]);
        })
      ).subscribe();
  }

  liquidar(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/liquidar`, {}).pipe(
      tap(() => this.cargarDeudasPorCobrar())
    );
  }
}
