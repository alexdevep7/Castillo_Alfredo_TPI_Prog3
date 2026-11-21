# 🍔 FoodStore — Trabajo Práctico Integrador (Programación III)

**Institución:** Universidad Tecnológica Nacional (UTN) — Tecnicatura Universitaria en Programación a Distancia  
**Materia:** Programación III  
**Alumno:** Alfredo Castillo  
**Año:** 2026

---

## 🎥 Video Demostrativo y Repositorio

- **Video de Presentación (Acceso Público):** [Ver video explicativo en Google Drive](https://drive.google.com/file/d/1nkG6n2pCAe3e4bKS6yfwOg3Cmogzvsrd/view?usp=sharing)
- **Repositorio en GitHub:** [https://github.com/alexdevep7/Castillo_Alfredo_TPI_Prog3](https://github.com/alexdevep7/Castillo_Alfredo_TPI_Prog3)

---

## 📋 Descripción del Proyecto

**FoodStore** es un sistema integral de gestión de pedidos de comida compuesto por dos partes complementarias[cite: 12]:

1. **Parte 1 — Frontend Web (`frontend/`):** Interfaz web multipágina desarrollada con TypeScript, Vite, HTML5 y CSS3 que consume datos iniciales desde archivos `.json` locales mediante `fetch()` y gestiona la sesión, carrito y operaciones en el navegador, preparada para migrar hacia una API REST[cite: 12, 14].
2. **Parte 2 — Backend JPA / Consola (`backend/`):** Sistema de persistencia y lógica de negocio desarrollado en Java con JPA/Hibernate y base de datos H2 en modo archivo, operado mediante un menú de consola navegable con operaciones CRUD, soft delete, consultas JPQL tipadas y transacciones atómicas[cite: 12, 31, 42].

---

## 📂 Estructura del Proyecto

```text
Castillo_Alfredo_TPI_Prog3/
├── README.md                 # Documentación general y enlace al video demostrativo
├── backend/                  # Proyecto Gradle Java + JPA/Hibernate + H2 (Menú de consola)
│   └── README.md             # Documentación e instrucciones específicas del Backend
└── frontend/                 # Proyecto Vite + TypeScript (Aplicación Web)
    └── README.md             # Documentación, configuración de envío y credenciales del Frontend
```
