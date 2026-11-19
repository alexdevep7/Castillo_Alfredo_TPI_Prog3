import type { IProduct } from "./product";

export enum Estado {
  PENDIENTE = "PENDIENTE",
  CONFIRMADO = "CONFIRMADO",
  TERMINADO = "TERMINADO",
  CANCELADO = "CANCELADO",
}

export enum FormaPago {
  TARJETA = "TARJETA",
  TRANSFERENCIA = "TRANSFERENCIA",
  EFECTIVO = "EFECTIVO",
}

export interface IDetallePedido {
  id?: number;
  cantidad: number;
  subtotal: number;
  productoId: number;
  producto?: IProduct; // Opcional, útil al hidratar los datos para mostrar nombre o imagen en la vista
}

export interface IPedido {
  id: number;
  fecha: string;
  estado: Estado;
  formaPago: FormaPago;
  total: number;
  usuarioId: number;
  detalles: IDetallePedido[];
  eliminado: boolean;
}
