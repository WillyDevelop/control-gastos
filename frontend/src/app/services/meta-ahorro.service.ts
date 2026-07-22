import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, tap } from 'rxjs/operators';
import { of, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MetaAhorroService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/metas`;

  metas = signal<any[]>([]);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  cargarMetas(): void {
    this.loading.set(true);
    this.http.get<any[]>(this.apiUrl)
      .pipe(
        tap(data => {
          this.metas.set(data);
          this.loading.set(false);
          this.error.set(null);
        }),
        catchError(err => {
          this.error.set('Error al cargar metas de ahorro');
          this.loading.set(false);
          return of([]);
        })
      ).subscribe();
  }

  crearMeta(meta: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, meta).pipe(
      tap(() => this.cargarMetas())
    );
  }

  activarMeta(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/activar`, {}).pipe(
      tap(() => this.cargarMetas())
    );
  }

  getMetaActiva(): any {
    return this.metas().find(m => m.activaParaRedondeo) || null;
  }
}
