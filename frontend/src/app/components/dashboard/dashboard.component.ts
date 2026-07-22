import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GastoService } from '../../services/gasto.service';
import { PatrimonioService } from '../../services/patrimonio.service';
import { Gasto } from '../../models/gasto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: []
})
export class DashboardComponent implements OnInit {
  gastos: Gasto[] = [];
  totalGastado: number = 0;

  constructor(
    private gastoService: GastoService,
    public patrimonioService: PatrimonioService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.patrimonioService.cargarPatrimonioActual();
    
    this.gastoService.getGastos().subscribe({
      next: (data) => this.gastos = data,
      error: (err) => console.error('Error al cargar gastos', err)
    });
    
    this.gastoService.getTotalGastos().subscribe({
      next: (total) => this.totalGastado = total || 0,
      error: (err) => console.error('Error al cargar el total', err)
    });
  }

  eliminar(id: number): void {
    if (confirm('¿Estás seguro de que quieres eliminar este gasto?')) {
      this.gastoService.eliminarGasto(id).subscribe({
        next: () => this.cargarDatos(),
        error: (err) => console.error('Error al eliminar el gasto', err)
      });
    }
  }
}
