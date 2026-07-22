import { Categoria } from './categoria';

export enum MetodoPago {
  TARJETA_CREDITO = 'TARJETA_CREDITO',
  TARJETA_DEBITO = 'TARJETA_DEBITO',
  EFECTIVO = 'EFECTIVO',
  TRANSFERENCIA = 'TRANSFERENCIA'
}

export interface Gasto {
  id: number;
  descripcion: string;
  monto: number;
  fecha: string;
  periodoFinanciero: string;
  categoria?: Categoria;
  metodoPago: MetodoPago;
  esRecurrente: boolean;
  pagado: boolean;
  notas?: string;
  entidadPago?: string;
  tarjetaCreditoId?: number;
  nombreTarjeta?: string;
}
