import { Component, inject, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';
import { ChartOptions, ChartData } from 'chart.js';
import { MetricasHeaderComponent } from '../../components/metricas-header/metricas-header.component';
import { PatrimonioService } from '../../services/patrimonio.service';
import { GastoService } from '../../services/gasto.service';
import { CategoriaService } from '../../services/categoria.service';
import { MetaAhorroService } from '../../services/meta-ahorro.service';
import { DeudaService } from '../../services/deuda.service';
import { TarjetaCreditoService } from '../../services/tarjeta-credito.service';
import { ThemeService } from '../../services/theme.service';

import { RouterModule, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, NgChartsModule, MetricasHeaderComponent, RouterModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  patrimonioService = inject(PatrimonioService);
  gastoService = inject(GastoService);
  categoriaService = inject(CategoriaService);
  metaAhorroService = inject(MetaAhorroService);
  deudaService = inject(DeudaService);
  tarjetaCreditoService = inject(TarjetaCreditoService);
  themeService = inject(ThemeService);
  private route = inject(ActivatedRoute);

  showModal = false;
  tipoTransaccion: 'INGRESO' | 'GASTO' = 'GASTO';
  showCategoriaModal = false;
  showMetaModal = false;
  showGestionCategoriasModal = false;
  showNuevaTarjetaModal = false;
  nuevaTarjetaTemp: any = { nombre: '', diaCierre: 27, diaVencimiento: 10 };
  
  categoriaEnEdicion: any = null;
  
  nuevaMeta: any = {
    nombre: '',
    montoObjetivo: null
  };
  
  mostrandoInputIngreso = false;
  nuevoIngresoRapido: any = { descripcion: '', monto: null };

  toggleInputIngreso() {
    this.mostrandoInputIngreso = !this.mostrandoInputIngreso;
    this.nuevoIngresoRapido = { descripcion: '', monto: null };
  }

  showConfirmModal = false;
  confirmConfig: { title: string, message: string, onConfirm: () => void, isAlert: boolean } = {
    title: '', message: '', onConfirm: () => {}, isAlert: false
  };

  openConfirm(title: string, message: string, onConfirm: () => void) {
    this.confirmConfig = { title, message, onConfirm, isAlert: false };
    this.showConfirmModal = true;
  }

  openAlert(title: string, message: string) {
    this.confirmConfig = { title, message, onConfirm: () => {}, isAlert: true };
    this.showConfirmModal = true;
  }

  closeConfirm() {
    this.showConfirmModal = false;
  }

  executeConfirm() {
    if (this.confirmConfig.onConfirm && !this.confirmConfig.isAlert) {
      this.confirmConfig.onConfirm();
    }
    this.closeConfirm();
  }

  guardarIngresoRapido() {
    if (!this.nuevoIngresoRapido.descripcion || !this.nuevoIngresoRapido.monto) return;
    
    if (this.nuevoIngresoRapido.monto > 99999999.99) {
      this.openAlert('Monto excedido', 'El monto máximo permitido para una transacción es $99.999.999,99.');
      return;
    }
    
    let catIngreso = this.categoriaService.categorias().find(c => c.tipo === 'INGRESO');
    if (catIngreso && catIngreso.id) {
      this.enviarIngresoRapido(catIngreso.id);
    } else {
      this.categoriaService.crearCategoria({
        nombre: 'Sueldo / Ingresos',
        tipo: 'INGRESO',
        icono: '',
        limiteMensual: null
      }).subscribe({
        next: (nuevaCat) => {
          this.enviarIngresoRapido(nuevaCat.id!);
        },
        error: (err) => {
          console.error('Error creando categoría de ingreso:', err);
          this.openAlert('Categoría Requerida', 'Debes crear una categoría de tipo Ingreso antes de guardar.');
        }
      });
    }
  }

  private enviarIngresoRapido(categoriaId: number) {
    this.gastoService.registrarGasto({
      descripcion: this.nuevoIngresoRapido.descripcion,
      monto: this.nuevoIngresoRapido.monto,
      fecha: new Date().toISOString().split('T')[0],
      categoriaId: categoriaId,
      metodoPago: 'EFECTIVO',
      entidadPago: '',
      esRecurrente: false,
      pagado: true,
      notas: ''
    }).subscribe({
      next: () => {
        this.patrimonioService.cargarPatrimonioActual();
        this.gastoService.cargarGastosMes();
        this.toggleInputIngreso();
      },
      error: (err) => {
        console.error('Error guardando ingreso:', err);
        this.openAlert('Error al guardar', 'No se pudo guardar el ingreso. Verifica tu conexión.');
      }
    });
  }
  
  nuevoGasto: any = {
    descripcion: '',
    monto: null,
    fecha: new Date().toISOString().split('T')[0],
    categoriaId: null,
    metodoPago: 'EFECTIVO',
    entidadPago: '',
    tarjetaCreditoId: null,
    dividirGasto: false,
    emailDeudor: '',
    porcentajeDeuda: 50
  };

  nuevaCategoria: any = {
    nombre: '',
    tipo: 'GASTO',
    limiteMensual: null
  };

  categoriasFiltradas = computed(() => {
    return this.categoriaService.categorias().filter(c => c.tipo === this.tipoTransaccion);
  });

  historialIngresos = computed(() => {
    return this.gastoService.gastosMes().filter(g => g.categoria?.tipo === 'INGRESO');
  });

  activeChart = signal<'DIARIO' | 'TENDENCIA' | 'CATEGORIAS' | 'COMPARATIVA' | 'RADAR'>('COMPARATIVA');
  showChartSelector = false;

  activeChartType = computed((): any => {
    switch(this.activeChart()) {
      case 'DIARIO': return 'bar';
      case 'TENDENCIA': return 'line';
      case 'CATEGORIAS': return 'polarArea';
      case 'COMPARATIVA': return 'bar';
      case 'RADAR': return 'radar';
    }
  });

  chartLabels = computed(() => {
    return Array.from({length: new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).getDate()}, (_, i) => (i + 1).toString());
  });

  gastosPorDia = computed(() => {
    const map = new Map<string, number>();
    this.gastoService.gastosMes().forEach(g => {
      if (g.categoria?.tipo === 'GASTO') {
        const d = new Date(g.fecha).getDate().toString();
        map.set(d, (map.get(d) || 0) + g.monto);
      }
    });
    return this.chartLabels().map(d => map.get(d) || 0);
  });

  ingresosPorDia = computed(() => {
    const map = new Map<string, number>();
    this.gastoService.gastosMes().forEach(g => {
      if (g.categoria?.tipo === 'INGRESO') {
        const d = new Date(g.fecha).getDate().toString();
        map.set(d, (map.get(d) || 0) + g.monto);
      }
    });
    return this.chartLabels().map(d => map.get(d) || 0);
  });

  gastosAcumulados = computed(() => {
    let acc = 0;
    return this.gastosPorDia().map(monto => {
      acc += monto;
      return acc;
    });
  });

  gastosPorCategoriaAgrupados = computed(() => {
    const map = new Map<string, number>();
    this.gastoService.gastosMes().forEach(g => {
      if (g.categoria?.tipo === 'GASTO') {
        const cat = g.categoria?.nombre || 'Otros';
        map.set(cat, (map.get(cat) || 0) + g.monto);
      }
    });
    return map;
  });

  chartData = computed((): ChartData<any> => {
    switch(this.activeChart()) {
      case 'DIARIO':
        return {
          labels: this.chartLabels(),
          datasets: [{
            type: 'bar', label: 'Gastos Diarios', data: this.gastosPorDia(),
            backgroundColor: '#F43F5E', borderRadius: 4, barPercentage: 0.6
          }]
        };
      case 'TENDENCIA':
        return {
          labels: this.chartLabels(),
          datasets: [{
            type: 'line', label: 'Gasto Acumulado', data: this.gastosAcumulados(),
            borderColor: '#F43F5E', backgroundColor: 'rgba(244, 63, 94, 0.1)',
            fill: true, tension: 0.4, pointRadius: 2, borderWidth: 3
          }]
        };
      case 'CATEGORIAS':
        const catMap = this.gastosPorCategoriaAgrupados();
        return {
          labels: Array.from(catMap.keys()),
          datasets: [{
            type: 'polarArea', label: 'Gastos por Categoría',
            data: Array.from(catMap.values()),
            backgroundColor: ['rgba(15, 23, 42, 0.7)', 'rgba(99, 102, 241, 0.7)', 'rgba(20, 184, 166, 0.7)', 'rgba(244, 63, 94, 0.7)', 'rgba(109, 40, 217, 0.7)'],
            borderWidth: 1
          }]
        };
      case 'COMPARATIVA':
        return {
          labels: this.chartLabels(),
          datasets: [
            {
              type: 'bar', label: 'Salidas', data: this.gastosPorDia(),
              backgroundColor: '#F43F5E', borderRadius: 4, barPercentage: 0.5
            },
            {
              type: 'bar', label: 'Entradas', data: this.ingresosPorDia(),
              backgroundColor: '#14B8A6', borderRadius: 4, barPercentage: 0.5
            }
          ]
        };
      case 'RADAR':
        const radarMap = this.gastosPorCategoriaAgrupados();
        return {
          labels: Array.from(radarMap.keys()),
          datasets: [{
            type: 'radar', label: 'Distribución', data: Array.from(radarMap.values()),
            backgroundColor: 'rgba(99, 102, 241, 0.2)', borderColor: '#6366F1',
            pointBackgroundColor: '#6366F1', pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff', pointHoverBorderColor: '#6366F1',
            fill: true
          }]
        };
    }
  });

  chartOptions = computed((): ChartOptions<any> => {
    const isDark = this.themeService.isDarkMode();
    const textColor = isDark ? '#8f9099' : '#64748B'; // Custom slate-400 or slate-500
    const gridColor = isDark ? '#1f1f24' : '#E2E8F0'; // Custom slate-700 or slate-200
    const baseOptions: any = {
      responsive: true, maintainAspectRatio: false,
      plugins: {
        legend: { display: true, position: 'top', labels: { color: textColor, font: { family: 'Inter', size: 12 }, usePointStyle: true, boxWidth: 8 } },
        tooltip: { backgroundColor: isDark ? '#121214' : '#0F172A', titleFont: { family: 'Inter', size: 13 }, bodyFont: { family: 'Inter', size: 13, weight: 'bold' }, padding: 12, cornerRadius: 8 }
      }
    };
    
    if (this.activeChart() === 'CATEGORIAS' || this.activeChart() === 'RADAR') {
      if (this.activeChart() === 'RADAR') {
        baseOptions.scales = {
          r: {
            grid: { color: gridColor },
            angleLines: { color: gridColor },
            pointLabels: { color: textColor, font: { family: 'Inter', size: 11 } },
            ticks: { display: false }
          }
        };
      } else if (this.activeChart() === 'CATEGORIAS') {
        baseOptions.scales = {
          r: {
            grid: { color: gridColor },
            ticks: { color: textColor, backdropColor: 'transparent', font: { family: 'Inter', size: 9 } }
          }
        };
      }
      return baseOptions;
    }

    return {
      ...baseOptions,
      scales: {
        x: { grid: { display: false }, ticks: { color: textColor, font: { family: 'Inter', size: 11 } } },
        y: { beginAtZero: true, border: { dash: [4, 4], display: false }, grid: { color: gridColor }, ticks: { color: textColor, font: { family: 'Inter', size: 11 } } }
      }
    };
  });

  donutOptions = computed((): ChartOptions<'doughnut'> => {
    const isDark = this.themeService.isDarkMode();
    return {
      responsive: true,
      maintainAspectRatio: false,
      cutout: '75%',
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: isDark ? '#121214' : '#0F172A',
          bodyFont: { family: 'Inter', size: 13 },
          padding: 10,
          cornerRadius: 8
        }
      }
    };
  });

  donutPagosData = computed((): ChartData<'doughnut'> => {
    const salidas = this.gastoService.gastosMes().filter(g => g.categoria?.tipo === 'GASTO');
    const grouped = salidas.reduce((acc: any, g) => {
      const cat = g.categoria?.nombre || 'Otros';
      acc[cat] = (acc[cat] || 0) + g.monto;
      return acc;
    }, {});
    return {
      labels: Object.keys(grouped),
      datasets: [{
        data: Object.values(grouped),
        backgroundColor: ['#38BDF8', '#6366F1', '#14B8A6', '#F43F5E', '#6D28D9', '#64748B'],
        borderWidth: 0
      }]
    };
  });

  ngOnInit() {
    this.patrimonioService.cargarPatrimonioActual();
    this.gastoService.cargarGastosMes();
    this.categoriaService.cargarCategorias();
    this.categoriaService.cargarPresupuestos();
    this.metaAhorroService.cargarMetas();
    this.tarjetaCreditoService.cargarTarjetas();

    this.gastoService.abrirModal$.subscribe(tipo => {
      this.abrirModalTransaccion(tipo);
    });

    this.route.queryParams.subscribe(params => {
      if (params['action'] === 'nuevoGasto') {
        this.abrirModalTransaccion('GASTO');
      }
    });
  }

  eliminarTransaccion(id: number) {
    this.openConfirm('Eliminar Registro', '¿Estás seguro de eliminar este registro?', () => {
      this.gastoService.eliminarGasto(id).subscribe(() => {
        this.patrimonioService.cargarPatrimonioActual();
      });
    });
  }

  get mensajeRedondeo(): string {
    if (!this.nuevoGasto.monto || this.nuevoGasto.monto <= 0) return '';
    if (this.nuevoGasto.metodoPago === 'TARJETA_CREDITO') return '';
    const metaActiva = this.metaAhorroService.getMetaActiva();
    if (!metaActiva) return '';

    const monto = this.nuevoGasto.monto;
    const redondeo = Math.ceil(monto / 1000) * 1000;
    const diferencia = redondeo - monto;

    if (diferencia > 0) {
      return `¡Se aplicará un redondeo automático de $${diferencia.toFixed(2)} a tu meta activa (${metaActiva.nombre})!`;
    }
    return '';
  }

  abrirMetaModal() {
    this.showMetaModal = true;
  }

  cerrarMetaModal() {
    this.showMetaModal = false;
    this.nuevaMeta = { nombre: '', montoObjetivo: null };
  }

  guardarMeta() {
    if (!this.nuevaMeta.nombre || !this.nuevaMeta.montoObjetivo) return;
    if (this.nuevaMeta.montoObjetivo > 9999999999.99) {
      this.openAlert('Monto excedido', 'El monto objetivo de la meta no puede superar los $9.999.999.999,99.');
      return;
    }
    this.metaAhorroService.crearMeta(this.nuevaMeta).subscribe(() => {
      this.cerrarMetaModal();
    });
  }

  activarMeta(id: number) {
    this.metaAhorroService.activarMeta(id).subscribe();
  }

  abrirModalTransaccion(tipo: 'INGRESO' | 'GASTO') {
    this.tipoTransaccion = tipo;
    this.nuevaCategoria.tipo = tipo; // Por si quiere crear una
    this.showModal = true;
    
    // Auto-select primera categoria si existe
    setTimeout(() => {
      const cats = this.categoriasFiltradas();
      if (cats.length > 0) {
        this.nuevoGasto.categoriaId = cats[0].id;
      }
    });
  }

  cerrarModal() {
    this.showModal = false;
    this.resetForm();
  }

  // Gestion Categorias Modal
  abrirGestionCategorias() {
    this.showGestionCategoriasModal = true;
  }

  cerrarGestionCategorias() {
    this.showGestionCategoriasModal = false;
    this.categoriaEnEdicion = null;
  }

  iniciarEdicionCategoria(cat: any) {
    this.categoriaEnEdicion = { ...cat };
  }

  cancelarEdicionCategoria() {
    this.categoriaEnEdicion = null;
  }

  guardarCategoriaEditada() {
    if (this.categoriaEnEdicion) {
      this.categoriaService.actualizarCategoria(this.categoriaEnEdicion.id, this.categoriaEnEdicion).subscribe({
        next: () => {
          this.categoriaEnEdicion = null;
        },
        error: () => this.openAlert('Error', 'Error al actualizar la categoría')
      });
    }
  }

  eliminarCategoria(id: number) {
    this.openConfirm('Eliminar Categoría', '¿Estás seguro de eliminar esta categoría? Si tiene gastos asociados, no se podrá eliminar.', () => {
      this.categoriaService.eliminarCategoria(id).subscribe({
        error: (err) => {
          this.openAlert('Error', 'No se puede eliminar la categoría porque está siendo usada por uno o más movimientos.');
        }
      });
    });
  }

  abrirCategoriaModal() {
    this.showCategoriaModal = true;
  }

  cerrarCategoriaModal() {
    this.showCategoriaModal = false;
    this.nuevaCategoria = { nombre: '', tipo: 'GASTO', limiteMensual: null };
  }

  abrirTarjetaModal() {
    this.nuevaTarjetaTemp = { nombre: '', diaCierre: 27, diaVencimiento: 10 };
    this.showNuevaTarjetaModal = true;
  }

  cerrarTarjetaModal() {
    this.showNuevaTarjetaModal = false;
  }

  guardarNuevaTarjeta() {
    if (!this.nuevaTarjetaTemp.nombre) return;
    this.tarjetaCreditoService.crearTarjeta(this.nuevaTarjetaTemp).subscribe({
      next: (tarjeta: any) => {
        this.nuevoGasto.tarjetaCreditoId = tarjeta.id;
        this.cerrarTarjetaModal();
      },
      error: () => alert('Error al crear la tarjeta')
    });
  }

  guardarCategoria() {
    if (!this.nuevaCategoria.nombre) return;
    this.categoriaService.crearCategoria(this.nuevaCategoria).subscribe(() => {
      this.cerrarCategoriaModal();
    });
  }

  resetForm() {
    this.nuevoGasto = {
      descripcion: '',
      monto: null,
      fecha: new Date().toISOString().split('T')[0],
      categoriaId: null,
      metodoPago: 'EFECTIVO',
      entidadPago: '',
      tarjetaCreditoId: null
    };
  }

  guardarGasto() {
    if (!this.nuevoGasto.descripcion || !this.nuevoGasto.monto || !this.nuevoGasto.categoriaId) return;
    
    if (this.nuevoGasto.monto > 99999999.99) {
      this.openAlert('Monto excedido', 'El monto máximo permitido para una transacción es $99.999.999,99.');
      return;
    }
    
    // Si el metodo es Tarjeta, validamos que haya elegido la tarjeta y reseteamos entidadPago
    if (this.nuevoGasto.metodoPago === 'TARJETA_CREDITO') {
      if (!this.nuevoGasto.tarjetaCreditoId) return;
      this.nuevoGasto.entidadPago = ''; // clear text input value
    } else {
      this.nuevoGasto.tarjetaCreditoId = null; // clear dropdown value
    }
    
    this.gastoService.registrarGasto({
      ...this.nuevoGasto,
      esRecurrente: false,
      pagado: true,
      notas: ''
    }).subscribe({
      next: () => {
        this.patrimonioService.cargarPatrimonioActual();
        this.gastoService.cargarGastosMes();
      },
      error: (err) => {
        console.error('Error al registrar gasto', err);
        alert('Hubo un error al registrar el gasto: ' + (err.error?.message || err.message));
      }
    });
    this.cerrarModal();
  }

  // --- Edición Fecha de Ingreso del Patrimonio ---
  showFechaIngresoModal = false;
  fechaIngresoEdicion: string = '';

  abrirModalFechaIngreso() {
    this.fechaIngresoEdicion = this.patrimonioService.patrimonio()?.fechaIngreso || new Date().toISOString().split('T')[0];
    this.showFechaIngresoModal = true;
  }

  cerrarModalFechaIngreso() {
    this.showFechaIngresoModal = false;
  }

  guardarFechaIngreso() {
    if (!this.fechaIngresoEdicion) return;
    const ingresoActual = this.patrimonioService.patrimonio()?.ingresoTotal || 0;
    this.patrimonioService.actualizarIngreso(ingresoActual, this.fechaIngresoEdicion).subscribe({
      next: () => {
        this.cerrarModalFechaIngreso();
      },
      error: (err) => {
        console.error('Error al actualizar fecha de ingreso', err);
        this.openAlert('Error', 'No se pudo actualizar la fecha de ingreso.');
      }
    });
  }
}
