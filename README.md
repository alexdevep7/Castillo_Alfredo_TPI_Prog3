# FoodStore — Trabajo Práctico Integrador (Programación III)

Aplicación web full-stack para la gestión integral de una tienda gastronómica (**FoodStore**), que contempla el catálogo de productos con control de stock, carrito de compras, gestión de pedidos y panel de administración con control de acceso por roles.

---

## 🛠️ Tecnologías Utilizadas

### Backend

- **Java 17+**
- **Hibernate / JPA** (Mapeo objeto-relacional y manejo de transacciones atómicas)
- **Base de Datos H2** (Persistencia relacional)
- **Maven / Gradle**

### Frontend

- **Vite** (Entorno de desarrollo y empaquetado multipágina)
- **TypeScript** (Tipado estático estricto)
- **HTML5 & CSS3**
- **Fetch API & LocalStorage** (Carga inicial desde archivos JSON estáticos y persistencia en cliente)

---

## 📂 Estructura del Proyecto

```text
Castillo_Alfredo_TPI_Prog3/
├── backend/                  # Modelo de dominio, entidades JPA, DAOs y pruebas de transacciones
└── frontend/                 # Aplicación web cliente/administrador
    ├── public/
    │   └── data/             # Datos iniciales en formato JSON (categorias, productos, usuarios, pedidos)
    ├── src/
    │   ├── data/             # Capa de acceso y persistencia sobre localStorage
    │   ├── pages/
    │   │   ├── admin/        # Panel administrativo (Gestión de Pedidos, CRUD Productos y Categorías)
    │   │   ├── auth/         # Inicio de sesión y registro de usuarios
    │   │   └── client/       # Catálogo con filtros/búsqueda y carrito de compras
    │   ├── types/            # Interfaces y Enums alineados con las entidades del backend
    │   └── utils/            # Autenticación, carrito, protección de rutas y almacenamiento
    ├── index.html
    ├── package.json
    └── vite.config.ts
```
