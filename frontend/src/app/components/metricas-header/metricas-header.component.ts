import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-metricas-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
      <div class="bg-gray-800/50 backdrop-blur-xl border border-gray-700 p-6 rounded-3xl shadow-xl transform hover:-translate-y-2 transition-all duration-300">
        <div class="flex items-center gap-4 mb-4">
          <div class="p-3 bg-emerald-500/20 text-emerald-400 rounded-2xl">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          </div>
          <div>
            <p class="text-gray-400 text-sm font-medium">Patrimonio Actual</p>
            <h2 class="text-3xl font-bold text-emerald-400">\${{ saldoActual }}</h2>
          </div>
        </div>
      </div>
      <div class="bg-gray-800/50 backdrop-blur-xl border border-gray-700 p-6 rounded-3xl shadow-xl transform hover:-translate-y-2 transition-all duration-300">
        <div class="flex items-center gap-4 mb-4">
          <div class="p-3 bg-red-500/20 text-red-400 rounded-2xl">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 17h8m0 0V9m0 8l-8-8-4 4-6-6"></path></svg>
          </div>
          <div>
            <p class="text-gray-400 text-sm font-medium">Gastos del Mes</p>
            <h2 class="text-3xl font-bold text-red-400">\${{ totalPagado }}</h2>
          </div>
        </div>
      </div>
      <div class="bg-gray-800/50 backdrop-blur-xl border border-gray-700 p-6 rounded-3xl shadow-xl transform hover:-translate-y-2 transition-all duration-300">
        <div class="flex items-center gap-4 mb-4">
          <div class="p-3 bg-blue-500/20 text-blue-400 rounded-2xl">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          </div>
          <div>
            <p class="text-gray-400 text-sm font-medium">Ingresos Totales</p>
            <h2 class="text-3xl font-bold text-blue-400">\${{ ingresoTotal }}</h2>
          </div>
        </div>
      </div>
    </div>
  `
})
export class MetricasHeaderComponent {
  @Input() saldoActual: number = 0;
  @Input() totalPagado: number = 0;
  @Input() ingresoTotal: number = 0;
}
