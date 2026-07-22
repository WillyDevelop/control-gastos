import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GastoService } from '../../services/gasto.service';
import { CategoriaService } from '../../services/categoria.service';
import { TarjetaCreditoService } from '../../services/tarjeta-credito.service';
import { Gasto } from '../../models/gasto';
import { TarjetaCredito } from '../../models/tarjeta-credito';

@Component({
  selector: 'app-tarjetas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tarjetas.component.html',
  styleUrls: ['./tarjetas.component.css']
})
export class TarjetasComponent implements OnInit {
  tarjetas: TarjetaCredito[] = [];
  activeTarjetaId: number | null = null;

  gastosPendientes: Gasto[] = [];
  gastosHistorial: Gasto[] = [];
  totalProximoResumen: number = 0;
  totalSubsiguientes: number = 0;
  
  loading: boolean = true;
  activeTab: 'PENDIENTES' | 'HISTORIAL' = 'PENDIENTES';

  // Modales Gasto
  showConfirmModal = false;
  confirmConfig = { title: '', message: '', action: () => {}, isAlert: false };
  showEditModal = false;
  gastoEnEdicion: any = {};

  // Modales Tarjeta
  showTarjetaModal = false;
  nuevaTarjeta: Partial<TarjetaCredito> = { nombre: '', diaCierre: 27, diaVencimiento: 10 };

  // Estado del Resumen
  periodoProximoResumen: string | null = null;
  estadoResumen: 'ABIERTO' | 'PAGO' | 'VENCIDO' = 'ABIERTO';
  textoBotonResumen = '⏳ Resumen Abierto';
  claseBotonResumen = '';

  categoriaService = inject(CategoriaService);
  private gastoService = inject(GastoService);
  private tarjetaService = inject(TarjetaCreditoService);

  ngOnInit(): void {
    this.cargarTarjetas();
  }

  cargarTarjetas(): void {
    this.tarjetaService.getTarjetas().subscribe({
      next: (data) => {
        this.tarjetas = data;
        if (this.tarjetas.length > 0 && !this.activeTarjetaId) {
          this.activeTarjetaId = this.tarjetas[0].id!;
        }
        this.cargarDatos();
      },
      error: (err) => console.error('Error al cargar tarjetas', err)
    });
  }

  seleccionarTarjeta(id: number): void {
    this.activeTarjetaId = id;
    this.cargarDatos();
  }

  getActiveTarjeta(): TarjetaCredito | undefined {
    return this.tarjetas.find(t => t.id === this.activeTarjetaId);
  }

  cargarDatos(): void {
    this.loading = true;
    if (this.activeTab === 'PENDIENTES') {
      this.cargarPendientes();
    } else {
      this.cargarHistorial();
    }
  }

  setTab(tab: 'PENDIENTES' | 'HISTORIAL'): void {
    this.activeTab = tab;
    this.cargarDatos();
  }

  cargarPendientes(): void {
    this.gastoService.getProximosGastosTarjeta().subscribe({
      next: (data) => {
        if (this.activeTarjetaId) {
          this.gastosPendientes = data.filter(g => g.tarjetaCreditoId === this.activeTarjetaId);
        } else {
          this.gastosPendientes = data;
        }
        this.calcularTotales();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar próximos gastos de tarjeta', err);
        this.loading = false;
      }
    });
  }

  cargarHistorial(): void {
    this.gastoService.getHistorialGastosTarjeta().subscribe({
      next: (data) => {
        if (this.activeTarjetaId) {
          this.gastosHistorial = data.filter(g => g.tarjetaCreditoId === this.activeTarjetaId);
        } else {
          this.gastosHistorial = data;
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar historial de tarjetas', err);
        this.loading = false;
      }
    });
  }

  pagarResumenMasivo(): void {
    if (this.estadoResumen === 'ABIERTO' || !this.periodoProximoResumen) return;

    this.confirmConfig = {
      title: 'Pagar Resumen Completo',
      message: `¿Estás seguro de que deseas liquidar el resumen completo por ${new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(this.totalProximoResumen)}? Esto descontará del saldo actual de tu patrimonio.`,
      isAlert: false,
      action: () => {
        this.gastoService.pagarResumen(this.periodoProximoResumen!, this.activeTarjetaId!).subscribe({
          next: () => {
            this.cargarPendientes();
            this.showConfirmModal = false;
          },
          error: (err) => console.error('Error al pagar resumen masivo', err)
        });
      }
    };
    this.showConfirmModal = true;
  }

  calcularTotales(): void {
    if (this.gastosPendientes.length === 0) {
      this.totalProximoResumen = 0;
      this.totalSubsiguientes = 0;
      this.periodoProximoResumen = null;
      this.evaluarEstadoResumen();
      return;
    }

    let earliestPeriod: string | null = null;
    this.gastosPendientes.forEach(gasto => {
      if (!earliestPeriod || gasto.periodoFinanciero < earliestPeriod) {
        earliestPeriod = gasto.periodoFinanciero;
      }
    });

    this.periodoProximoResumen = earliestPeriod;
    this.totalProximoResumen = 0;
    this.totalSubsiguientes = 0;

    this.gastosPendientes.forEach(gasto => {
      if (gasto.periodoFinanciero === earliestPeriod) {
        this.totalProximoResumen += gasto.monto;
      } else {
        this.totalSubsiguientes += gasto.monto;
      }
    });

    this.evaluarEstadoResumen();
  }

  evaluarEstadoResumen(): void {
    this.estadoResumen = 'ABIERTO';
    if (!this.periodoProximoResumen || !this.activeTarjetaId) {
        this.textoBotonResumen = '⏳ Sin consumos pendientes';
        this.claseBotonResumen = 'bg-extended-gray-light-bg text-extended-gray-muted border-extended-gray-border opacity-70';
        return;
    }

    const activeCard = this.tarjetas.find(t => t.id === this.activeTarjetaId);
    const diaCierre = activeCard?.diaCierre || 27;
    const diaVencimiento = activeCard?.diaVencimiento || 10;

    const today = new Date();
    const [yearStr, monthStr] = this.periodoProximoResumen.split('-');
    const dueYear = parseInt(yearStr, 10);
    const dueMonth = parseInt(monthStr, 10);
    
    // Asumimos que el mes en periodoFinanciero es el mes en el que se DEBE PAGAR.
    // El cierre fue el mes anterior.
    const dueDate = new Date(dueYear, dueMonth - 1, diaVencimiento);
    const closeDate = new Date(dueYear, dueMonth - 2, diaCierre);

    const todayDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());

    if (todayDate > dueDate) {
        this.estadoResumen = 'VENCIDO';
        this.textoBotonResumen = `🚨 Resumen Vencido (Regularizar)`;
        this.claseBotonResumen = 'bg-rose-300 text-rose-900 border-rose-400 hover:bg-rose-400 shadow-subtle';
    } else if (todayDate >= closeDate && todayDate <= dueDate) {
        this.estadoResumen = 'PAGO';
        this.textoBotonResumen = `💳 Pagar Resumen (Vence ${diaVencimiento}/${dueMonth})`;
        this.claseBotonResumen = 'bg-emerald-300 text-emerald-900 border-emerald-400 hover:bg-emerald-400 shadow-subtle';
    } else {
        this.estadoResumen = 'ABIERTO';
        this.textoBotonResumen = `⏳ Resumen Abierto (Cierra el ${diaCierre})`;
        this.claseBotonResumen = 'bg-extended-gray-light-bg text-extended-gray-muted border-extended-gray-border opacity-70';
    }
  }

  getMesPago(periodoFinanciero: string): string {
    const date = new Date(periodoFinanciero + 'T00:00:00');
    let str = new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' }).format(date);
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  // --- Modal Editar Gasto ---
  editarGasto(gasto: Gasto): void {
    this.gastoEnEdicion = {
      id: gasto.id,
      descripcion: gasto.descripcion,
      monto: gasto.monto,
      fecha: gasto.fecha,
      categoriaId: gasto.categoria?.id,
      metodoPago: gasto.metodoPago,
      pagado: gasto.pagado,
      notas: gasto.notas,
      entidadPago: gasto.entidadPago,
      tarjetaCreditoId: gasto.tarjetaCreditoId
    };
    this.showEditModal = true;
  }

  guardarGastoEditado(): void {
    if (this.gastoEnEdicion.monto > 99999999.99) {
      alert('El monto no puede superar $99.999.999,99.');
      return;
    }
    this.gastoService.actualizarGasto(this.gastoEnEdicion.id, this.gastoEnEdicion).subscribe({
      next: () => {
        this.showEditModal = false;
        this.cargarDatos();
      },
      error: (err) => console.error('Error al editar el gasto', err)
    });
  }
  
  cerrarEditModal(): void {
    this.showEditModal = false;
  }

  borrarGasto(id: number): void {
    this.confirmConfig = {
      title: 'Eliminar Consumo',
      message: '¿Estás seguro de que deseas eliminar este gasto de la tarjeta? Esta acción no se puede deshacer.',
      isAlert: false,
      action: () => {
        this.gastoService.eliminarGasto(id).subscribe({
          next: () => {
            this.cargarDatos();
            this.showConfirmModal = false;
          },
          error: (err) => console.error('Error al borrar el gasto', err)
        });
      }
    };
    this.showConfirmModal = true;
  }

  closeConfirm(): void {
    this.showConfirmModal = false;
  }

  executeConfirm(): void {
    if (this.confirmConfig.action) {
      this.confirmConfig.action();
    }
  }

  // --- Modal Nueva Tarjeta ---
  abrirModalTarjeta(): void {
    this.tarjetaEnEdicionId = null;
    this.nuevaTarjeta = { nombre: '', diaCierre: 27, diaVencimiento: 10 };
    this.showTarjetaModal = true;
  }

  cerrarModalTarjeta(): void {
    this.showTarjetaModal = false;
    this.tarjetaEnEdicionId = null;
  }

  guardarTarjeta(): void {
    if (!this.nuevaTarjeta.nombre || !this.nuevaTarjeta.diaCierre || !this.nuevaTarjeta.diaVencimiento) return;
    
    if (this.tarjetaEnEdicionId) {
      this.tarjetaService.actualizarTarjeta(this.tarjetaEnEdicionId, this.nuevaTarjeta as TarjetaCredito).subscribe({
        next: (t) => {
          this.tarjetas = this.tarjetas.map(card => card.id === t.id ? t : card);
          this.showTarjetaModal = false;
          this.tarjetaEnEdicionId = null;
          this.cargarDatos();
        },
        error: (err) => console.error('Error al editar tarjeta', err)
      });
    } else {
      this.tarjetaService.crearTarjeta(this.nuevaTarjeta as TarjetaCredito).subscribe({
        next: (t) => {
          this.tarjetas.push(t);
          this.activeTarjetaId = t.id!;
          this.showTarjetaModal = false;
          this.cargarDatos();
        },
        error: (err) => console.error('Error al crear tarjeta', err)
      });
    }
  }

  tarjetaEnEdicionId: number | null = null;

  editarTarjeta(tarjeta: TarjetaCredito): void {
    this.tarjetaEnEdicionId = tarjeta.id!;
    this.nuevaTarjeta = { ...tarjeta };
    this.showTarjetaModal = true;
  }

  borrarTarjeta(id: number): void {
    this.confirmConfig = {
      title: 'Eliminar Tarjeta',
      message: '¿Estás seguro de que deseas eliminar esta tarjeta? Si tiene consumos asociados, la base de datos podría impedirlo si no están migrados.',
      isAlert: false,
      action: () => {
        this.tarjetaService.eliminarTarjeta(id).subscribe({
          next: () => {
            this.tarjetas = this.tarjetas.filter(t => t.id !== id);
            if (this.activeTarjetaId === id) {
              this.activeTarjetaId = this.tarjetas.length > 0 ? this.tarjetas[0].id! : null;
            }
            this.cargarDatos();
            this.showConfirmModal = false;
          },
          error: (err) => {
            console.error('Error al borrar la tarjeta', err);
            alert('No se pudo borrar la tarjeta. Asegúrate de que no tenga gastos asociados.');
          }
        });
      }
    };
    this.showConfirmModal = true;
  }

  getCategoriasGasto() {
    return this.categoriaService.categorias().filter(c => c.tipo === 'GASTO');
  }
}

