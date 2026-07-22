export interface Patrimonio {
  id: number;
  mes: number;
  anio: number;
  ingresoTotal: number;
  saldoActual: number;
  totalPagado: number;
  gastoMesQueViene: number;
  fechaIngreso?: string;
}
