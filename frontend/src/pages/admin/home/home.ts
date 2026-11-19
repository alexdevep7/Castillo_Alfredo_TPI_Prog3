import {
  getAllCategories,
  getCategories,
  saveCategories,
  getAllProducts,
  saveProducts,
  getAllPedidos,
  savePedidos,
} from "../../../data/data";
import { getSession, getUsers, logout } from "../../../utils/auth";
import { initData } from "../../../utils/localStorage";
import { Rol } from "../../../types/Rol";
import { Estado } from "../../../types/pedido";
import type { ICategoria } from "../../../types/categoria";
import type { IProduct } from "../../../types/product";

// Verificar sesión y rol ADMIN
const checkAdminAccess = () => {
  const user = getSession();
  if (!user || user.rol !== Rol.ADMIN) {
    window.location.href = "../../auth/login/login.html";
    return;
  }
  const adminNameEl = document.getElementById("admin-name");
  if (adminNameEl) {
    adminNameEl.textContent = `Hola, ${user.nombre} ${user.apellido}`;
  }
};

// Cierre de sesión
const btnLogout = document.getElementById("logout-btn");
btnLogout?.addEventListener("click", () => {
  logout();
  window.location.href = "../../auth/login/login.html";
});

// Navegación entre pestañas
const tabBtns = document.querySelectorAll<HTMLButtonElement>(".tab-btn");
const tabSections = document.querySelectorAll<HTMLElement>(".tab-section");

tabBtns.forEach((btn) => {
  btn.addEventListener("click", () => {
    const targetTab = btn.getAttribute("data-tab");
    tabBtns.forEach((b) => b.classList.remove("active"));
    tabSections.forEach((s) => s.classList.remove("active"));

    btn.classList.add("active");
    document.getElementById(`tab-${targetTab}`)?.classList.add("active");
  });
});

// ==========================================
// 1. GESTIÓN DE PEDIDOS
// ==========================================
const renderPedidos = () => {
  const tbody = document.getElementById(
    "pedidos-tbody",
  ) as HTMLTableSectionElement;
  const pedidos = getAllPedidos().filter((p) => !p.eliminado);
  const users = getUsers();
  const products = getAllProducts();

  tbody.innerHTML = "";

  if (pedidos.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">No hay pedidos registrados.</td></tr>`;
    return;
  }

  // Mostrar los más recientes primero
  [...pedidos].reverse().forEach((pedido) => {
    const cliente = users.find((u) => u.id === pedido.usuarioId);
    const clienteText = cliente
      ? `${cliente.nombre} ${cliente.apellido}<br><small style="color:#a88a7c">${cliente.mail}</small>`
      : `Usuario #${pedido.usuarioId}`;

    const detallesHtml = pedido.detalles
      .map((d) => {
        const prod = products.find((p) => p.id === d.productoId);
        return `<div>• ${d.cantidad}x ${prod ? prod.nombre : `Prod #${d.productoId}`} ($${d.subtotal})</div>`;
      })
      .join("");

    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td><strong>#${pedido.id}</strong></td>
      <td>${pedido.fecha}</td>
      <td>${clienteText}</td>
      <td>${detallesHtml}</td>
      <td>${pedido.formaPago}</td>
      <td><strong>$${pedido.total}</strong></td>
      <td>
        <select class="status-select" data-id="${pedido.id}">
          ${Object.values(Estado)
            .map(
              (est) =>
                `<option value="${est}" ${pedido.estado === est ? "selected" : ""}>${est}</option>`,
            )
            .join("")}
        </select>
      </td>
    `;
    tbody.appendChild(tr);
  });

  // Cambio de estado del pedido
  document
    .querySelectorAll<HTMLSelectElement>(".status-select")
    .forEach((sel) => {
      sel.addEventListener("change", (e) => {
        const target = e.target as HTMLSelectElement;
        const pedidoId = parseInt(target.getAttribute("data-id") || "0");
        const nuevoEstado = target.value as Estado;

        const allPedidos = getAllPedidos();
        const idx = allPedidos.findIndex((p) => p.id === pedidoId);
        if (idx === -1) return;

        const estadoAnterior = allPedidos[idx].estado;

        // Si se cancela un pedido que no estaba cancelado, devolver el stock
        if (
          nuevoEstado === Estado.CANCELADO &&
          estadoAnterior !== Estado.CANCELADO
        ) {
          const allProds = getAllProducts();
          allPedidos[idx].detalles.forEach((det) => {
            const pIdx = allProds.findIndex((p) => p.id === det.productoId);
            if (pIdx !== -1) {
              allProds[pIdx].stock += det.cantidad;
            }
          });
          saveProducts(allProds);
          renderProductos();
        }

        allPedidos[idx].estado = nuevoEstado;
        savePedidos(allPedidos);
        alert(`Estado del Pedido #${pedidoId} actualizado a ${nuevoEstado}.`);
      });
    });
};

// ==========================================
// 2. CRUD DE PRODUCTOS
// ==========================================
const formProducto = document.getElementById(
  "form-producto",
) as HTMLFormElement;
const prodIdInput = document.getElementById("prod-id") as HTMLInputElement;
const prodNombreInput = document.getElementById(
  "prod-nombre",
) as HTMLInputElement;
const prodCategoriaSelect = document.getElementById(
  "prod-categoria",
) as HTMLSelectElement;
const prodPrecioInput = document.getElementById(
  "prod-precio",
) as HTMLInputElement;
const prodStockInput = document.getElementById(
  "prod-stock",
) as HTMLInputElement;
const prodImagenInput = document.getElementById(
  "prod-imagen",
) as HTMLInputElement;
const prodDescripcionInput = document.getElementById(
  "prod-descripcion",
) as HTMLInputElement;
const prodDisponibleInput = document.getElementById(
  "prod-disponible",
) as HTMLInputElement;
const prodFormTitle = document.getElementById(
  "prod-form-title",
) as HTMLHeadingElement;
const prodCancelBtn = document.getElementById(
  "prod-cancel-btn",
) as HTMLButtonElement;

const populateCategorySelect = () => {
  const categories = getCategories();
  prodCategoriaSelect.innerHTML = categories
    .map((c) => `<option value="${c.id}">${c.nombre}</option>`)
    .join("");
};

const resetProductForm = () => {
  formProducto.reset();
  prodIdInput.value = "";
  prodDisponibleInput.checked = true;
  prodFormTitle.textContent = "Nuevo Producto";
  prodCancelBtn.style.display = "none";
};

prodCancelBtn.addEventListener("click", resetProductForm);

formProducto.addEventListener("submit", (e) => {
  e.preventDefault();
  const allProducts = getAllProducts();
  const idValue = prodIdInput.value;

  const productData = {
    nombre: prodNombreInput.value.trim(),
    categoriaId: parseInt(prodCategoriaSelect.value),
    precio: parseFloat(prodPrecioInput.value),
    stock: parseInt(prodStockInput.value),
    imagen: prodImagenInput.value.trim(),
    descripcion: prodDescripcionInput.value.trim(),
    disponible: prodDisponibleInput.checked,
    eliminado: false,
  };

  if (idValue) {
    // Edición
    const id = parseInt(idValue);
    const idx = allProducts.findIndex((p) => p.id === id);
    if (idx !== -1) {
      allProducts[idx] = { ...allProducts[idx], ...productData };
    }
  } else {
    // Alta
    const nextId =
      allProducts.length > 0
        ? Math.max(...allProducts.map((p) => p.id)) + 1
        : 1;
    const newProduct: IProduct = { id: nextId, ...productData };
    allProducts.push(newProduct);
  }

  saveProducts(allProducts);
  resetProductForm();
  renderProductos();
});

const renderProductos = () => {
  const tbody = document.getElementById(
    "productos-tbody",
  ) as HTMLTableSectionElement;
  const products = getAllProducts().filter((p) => !p.eliminado);
  const categories = getAllCategories();

  tbody.innerHTML = "";

  products.forEach((prod) => {
    const catName =
      categories.find((c) => c.id === prod.categoriaId)?.nombre ||
      "Sin categoría";

    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${prod.id}</td>
      <td><img src="${prod.imagen}" alt="${prod.nombre}" /></td>
      <td><strong>${prod.nombre}</strong></td>
      <td>${catName}</td>
      <td>$${prod.precio}</td>
      <td>${prod.stock}</td>
      <td>
        <span class="badge ${prod.disponible ? "badge-ok" : "badge-no"}">
          ${prod.disponible ? "Disponible" : "No disponible"}
        </span>
      </td>
      <td>
        <button class="action-btn edit-btn edit-prod-btn" data-id="${prod.id}">Editar</button>
        <button class="action-btn delete-btn del-prod-btn" data-id="${prod.id}">Eliminar</button>
      </td>
    `;
    tbody.appendChild(tr);
  });

  // Editar producto
  document.querySelectorAll(".edit-prod-btn").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const id = parseInt(
        (e.target as HTMLButtonElement).getAttribute("data-id") || "0",
      );
      const prod = getAllProducts().find((p) => p.id === id);
      if (!prod) return;

      prodIdInput.value = prod.id.toString();
      prodNombreInput.value = prod.nombre;
      prodCategoriaSelect.value = prod.categoriaId.toString();
      prodPrecioInput.value = prod.precio.toString();
      prodStockInput.value = prod.stock.toString();
      prodImagenInput.value = prod.imagen;
      prodDescripcionInput.value = prod.descripcion || "";
      prodDisponibleInput.checked = prod.disponible;

      prodFormTitle.textContent = `Editando Producto #${prod.id}`;
      prodCancelBtn.style.display = "inline-block";
      window.scrollTo({ top: 0, behavior: "smooth" });
    });
  });

  // Baja lógica de producto
  document.querySelectorAll(".del-prod-btn").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const id = parseInt(
        (e.target as HTMLButtonElement).getAttribute("data-id") || "0",
      );
      if (!confirm("¿Seguro que deseas eliminar este producto?")) return;

      const allProducts = getAllProducts();
      const idx = allProducts.findIndex((p) => p.id === id);
      if (idx !== -1) {
        allProducts[idx].eliminado = true;
        saveProducts(allProducts);
        renderProductos();
      }
    });
  });
};

// ==========================================
// 3. CRUD DE CATEGORÍAS
// ==========================================
const formCategoria = document.getElementById(
  "form-categoria",
) as HTMLFormElement;
const catIdInput = document.getElementById("cat-id") as HTMLInputElement;
const catNombreInput = document.getElementById(
  "cat-nombre",
) as HTMLInputElement;
const catDescripcionInput = document.getElementById(
  "cat-descripcion",
) as HTMLInputElement;
const catFormTitle = document.getElementById(
  "cat-form-title",
) as HTMLHeadingElement;
const catCancelBtn = document.getElementById(
  "cat-cancel-btn",
) as HTMLButtonElement;

const resetCategoryForm = () => {
  formCategoria.reset();
  catIdInput.value = "";
  catFormTitle.textContent = "Nueva Categoría";
  catCancelBtn.style.display = "none";
};

catCancelBtn.addEventListener("click", resetCategoryForm);

formCategoria.addEventListener("submit", (e) => {
  e.preventDefault();
  const allCategories = getAllCategories();
  const idValue = catIdInput.value;

  const nombre = catNombreInput.value.trim();
  const descripcion = catDescripcionInput.value.trim();

  if (idValue) {
    // Edición
    const id = parseInt(idValue);
    const idx = allCategories.findIndex((c) => c.id === id);
    if (idx !== -1) {
      allCategories[idx] = { ...allCategories[idx], nombre, descripcion };
    }
  } else {
    // Alta
    const nextId =
      allCategories.length > 0
        ? Math.max(...allCategories.map((c) => c.id)) + 1
        : 1;
    const newCat: ICategoria = {
      id: nextId,
      nombre,
      descripcion,
      eliminado: false,
    };
    allCategories.push(newCat);
  }

  saveCategories(allCategories);
  resetCategoryForm();
  populateCategorySelect();
  renderCategorias();
  renderProductos();
});

const renderCategorias = () => {
  const tbody = document.getElementById(
    "categorias-tbody",
  ) as HTMLTableSectionElement;
  const categories = getCategories();

  tbody.innerHTML = "";

  categories.forEach((cat) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${cat.id}</td>
      <td><strong>${cat.nombre}</strong></td>
      <td>${cat.descripcion || "-"}</td>
      <td>
        <button class="action-btn edit-btn edit-cat-btn" data-id="${cat.id}">Editar</button>
        <button class="action-btn delete-btn del-cat-btn" data-id="${cat.id}">Eliminar</button>
      </td>
    `;
    tbody.appendChild(tr);
  });

  // Editar categoría
  document.querySelectorAll(".edit-cat-btn").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const id = parseInt(
        (e.target as HTMLButtonElement).getAttribute("data-id") || "0",
      );
      const cat = getAllCategories().find((c) => c.id === id);
      if (!cat) return;

      catIdInput.value = cat.id.toString();
      catNombreInput.value = cat.nombre;
      catDescripcionInput.value = cat.descripcion || "";

      catFormTitle.textContent = `Editando Categoría #${cat.id}`;
      catCancelBtn.style.display = "inline-block";
      window.scrollTo({ top: 0, behavior: "smooth" });
    });
  });

  // Baja lógica de categoría
  document.querySelectorAll(".del-cat-btn").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const id = parseInt(
        (e.target as HTMLButtonElement).getAttribute("data-id") || "0",
      );
      if (!confirm("¿Seguro que deseas eliminar esta categoría?")) return;

      const allCategories = getAllCategories();
      const idx = allCategories.findIndex((c) => c.id === id);
      if (idx !== -1) {
        allCategories[idx].eliminado = true;
        saveCategories(allCategories);
        populateCategorySelect();
        renderCategorias();
        renderProductos();
      }
    });
  });
};

// ==========================================
// INICIALIZACIÓN
// ==========================================
const initAdmin = async () => {
  await initData();
  checkAdminAccess();
  populateCategorySelect();
  renderPedidos();
  renderProductos();
  renderCategorias();
};

initAdmin();
