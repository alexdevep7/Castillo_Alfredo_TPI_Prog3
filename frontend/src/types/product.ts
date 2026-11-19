export interface IProduct {
  id: number;
  nombre: string;
  precio: number;
  categoriaId: number;
  imagen: string;
  descripcion?: string;
  stock: number;
  disponible: boolean;
  eliminado: boolean;
}

export interface ICartItem {
  producto: IProduct;
  cantidad: number;
}
