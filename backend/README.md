# Evaluación Parcial - Programación III (JPA)

## Descripción del Proyecto
Extensión del Trabajo Práctico de la Unidad 8 implementando repositorios genéricos y específicos para la gestión y persistencia de las entidades `Categoria` y `Producto` mediante JPA/Hibernate. 
El sistema cuenta con una interfaz de consola interactiva para operaciones de Alta, Modificación, Baja Lógica (sin borrado físico) y un módulo de Reportes con consultas JPQL tipadas sobre relaciones unidireccionales `@OneToMany`.

## Arquitectura y Componentes
- `com.tp.jpa.repository.BaseRepository<T>`: Repositorio genérico abstracto con operaciones CRUD (`guardar`, `buscarPorId`, `listarActivos`, `eliminarLogico`) gestionando transacciones y cierres de `EntityManager`.
- `com.tp.jpa.repository.CategoriaRepository`: Extiende de `BaseRepository<Categoria>` e incorpora la consulta tipada JPQL `buscarProductosPorCategoria` y la persistencia relacional atómica en `agregarProducto`.
- `com.tp.jpa.repository.ProductoRepository`: Extiende de `BaseRepository<Producto>`.
- `com.tp.jpa.Main`: Interfaz por consola con menús jerárquicos y validaciones de reglas de negocio.

## Instrucciones de Ejecución
1. Abrir una terminal en la raíz del proyecto.
2. Ejecutar la aplicación con Gradle:
   - En Linux / macOS:
     ```bash
     ./gradlew run
     ```
   - En Windows:
     ```bash
     gradlew.bat run
     ```
3. Alternativamente, ejecutar la clase `com.tp.jpa.Main` directamente desde IntelliJ IDEA.