# 🖥️ FoodStore — Frontend Web (TypeScript + Vite)

Interfaz web multipágina de **FoodStore** desarrollada con TypeScript, Vite, HTML5 y CSS3. Consume datos iniciales desde archivos `.json` locales mediante `fetch()` y gestiona la sesión, el carrito de compras y las operaciones del sistema en `localStorage`.

---

## 🎥 Video Demostrativo

- **Presentación del proyecto (Acceso Público):** [Ver video explicativo](https://drive.google.com/file/d/1nkG6n2pCAe3e4bKS6yfwOg3Cmogzvsrd/view?usp=drive_link)

---

## 🛠️ Tecnologías Utilizadas

- **Bundler y Servidor de Desarrollo:** Vite (configuración multipágina)
- **Lenguaje:** TypeScript (tipado estático estricto)
- **Maquetado y Estilos:** HTML5 y CSS3
- **Persistencia en Cliente:** Fetch API + `localStorage`

---

## 📦 Fuente de Datos y Configuración de Envío

- **Archivos JSON iniciales (`public/data/`):** `categorias.json`, `productos.json`, `usuarios.json` y `pedidos.json`. Las llamadas `fetch()` están aisladas en la capa de acceso a datos (`src/data/` y `src/utils/`) para permitir su futura migración directa a los endpoints REST del backend.
- **Constante de Envío:** El costo de envío en el carrito de compras está configurado como una constante fija (`ENVIO = $0` / envío gratuito) declarada en el módulo de carrito (`total = subtotal + envio`).

---

## 📂 Estructura del Proyecto (`frontend/`)

```text
frontend/
├── index.html                        # Redirección inicial según sesión activa
├── package.json                      # Dependencias y scripts (dev, build)
├── tsconfig.json                     # Configuración de TypeScript
├── vite.config.ts                    # Configuración de puntos de entrada multipágina
├── public/data/                      # Datos iniciales en formato JSON
│   ├── categorias.json
│   ├── pedidos.json
│   ├── productos.json
│   └── usuarios.json
└── src/
    ├── data/                         # Capa de acceso a datos y filtrado por baja lógica
    ├── pages/
    │   ├── auth/                     # Inicio de sesión (login) y registro de clientes
    │   ├── client/                   # Catálogo de tienda (home) y carrito de compras (cart)
    │   └── admin/                    # Panel de administración (Pedidos, Productos y Categorías)
    ├── types/                        # Interfaces y Enumeraciones alineadas al backend
    └── utils/                        # Autenticación, gestión de carrito, almacenamiento y rutas
```
