# ⚙️ FoodStore — Backend (JPA + Hibernate + H2)

Módulo backend en consola desarrollado en Java con JPA, Hibernate y base de datos relacional H2 embebida en archivo para la gestión integral de categorías, productos, usuarios y pedidos[cite: 31, 35].

---

## 🛠️ Tecnologías Utilizadas
* **Lenguaje:** Java 17+
* **ORM / Persistencia:** JPA / Hibernate (`jakarta.persistence`)[cite: 31]
* **Base de Datos:** H2 Database en modo archivo (`jdbc:h2:file:./data/jpa_db`)
* **Gestor de Proyecto:** Gradle[cite: 35]

---

## 🏗️ Arquitectura y Estructura (`com.tp.jpa`)

```text
backend/
├── build.gradle
├── data/                             # Base de datos H2 persistida en archivo
└── src/main/
    ├── java/com/tp/jpa/
    │   ├── Main.java                 # Menú de consola principal y submenús de gestión
    │   ├── model/                    # Entidades JPA (Base, Calculable, Categoria, Producto, Usuario, Pedido, DetallePedido)
    │   ├── model/enums/              # Enumeraciones (Rol, Estado, FormaPago)
    │   ├── repository/               # Patrón Repository genérico (BaseRepository) y específicos
    │   └── util/                     # JPAUtil (gestión Singleton de EntityManagerFactory)
    └── resources/META-INF/
        └── persistence.xml           # Configuración de la unidad de persistencia con Hibernate