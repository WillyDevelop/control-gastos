import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface PlantillaGasto {
  id?: number;
  nombrePlantilla: string;
  descripcion: string;
  monto: number;
  categoriaId: number;
  metodoPago: string;
}

export interface PlantillaGastoResponse {
  id: number;
  nombrePlantilla: string;
  descripcion: string;
  monto: number;
  categoria: any;
  metodoPago: string;
}

@Injectable({
  providedIn: 'root'
})
export class PlantillaGastoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/plantillas`;

  plantillas = signal<PlantillaGastoResponse[]>([]);

  limpiarEstado(): void {
    this.plantillas.set([]);
  }

  cargarPlantillas(): void {
    this.http.get<PlantillaGastoResponse[]>(this.apiUrl)
      .subscribe(data => this.plantillas.set(data));
  }

  crearPlantilla(dto: PlantillaGasto): Observable<PlantillaGastoResponse> {
    return this.http.post<PlantillaGastoResponse>(this.apiUrl, dto).pipe(
      tap(() => this.cargarPlantillas())
    );
  }

  eliminarPlantilla(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.cargarPlantillas())
    );
  }
}
