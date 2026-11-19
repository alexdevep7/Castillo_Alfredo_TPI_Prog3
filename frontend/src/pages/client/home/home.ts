import { getProducts, getCategories } from "../../../data/data";
import { addToCart, getCart } from "../../../utils/cart";
import { initData } from "../../../utils/localStorage";
import type { IProduct } from "../../../types/product";

// Referencias al DOM
const productsContainer = document.getElementById(
  "products-container",
) as HTMLDivElement;
const categoryContainer = document.getElementById(
  "category-container",
) as HTMLDivElement;
const searchInput = document.getElementById("search-input") as HTMLInputElement;
const noResultsMsg = document.getElementById(
  "no-results-msg",
) as HTMLParagraphElement;
const cartCountElement = document.getElementById(
  "cart-count",
) as HTMLSpanElement;

// Variables de estado para los filtros
let currentCategory: number | null = null;
let currentSearchTerm: string = "";

// Actualiza el número en el ícono del carrito
const updateCartCount = () => {
  const cart = getCart();
  const totalItems = cart.reduce((acc, item) => acc + item.cantidad, 0);
  if (cartCountElement) {
    cartCountElement.textContent = totalItems.toString();
  }
};

// Renderizar Productos
const renderProducts = (products: IProduct[]) => {
  productsContainer.innerHTML = "";

  if (products.length === 0) {
    noResultsMsg.style.display = "block";
    return;
  }

  noResultsMsg.style.display = "none";
  const categories = getCategories();

  products.forEach((producto) => {
    const categoryName =
      categories.find((c) => c.id === producto.categoriaId)?.nombre || "";
    const sinStock = producto.stock <= 0;

    const card = document.createElement("div");
    card.className = "product-card";

    card.innerHTML = `
      <img src="${producto.imagen}" alt="${producto.nombre}" />
      <span class="product-category">${categoryName}</span>
      <h3 class="product-title">${producto.nombre}</h3>
      <p style="font-size: 0.85rem; opacity: 0.8; margin-bottom: 8px;">
        ${sinStock ? "Sin stock" : `Stock disponible: ${producto.stock}`}
      </p>
      
      <div class="product-footer">
        <span class="product-price">$${producto.precio}</span>
        <button 
          class="add-btn" 
          data-id="${producto.id}" 
          ${sinStock ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : ""}
        >
          ${sinStock ? "Agotado" : "+ Agregar"}
        </button>
      </div>
    `;
    productsContainer.appendChild(card);
  });

  // Asignar eventos de click a los botones generados
  const buttons = document.querySelectorAll(".add-btn");
  buttons.forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const target = e.target as HTMLButtonElement;
      const id = parseInt(target.getAttribute("data-id") || "0");
      const currentProducts = getProducts();
      const productToAdd = currentProducts.find((p: IProduct) => p.id === id);

      if (productToAdd && productToAdd.stock > 0) {
        // Verificar que no supere el stock disponible en el carrito
        const cart = getCart();
        const itemInCart = cart.find((item) => item.producto.id === id);
        if (itemInCart && itemInCart.cantidad >= productToAdd.stock) {
          alert(`Solo hay ${productToAdd.stock} unidades disponibles.`);
          return;
        }

        addToCart(productToAdd);
        updateCartCount();

        // Efecto visual temporal en el botón
        const originalText = target.textContent;
        target.textContent = "✓ Agregado";
        target.style.backgroundColor = "#5E3122";

        setTimeout(() => {
          target.textContent = originalText;
          target.style.backgroundColor = "";
        }, 1000);
      }
    });
  });
};

// Lógica de Filtrado Combinado (Búsqueda + Categoría)
const filterProducts = () => {
  let filtered = getProducts();

  if (currentCategory !== null) {
    filtered = filtered.filter((p) => p.categoriaId === currentCategory);
  }

  if (currentSearchTerm) {
    filtered = filtered.filter((p) =>
      p.nombre.toLowerCase().includes(currentSearchTerm.toLowerCase()),
    );
  }

  renderProducts(filtered);
};

// Renderizar Categorías en el Menú Lateral
const renderCategories = () => {
  categoryContainer.innerHTML = "";
  const categories = getCategories();

  const btnAll = document.createElement("button");
  btnAll.textContent = "Todos los productos";
  btnAll.className = `category-btn ${currentCategory === null ? "active" : ""}`;
  btnAll.addEventListener("click", () => {
    currentCategory = null;
    renderCategories();
    filterProducts();
  });
  categoryContainer.appendChild(btnAll);

  categories.forEach((cat) => {
    const btn = document.createElement("button");
    btn.textContent = cat.nombre;
    btn.className = `category-btn ${currentCategory === cat.id ? "active" : ""}`;
    btn.addEventListener("click", () => {
      currentCategory = cat.id;
      renderCategories();
      filterProducts();
    });
    categoryContainer.appendChild(btn);
  });
};

// Lógica de Cierre de Sesión
const logoutBtn = document.getElementById("logout-btn");
if (logoutBtn) {
  logoutBtn.addEventListener("click", (e) => {
    e.preventDefault();
    localStorage.removeItem("userData");
    window.location.href = "../../auth/login/login.html";
  });
}

// Escuchar cambios en la barra de búsqueda
searchInput.addEventListener("input", (e) => {
  const target = e.target as HTMLInputElement;
  currentSearchTerm = target.value;
  filterProducts();
});

// Inicialización de la vista
const initHome = async () => {
  await initData();
  renderCategories();
  renderProducts(getProducts());
  updateCartCount();
};

initHome();
