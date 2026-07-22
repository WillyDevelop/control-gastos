import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Gasto } from '../models/gasto';
import { ReporteGasto } from '../models/reporte';
import { catchError, tap, map } from 'rxjs/operators';
import { of, Observable, Subject } from 'rxjs';
import { PatrimonioService } from './patrimonio.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GastoService {
  private http = inject(HttpClient);
  private patrimonioService = inject(PatrimonioService);
  private apiUrl = `${environment.apiUrl}/gastos`;

  private abrirModalSubject = new Subject<'GASTO' | 'INGRESO'>();
  abrirModal$ = this.abrirModalSubject.asObservable();

  triggerAbrirModal(tipo: 'GASTO' | 'INGRESO' = 'GASTO') {
    this.abrirModalSubject.next(tipo);
  }

  gastosMes = signal<Gasto[]>([]);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  cargarGastosMes(): void {
    this.loading.set(true);
    this.http.get<Gasto[]>(this.apiUrl)
      .pipe(
        tap(data => {
          this.gastosMes.set(data);
          this.loading.set(false);
          this.error.set(null);
        }),
        catchError(err => {
          this.error.set('Error al cargar gastos del mes');
          this.loading.set(false);
          return of([]);
        })
      ).subscribe();
  }

  registrarGasto(gastoDto: any): Observable<Gasto> {
    return this.http.post<Gasto>(this.apiUrl, gastoDto).pipe(
      tap(() => {
        this.cargarGastosMes();
        this.patrimonioService.cargarPatrimonioActual();
      })
    );
  }

  crearGasto(gastoDto: any): Observable<Gasto> {
    return this.registrarGasto(gastoDto);
  }

  actualizarGasto(id: number, gastoDto: any): Observable<Gasto> {
    return this.http.put<Gasto>(`${this.apiUrl}/${id}`, gastoDto).pipe(
      tap(() => {
        this.cargarGastosMes();
        this.patrimonioService.cargarPatrimonioActual();
      })
    );
  }

  getGastos(): Observable<Gasto[]> {
    return this.http.get<Gasto[]>(this.apiUrl);
  }

  getProximosGastosTarjeta(): Observable<Gasto[]> {
    return this.http.get<Gasto[]>(`${this.apiUrl}/tarjeta/proximos`);
  }

  getHistorialGastosTarjeta(): Observable<Gasto[]> {
    return this.http.get<Gasto[]>(`${this.apiUrl}/tarjeta/historial`);
  }

  pagarResumen(periodo: string, tarjetaId?: number): Observable<void> {
    const periodoFinal = tarjetaId ? `${periodo}-${tarjetaId}` : periodo;
    return this.http.put<void>(`${this.apiUrl}/tarjeta/pagar-resumen?periodo=${periodoFinal}`, {}).pipe(
      tap(() => {
        this.cargarGastosMes();
        this.patrimonioService.cargarPatrimonioActual();
      })
    );
  }

  pagarGasto(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/pagar`, {}).pipe(
      tap(() => {
        this.cargarGastosMes();
        this.patrimonioService.cargarPatrimonioActual();
      })
    );
  }

  getTotalGastos(): Observable<number> {
    return this.getGastos().pipe(
      map(gastos => gastos.reduce((sum, current) => sum + current.monto, 0))
    );
  }

  getCategorias(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/categorias`);
  }

  eliminarGasto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        this.cargarGastosMes();
        this.patrimonioService.cargarPatrimonioActual();
      })
    );
  }

  obtenerReporte(periodo: string): Observable<ReporteGasto[]> {
    return this.http.get<ReporteGasto[]>(`${this.apiUrl}/reporte?periodo=${periodo}`);
  }
}
