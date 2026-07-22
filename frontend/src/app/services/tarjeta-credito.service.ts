import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { TarjetaCredito } from '../models/tarjeta-credito';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TarjetaCreditoService {

  private apiUrl = `${environment.apiUrl}/tarjetas`;
  
  tarjetas = signal<TarjetaCredito[]>([]);

  limpiarEstado(): void {
    this.tarjetas.set([]);
  }

  constructor(private http: HttpClient) { }

  cargarTarjetas(): void {
    this.http.get<TarjetaCredito[]>(this.apiUrl).subscribe(data => {
      this.tarjetas.set(data);
    });
  }

  getTarjetas(): Observable<TarjetaCredito[]> {
    return this.http.get<TarjetaCredito[]>(this.apiUrl).pipe(
      tap(data => this.tarjetas.set(data))
    );
  }

  crearTarjeta(tarjeta: TarjetaCredito): Observable<TarjetaCredito> {
    return this.http.post<TarjetaCredito>(this.apiUrl, tarjeta).pipe(
      tap(() => this.cargarTarjetas())
    );
  }

  eliminarTarjeta(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.cargarTarjetas())
    );
  }

  actualizarTarjeta(id: number, tarjeta: TarjetaCredito): Observable<TarjetaCredito> {
    return this.http.put<TarjetaCredito>(`${this.apiUrl}/${id}`, tarjeta).pipe(
      tap(() => this.cargarTarjetas())
    );
  }
}
