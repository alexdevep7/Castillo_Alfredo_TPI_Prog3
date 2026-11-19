import type { ICartItem, IProduct } from "../types/product";

const CART_KEY = "cart";

export const getCart = (): ICartItem[] => {
  const cartData = localStorage.getItem(CART_KEY);
  return cartData ? JSON.parse(cartData) : [];
};

export const saveCart = (cart: ICartItem[]): void => {
  localStorage.setItem(CART_KEY, JSON.stringify(cart));
};

export const addToCart = (producto: IProduct): void => {
  const cart = getCart();
  const existingItem = cart.find((item) => item.producto.id === producto.id);

  if (existingItem) {
    existingItem.cantidad += 1;
  } else {
    cart.push({ producto, cantidad: 1 });
  }

  saveCart(cart);
};

export const getCartTotal = (): number => {
  const cart = getCart();
  return cart.reduce(
    (total, item) => total + item.producto.precio * item.cantidad,
    0,
  );
};

// --- NUEVAS FUNCIONES PARA EL CARRITO ---

export const updateQuantity = (productId: number, change: number): void => {
  const cart = getCart();
  const itemIndex = cart.findIndex((item) => item.producto.id === productId);

  if (itemIndex > -1) {
    cart[itemIndex].cantidad += change;
    if (cart[itemIndex].cantidad <= 0) {
      cart.splice(itemIndex, 1); // Elimina el producto si llega a 0
    }
    saveCart(cart);
  }
};

export const removeFromCart = (productId: number): void => {
  let cart = getCart();
  cart = cart.filter((item) => item.producto.id !== productId);
  saveCart(cart);
};

export const clearCart = (): void => {
  saveCart([]);
};
