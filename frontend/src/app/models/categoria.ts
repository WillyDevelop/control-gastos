export enum TipoCategoria {
  GASTO = 'GASTO',
  INGRESO = 'INGRESO'
}

export interface Categoria {
  id: number;
  nombre: string;
  icono?: string;
  tipo: TipoCategoria;
  limiteMensual?: number;
}
