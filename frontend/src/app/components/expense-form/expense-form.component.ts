import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { GastoService } from '../../services/gasto.service';
import { PlantillaGastoService, PlantillaGastoResponse } from '../../services/plantilla-gasto.service';
import { Categoria } from '../../models/categoria';
import { TarjetaCreditoService } from '../../services/tarjeta-credito.service';
import { TarjetaCredito } from '../../models/tarjeta-credito';
import { MetaAhorroService } from '../../services/meta-ahorro.service';

@Component({
  selector: 'app-expense-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './expense-form.component.html',
  styleUrls: []
})
export class ExpenseFormComponent implements OnInit {
  expenseForm: FormGroup;
  categorias: Categoria[] = [];
  tarjetas: TarjetaCredito[] = [];
  submitting: boolean = false;

  cierraProximoMes: boolean = false;
  mensajeAlerta: string = '';
  mensajeRedondeo: string = '';

  constructor(
    private fb: FormBuilder,
    private gastoService: GastoService,
    public plantillaService: PlantillaGastoService,
    public metaAhorroService: MetaAhorroService,
    private tarjetaService: TarjetaCreditoService,
    private router: Router
  ) {
    this.expenseForm = this.fb.group({
      descripcion: ['', [Validators.required, Validators.minLength(3)]],
      monto: ['', [Validators.required, Validators.min(0.01), Validators.max(99999999.99)]],
      fecha: [new Date().toISOString().split('T')[0], Validators.required],
      categoriaId: ['', Validators.required],
      metodoPago: ['', Validators.required],
      tarjetaCreditoId: [''],
      dividirGasto: [false],
      emailDeudor: [''],
      porcentajeDeuda: [50],
      destinarAhorro: [false],
      montoAhorro: ['']
    });

    this.expenseForm.get('dividirGasto')?.valueChanges.subscribe(dividir => {
      const emailCtrl = this.expenseForm.get('emailDeudor');
      const porcCtrl = this.expenseForm.get('porcentajeDeuda');
      
      if (dividir) {
        emailCtrl?.setValidators([Validators.required, Validators.email]);
        porcCtrl?.setValidators([Validators.required, Validators.min(1), Validators.max(99)]);
      } else {
        emailCtrl?.clearValidators();
        porcCtrl?.clearValidators();
      }
      emailCtrl?.updateValueAndValidity();
      porcCtrl?.updateValueAndValidity();
    });

    this.expenseForm.get('metodoPago')?.valueChanges.subscribe(metodo => {
      const tarjetaCtrl = this.expenseForm.get('tarjetaCreditoId');
      if (metodo === 'TARJETA_CREDITO') {
        tarjetaCtrl?.setValidators([Validators.required]);
      } else {
        tarjetaCtrl?.clearValidators();
        tarjetaCtrl?.setValue('');
      }
      tarjetaCtrl?.updateValueAndValidity();
    });

    this.expenseForm.valueChanges.subscribe(value => {
      // Lógica de alerta de tarjeta
      if (value.metodoPago === 'TARJETA_CREDITO' && value.fecha && value.tarjetaCreditoId) {
        const date = new Date(value.fecha + 'T00:00:00'); // Ensure it parses the local date correctly
        const tarjeta = this.tarjetas.find(t => t.id == value.tarjetaCreditoId);
        const diaCierre = tarjeta ? tarjeta.diaCierre : 27;
        
        if (date.getDate() <= diaCierre) {
          this.cierraProximoMes = true;
          this.mensajeAlerta = 'Este gasto de tarjeta se pagará en el próximo resumen.';
        } else {
          this.cierraProximoMes = true;
          this.mensajeAlerta = `Gasto posterior al cierre (día ${diaCierre}). Este monto se pagará en el resumen del mes subsiguiente.`;
        }
      } else {
        this.cierraProximoMes = false;
        this.mensajeAlerta = '';
      }

      // Lógica de redondeo
      this.mensajeRedondeo = '';
      if (value.metodoPago && value.metodoPago !== 'TARJETA_CREDITO' && value.monto > 0) {
        const metaActiva = this.metaAhorroService.getMetaActiva();
        if (metaActiva) {
          const monto = value.monto;
          const redondeo = Math.ceil(monto / 1000) * 1000;
          const diferencia = redondeo - monto;
          if (diferencia > 0) {
            this.mensajeRedondeo = `¡Se aplicará un redondeo automático de $${diferencia.toFixed(2)} a tu meta activa (${metaActiva.nombre})!`;
          }
        }
      }
    });
  }

  ngOnInit(): void {
    this.gastoService.getCategorias().subscribe({
      next: (data) => this.categorias = data,
      error: (err) => console.error('Error al cargar categorías', err)
    });
    
    this.tarjetaService.getTarjetas().subscribe({
      next: (data) => this.tarjetas = data,
      error: (err) => console.error('Error al cargar tarjetas', err)
    });
    
    this.plantillaService.cargarPlantillas();
    this.metaAhorroService.cargarMetas();
  }

  cargarPlantilla(plantilla: PlantillaGastoResponse): void {
    this.expenseForm.patchValue({
      descripcion: plantilla.descripcion,
      monto: plantilla.monto,
      categoriaId: plantilla.categoria.id,
      metodoPago: plantilla.metodoPago
    });
  }

  get redondeoSugerido(): number {
    const monto = this.expenseForm.get('monto')?.value;
    if (!monto || monto <= 0) return 0;
    const redondeo = Math.ceil(Number(monto) / 1000) * 1000;
    const diff = redondeo - Number(monto);
    return diff > 0 ? Number(diff.toFixed(2)) : 0;
  }

  onDestinarAhorroToggle(): void {
    const destinar = this.expenseForm.get('destinarAhorro')?.value;
    const montoAhorroCtrl = this.expenseForm.get('montoAhorro');
    if (destinar && (!montoAhorroCtrl?.value || montoAhorroCtrl.value <= 0)) {
      const sugerido = this.redondeoSugerido;
      if (sugerido > 0) {
        montoAhorroCtrl?.setValue(sugerido);
      }
    }
  }

  aplicarRedondeo(): void {
    const sugerido = this.redondeoSugerido;
    if (sugerido > 0) {
      this.expenseForm.get('montoAhorro')?.setValue(sugerido);
    }
  }

  onSubmit(): void {
    if (this.expenseForm.valid) {
      this.submitting = true;
      const formVal = { ...this.expenseForm.value };
      if (!formVal.destinarAhorro) {
        formVal.montoAhorro = null;
      } else if (!formVal.montoAhorro) {
        formVal.montoAhorro = this.redondeoSugerido > 0 ? this.redondeoSugerido : null;
      }

      this.gastoService.crearGasto(formVal).subscribe({
        next: () => {
          this.submitting = false;
          this.expenseForm.reset();
          this.router.navigate(['/gastos']);
        },
        error: (err) => {
          console.error('Error al guardar el gasto', err);
          this.submitting = false;
        }
      });
    } else {
      this.expenseForm.markAllAsTouched();
    }
  }
}
