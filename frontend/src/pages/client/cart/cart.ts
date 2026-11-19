import {
  getCart,
  getCartTotal,
  updateQuantity,
  removeFromCart,
  clearCart,
} from "../../../utils/cart";
import {
  getCategories,
  getAllProducts,
  saveProducts,
  getAllPedidos,
  savePedidos,
} from "../../../data/data";
import { getSession } from "../../../utils/auth";
import { initData } from "../../../utils/localStorage";
import type { ICartItem } from "../../../types/product";
import {
  Estado,
  FormaPago,
  type IDetallePedido,
  type IPedido,
} from "../../../types/pedido";

const cartItemsContainer = document.getElementById(
  "cart-items-container",
) as HTMLDivElement;
const emptyCartMsg = document.getElementById(
  "empty-cart-msg",
) as HTMLParagraphElement;
const summarySubtotalElement = document.getElementById(
  "summary-subtotal",
) as HTMLSpanElement;
const summaryTotalElement = document.getElementById(
  "summary-total",
) as HTMLSpanElement;
const clearCartBtn = document.getElementById(
  "clear-cart-btn",
) as HTMLButtonElement;
const checkoutBtn = document.getElementById(
  "checkout-btn",
) as HTMLButtonElement;
const formaPagoSelect = document.getElementById(
  "forma-pago",
) as HTMLSelectElement;

const renderCart = () => {
  const cart: ICartItem[] = getCart();
  const categories = getCategories();
  cartItemsContainer.innerHTML = "";

  if (cart.length === 0) {
    emptyCartMsg.style.display = "block";
    summarySubtotalElement.textContent = "0";
    summaryTotalElement.textContent = "0";
    clearCartBtn.style.display = "none";
    checkoutBtn.disabled = true;
    return;
  }

  emptyCartMsg.style.display = "none";
  clearCartBtn.style.display = "block";
  checkoutBtn.disabled = false;

  cart.forEach((item: ICartItem) => {
    const categoryName =
      categories.find((c) => c.id === item.producto.categoriaId)?.nombre || "";
    const row = document.createElement("div");
    row.className = "cart-item";

    row.innerHTML = `
      <img src="${item.producto.imagen}" alt="${item.producto.nombre}">
      <div class="item-info">
        <h3>${item.producto.nombre}</h3>
        <div class="item-category">${categoryName}</div>
        <div class="item-subtotal">Subtotal: $${item.producto.precio * item.cantidad}</div>
      </div>
      <div>
        <div class="quantity-controls">
          <button class="qty-btn decrease-btn" data-id="${item.producto.id}">-</button>
          <span class="qty-value">${item.cantidad}</span>
          <button class="qty-btn increase-btn" data-id="${item.producto.id}">+</button>
        </div>
        <button class="remove-btn" data-id="${item.producto.id}">Eliminar</button>
      </div>
    `;
    cartItemsContainer.appendChild(row);
  });

  const total = getCartTotal().toString();
  summarySubtotalElement.textContent = total;
  summaryTotalElement.textContent = total;

  // Sumar cantidad validando stock
  document.querySelectorAll(".increase-btn").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const id = parseInt(
        (e.target as HTMLButtonElement).getAttribute("data-id") || "0",
      );
      const currentCart = getCart();
      const item = currentCart.find((i) => i.producto.id === id);
      const productInDb = getAllProducts().find((p) => p.id === id);

      if (item && productInDb && item.cantidad >= productInDb.stock) {
        alert(`Stock máximo alcanzado (${productInDb.stock} unidades).`);
        return;
      }

      updateQuantity(id, 1);
      renderCart();
    });
  });

  // Restar cantidad
  document.querySelectorAll(".decrease-btn").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const id = parseInt(
        (e.target as HTMLButtonElement).getAttribute("data-id") || "0",
      );
      updateQuantity(id, -1);
      renderCart();
    });
  });

  // Eliminar producto
  document.querySelectorAll(".remove-btn").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const id = parseInt(
        (e.target as HTMLButtonElement).getAttribute("data-id") || "0",
      );
      removeFromCart(id);
      renderCart();
    });
  });
};

// Confirmar compra y generar pedido
checkoutBtn.addEventListener("click", () => {
  const user = getSession();
  if (!user) {
    alert("Debes iniciar sesión para finalizar la compra.");
    window.location.href = "../../auth/login/login.html";
    return;
  }

  const cart = getCart();
  if (cart.length === 0) return;

  const allProducts = getAllProducts();

  // Validar disponibilidad y stock actual de cada ítem antes de confirmar
  for (const item of cart) {
    const prod = allProducts.find((p) => p.id === item.producto.id);
    if (
      !prod ||
      prod.eliminado ||
      !prod.disponible ||
      prod.stock < item.cantidad
    ) {
      alert(
        `No hay stock suficiente para "${item.producto.nombre}". Disponible: ${prod ? prod.stock : 0}.`,
      );
      return;
    }
  }

  // Armar los detalles del pedido
  const detalles: IDetallePedido[] = cart.map((item, index) => ({
    id: index + 1,
    cantidad: item.cantidad,
    subtotal: item.producto.precio * item.cantidad,
    productoId: item.producto.id,
  }));

  const pedidos = getAllPedidos();
  const nextPedidoId =
    pedidos.length > 0 ? Math.max(...pedidos.map((p) => p.id)) + 1 : 1;

  const nuevoPedido: IPedido = {
    id: nextPedidoId,
    fecha: new Date().toISOString().split("T")[0],
    estado: Estado.PENDIENTE,
    formaPago: formaPagoSelect.value as FormaPago,
    total: getCartTotal(),
    usuarioId: user.id,
    detalles,
    eliminado: false,
  };

  // 1. Guardar el pedido en localStorage
  pedidos.push(nuevoPedido);
  savePedidos(pedidos);

  // 2. Descontar stock de los productos
  const updatedProducts = allProducts.map((prod) => {
    const cartItem = cart.find((item) => item.producto.id === prod.id);
    if (cartItem) {
      return {
        ...prod,
        stock: prod.stock - cartItem.cantidad,
      };
    }
    return prod;
  });
  saveProducts(updatedProducts);

  // 3. Vaciar carrito y redirigir
  clearCart();
  alert(
    `¡Pedido #${nuevoPedido.id} confirmado con éxito! Total: $${nuevoPedido.total}`,
  );
  window.location.href = "../home/home.html";
});

// Cierre de sesión
const logoutBtn = document.getElementById("logout-btn");
if (logoutBtn) {
  logoutBtn.addEventListener("click", (e) => {
    e.preventDefault();
    localStorage.removeItem("userData");
    window.location.href = "../../auth/login/login.html";
  });
}

// Vaciar carrito
clearCartBtn.addEventListener("click", () => {
  clearCart();
  renderCart();
});

// Inicializar vista
const initCart = async () => {
  await initData();
  renderCart();
};

initCart();
