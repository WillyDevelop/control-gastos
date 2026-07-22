import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Categoria } from '../models/categoria';
import { catchError, tap } from 'rxjs/operators';
import { of, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/categorias';

  categorias = signal<Categoria[]>([]);
  presupuestos = signal<any[]>([]);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  cargarCategorias(): void {
    this.loading.set(true);
    this.http.get<Categoria[]>(this.apiUrl)
      .pipe(
        tap(data => {
          this.categorias.set(data);
          this.loading.set(false);
          this.error.set(null);
        }),
        catchError(err => {
          this.error.set('Error al cargar categorías');
          this.loading.set(false);
          return of([]);
        })
      ).subscribe();
  }

  cargarPresupuestos(): void {
    this.http.get<any[]>(`${this.apiUrl}/presupuestos`)
      .pipe(
        tap(data => {
          this.presupuestos.set(data);
        }),
        catchError(err => {
          console.error('Error al cargar presupuestos', err);
          return of([]);
        })
      ).subscribe();
  }

  crearCategoria(categoria: any): Observable<Categoria> {
    return this.http.post<Categoria>(this.apiUrl, categoria).pipe(
      tap(() => {
        this.cargarCategorias();
        this.cargarPresupuestos();
      })
    );
  }

  actualizarCategoria(id: number, categoria: any): Observable<Categoria> {
    return this.http.put<Categoria>(`${this.apiUrl}/${id}`, categoria).pipe(
      tap(() => {
        this.cargarCategorias();
        this.cargarPresupuestos();
      })
    );
  }

  eliminarCategoria(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        this.cargarCategorias();
        this.cargarPresupuestos();
      })
    );
  }
}
