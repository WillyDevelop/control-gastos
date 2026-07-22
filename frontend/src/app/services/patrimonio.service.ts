import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Patrimonio } from '../models/patrimonio';
import { catchError, tap } from 'rxjs/operators';
import { of, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PatrimonioService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/patrimonio`;

  patrimonio = signal<Patrimonio | null>(null);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  cargarPatrimonioActual(): void {
    this.loading.set(true);
    this.http.get<Patrimonio>(`${this.apiUrl}/actual`)
      .pipe(
        tap(data => {
          this.patrimonio.set(data);
          this.loading.set(false);
          this.error.set(null);
        }),
        catchError(err => {
          this.error.set('Error al cargar patrimonio');
          this.loading.set(false);
          return of(null);
        })
      ).subscribe();
  }

  actualizarIngreso(ingresoTotal: number, fechaIngreso?: string): Observable<any> {
    const payload: any = { ingresoTotal };
    if (fechaIngreso) {
      payload.fechaIngreso = fechaIngreso;
    }
    return this.http.put(`${this.apiUrl}/actual`, payload).pipe(
      tap(() => this.cargarPatrimonioActual())
    );
  }
}
