import type { IProduct } from "../types/product";
import type { ICategoria } from "../types/categoria";
import type { IPedido } from "../types/pedido";

// --- CATEGORÍAS ---
export const getAllCategories = (): ICategoria[] => {
  const data = localStorage.getItem("categorias");
  return data ? JSON.parse(data) : [];
};

export const getCategories = (): ICategoria[] => {
  return getAllCategories().filter((c) => !c.eliminado);
};

export const saveCategories = (categories: ICategoria[]): void => {
  localStorage.setItem("categorias", JSON.stringify(categories));
};

// --- PRODUCTOS ---
export const getAllProducts = (): IProduct[] => {
  const data = localStorage.getItem("productos");
  return data ? JSON.parse(data) : [];
};

export const getProducts = (): IProduct[] => {
  // Para el cliente: solo productos no eliminados y marcados como disponibles
  return getAllProducts().filter((p) => !p.eliminado && p.disponible);
};

export const saveProducts = (products: IProduct[]): void => {
  localStorage.setItem("productos", JSON.stringify(products));
};

// --- PEDIDOS ---
export const getAllPedidos = (): IPedido[] => {
  const data = localStorage.getItem("pedidos");
  return data ? JSON.parse(data) : [];
};

export const savePedidos = (pedidos: IPedido[]): void => {
  localStorage.setItem("pedidos", JSON.stringify(pedidos));
};
