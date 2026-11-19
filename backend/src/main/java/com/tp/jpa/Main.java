package com.tp.jpa;

import com.tp.jpa.model.Categoria;
import com.tp.jpa.model.Pedido;
import com.tp.jpa.model.Producto;
import com.tp.jpa.model.Usuario;
import com.tp.jpa.model.enums.Estado;
import com.tp.jpa.model.enums.FormaPago;
import com.tp.jpa.repository.CategoriaRepository;
import com.tp.jpa.repository.PedidoRepository;
import com.tp.jpa.repository.ProductoRepository;
import com.tp.jpa.repository.UsuarioRepository;
import com.tp.jpa.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CategoriaRepository categoriaRepo = new CategoriaRepository();
    private static final ProductoRepository productoRepo = new ProductoRepository();
    private static final UsuarioRepository usuarioRepo = new UsuarioRepository();
    private static final PedidoRepository pedidoRepo = new PedidoRepository();

    public static void main(String[] args) {
        int opcion;
        do {
            System.out.println("\n========================================");
            System.out.println("            MENÚ PRINCIPAL              ");
            System.out.println("========================================");
            System.out.println("1. Gestionar Categorías");
            System.out.println("2. Gestionar Productos");
            System.out.println("3. Gestionar Usuarios");
            System.out.println("4. Gestionar Pedidos");
            System.out.println("5. Reportes");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> menuCategorias();
                case 2 -> menuProductos();
                case 3 -> menuUsuarios();
                case 4 -> menuPedidos();
                case 5 -> menuReportes();
                case 0 -> {
                    System.out.println("Cerrando EntityManagerFactory y finalizando aplicación...");
                    JPAUtil.close();
                }
                default -> System.out.println("Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 0);
    }

    // ==========================================
    // 1. SUBMENÚ CATEGORÍAS
    // ==========================================
    private static void menuCategorias() {
        int opcion;
        do {
            System.out.println("\n----------------------------------------");
            System.out.println("         GESTIÓN DE CATEGORÍAS          ");
            System.out.println("----------------------------------------");
            System.out.println("1. Alta de categoría");
            System.out.println("2. Modificar categoría");
            System.out.println("3. Baja lógica de categoría");
            System.out.println("4. Listar categorías activas");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> altaCategoria();
                case 2 -> modificarCategoria();
                case 3 -> bajaCategoria();
                case 4 -> listarCategorias();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private static void altaCategoria() {
        System.out.println("\n[Alta de Categoría]");
        System.out.print("Ingrese nombre (obligatorio): ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("Error: El nombre no puede estar vacío.");
            return;
        }

        System.out.print("Ingrese descripción (opcional): ");
        String descripcion = scanner.nextLine().trim();

        Categoria categoria = Categoria.builder()
                .nombre(nombre)
                .descripcion(descripcion.isEmpty() ? null : descripcion)
                .build();

        Categoria guardada = categoriaRepo.guardar(categoria);
        System.out.println("Categoría dada de alta con éxito. ID generado: " + guardada.getId());
    }

    private static void modificarCategoria() {
        System.out.println("\n[Modificar Categoría]");
        List<Categoria> activas = categoriaRepo.listarActivos();
        if (activas.isEmpty()) {
            System.out.println("No hay categorías activas para modificar.");
            return;
        }

        listarCategorias();
        System.out.print("Ingrese el ID de la categoría a modificar: ");
        Long id = leerLong();

        Optional<Categoria> optCat = categoriaRepo.buscarPorId(id);
        if (optCat.isEmpty() || optCat.get().isEliminado()) {
            System.out.println("Error: La categoría con ID " + id + " no existe o se encuentra inactiva.");
            return;
        }

        Categoria categoria = optCat.get();
        System.out.println("Valores actuales:");
        System.out.println(" - Nombre actual: " + categoria.getNombre());
        System.out.println(" - Descripción actual: " + (categoria.getDescripcion() != null ? categoria.getDescripcion() : "(sin descripción)"));

        System.out.print("Nuevo nombre (Enter para conservar): ");
        String nuevoNombre = scanner.nextLine().trim();
        if (!nuevoNombre.isEmpty()) {
            categoria.setNombre(nuevoNombre);
        }

        System.out.print("Nueva descripción (Enter para conservar): ");
        String nuevaDescripcion = scanner.nextLine().trim();
        if (!nuevaDescripcion.isEmpty()) {
            categoria.setDescripcion(nuevaDescripcion);
        }

        categoriaRepo.guardar(categoria);
        System.out.println("Categoría ID " + categoria.getId() + " actualizada con éxito.");
    }

    private static void bajaCategoria() {
        System.out.println("\n[Baja Lógica de Categoría]");
        System.out.print("Ingrese el ID de la categoría a dar de baja: ");
        Long id = leerLong();

        Optional<Categoria> optCat = categoriaRepo.buscarPorId(id);
        if (optCat.isEmpty() || optCat.get().isEliminado()) {
            System.out.println("Error: Categoría no encontrada o ya dada de baja.");
            return;
        }

        Categoria categoria = optCat.get();
        List<Producto> productosActivos = categoriaRepo.buscarProductosPorCategoria(id);
        if (!productosActivos.isEmpty()) {
            System.out.println("Error: No es posible dar de baja la categoría '" + categoria.getNombre()
                    + "' porque cuenta con " + productosActivos.size() + " producto(s) activo(s).");
            return;
        }

        boolean eliminada = categoriaRepo.eliminarLogico(id);
        if (eliminada) {
            System.out.println("Categoría '" + categoria.getNombre() + "' dada de baja correctamente.");
        } else {
            System.out.println("Error: No se pudo completar la baja lógica.");
        }
    }

    private static void listarCategorias() {
        System.out.println("\n--- Categorías Activas ---");
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No se registran categorías activas.");
            return;
        }
        for (Categoria c : categorias) {
            System.out.println("ID: " + c.getId()
                    + " | Nombre: " + c.getNombre()
                    + " | Descripción: " + (c.getDescripcion() != null ? c.getDescripcion() : "-"));
        }
    }

    // ==========================================
    // 2. SUBMENÚ PRODUCTOS
    // ==========================================
    private static void menuProductos() {
        int opcion;
        do {
            System.out.println("\n----------------------------------------");
            System.out.println("          GESTIÓN DE PRODUCTOS          ");
            System.out.println("----------------------------------------");
            System.out.println("1. Alta de producto");
            System.out.println("2. Modificar producto");
            System.out.println("3. Baja lógica de producto");
            System.out.println("4. Listar productos");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> altaProducto();
                case 2 -> modificarProducto();
                case 3 -> bajaProducto();
                case 4 -> listarProductos();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private static void altaProducto() {
        System.out.println("\n[Alta de Producto]");
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("Error: No existen categorías activas disponibles. Registre una categoría antes de continuar.");
            return;
        }

        listarCategorias();
        System.out.print("Ingrese el ID de la categoría a asociar: ");
        Long idCategoria = leerLong();

        Optional<Categoria> optCat = categoriaRepo.buscarPorId(idCategoria);
        if (optCat.isEmpty() || optCat.get().isEliminado()) {
            System.out.println("Error: La categoría ingresada no existe o está inactiva.");
            return;
        }

        System.out.print("Nombre (obligatorio): ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("Error: El nombre es obligatorio.");
            return;
        }

        System.out.print("Descripción (opcional): ");
        String descripcion = scanner.nextLine().trim();

        System.out.print("Precio (mayor a 0): ");
        Double precio = leerDouble();
        if (precio == null || precio <= 0) {
            System.out.println("Error: El precio debe ser un número mayor a 0.");
            return;
        }

        System.out.print("Stock (mayor o igual a 0): ");
        Integer stock = leerInteger();
        if (stock == null || stock < 0) {
            System.out.println("Error: El stock debe ser mayor o igual a 0.");
            return;
        }

        System.out.print("Imagen (opcional): ");
        String imagen = scanner.nextLine().trim();

        System.out.print("Disponible (S/N, por defecto S): ");
        String dispInput = scanner.nextLine().trim().toUpperCase();
        boolean disponible = !dispInput.equals("N");

        Producto nuevoProducto = Producto.builder()
                .nombre(nombre)
                .descripcion(descripcion.isEmpty() ? null : descripcion)
                .precio(precio)
                .stock(stock)
                .imagen(imagen.isEmpty() ? null : imagen)
                .disponible(disponible)
                .build();

        try {
            Producto guardado = categoriaRepo.agregarProducto(idCategoria, nuevoProducto);
            System.out.println("Producto registrado exitosamente. ID: " + guardado.getId()
                    + " | Categoría asignada: " + optCat.get().getNombre());
        } catch (Exception e) {
            System.out.println("Error al asociar el producto: " + e.getMessage());
        }
    }

    private static void modificarProducto() {
        System.out.println("\n[Modificar Producto]");
        List<Producto> activos = productoRepo.listarActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay productos activos para modificar.");
            return;
        }

        System.out.println("Productos activos:");
        for (Producto p : activos) {
            System.out.println("ID: " + p.getId() + " | Nombre: " + p.getNombre() + " | Precio: $" + p.getPrecio() + " | Stock: " + p.getStock());
        }

        System.out.print("Ingrese el ID del producto a modificar: ");
        Long id = leerLong();

        Optional<Producto> optProd = productoRepo.buscarPorId(id);
        if (optProd.isEmpty() || optProd.get().isEliminado()) {
            System.out.println("Error: El producto no existe o está dado de baja.");
            return;
        }

        Producto producto = optProd.get();
        System.out.println("Valores actuales:");
        System.out.println(" - Nombre: " + producto.getNombre());
        System.out.println(" - Precio: $" + producto.getPrecio());
        System.out.println(" - Stock: " + producto.getStock());

        System.out.print("Nuevo nombre (Enter para conservar): ");
        String nuevoNombre = scanner.nextLine().trim();
        if (!nuevoNombre.isEmpty()) {
            producto.setNombre(nuevoNombre);
        }

        System.out.print("Nuevo precio (Enter para conservar): ");
        String nuevoPrecioStr = scanner.nextLine().trim();
        if (!nuevoPrecioStr.isEmpty()) {
            try {
                double nuevoPrecio = Double.parseDouble(nuevoPrecioStr);
                if (nuevoPrecio <= 0) {
                    System.out.println("Error: El precio debe ser mayor a 0. Operación cancelada.");
                    return;
                }
                producto.setPrecio(nuevoPrecio);
            } catch (NumberFormatException e) {
                return;
            }
        }

        System.out.print("Nuevo stock (Enter para conservar): ");
        String nuevoStockStr = scanner.nextLine().trim();
        if (!nuevoStockStr.isEmpty()) {
            try {
                int nuevoStock = Integer.parseInt(nuevoStockStr);
                if (nuevoStock < 0) {
                    System.out.println("Error: El stock debe ser mayor o igual a 0. Operación cancelada.");
                    return;
                }
                producto.setStock(nuevoStock);
            } catch (NumberFormatException e) {
                System.out.println("Error: Stock numérico inválido. Operación cancelada.");
                return;
            }
        }

        productoRepo.guardar(producto);
        System.out.println("Producto ID " + producto.getId() + " actualizado con éxito.");
    }

    private static void bajaProducto() {
        System.out.println("\n[Baja Lógica de Producto]");
        System.out.print("Ingrese el ID del producto a dar de baja: ");
        Long id = leerLong();

        Optional<Producto> optProd = productoRepo.buscarPorId(id);
        if (optProd.isEmpty() || optProd.get().isEliminado()) {
            System.out.println("Error: El producto no existe o ya está dado de baja.");
            return;
        }

        String nombre = optProd.get().getNombre();
        boolean bajaOk = productoRepo.eliminarLogico(id);
        if (bajaOk) {
            System.out.println("Producto '" + nombre + "' dado de baja con éxito.");
        } else {
            System.out.println("Error: No se pudo completar la baja lógica.");
        }
    }

    private static void listarProductos() {
        System.out.println("\n--- LISTADO DE PRODUCTOS ACTIVOS ---");
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No se encontraron categorías activas.");
            return;
        }

        boolean seEncontraronProductos = false;
        for (Categoria cat : categorias) {
            List<Producto> productos = categoriaRepo.buscarProductosPorCategoria(cat.getId());
            for (Producto p : productos) {
                seEncontraronProductos = true;
                System.out.println("ID: " + p.getId()
                        + " | Nombre: " + p.getNombre()
                        + " | Precio: $" + p.getPrecio()
                        + " | Stock: " + p.getStock()
                        + " | Disponible: " + (Boolean.TRUE.equals(p.getDisponible()) ? "Sí" : "No")
                        + " | Categoría: " + cat.getNombre());
            }
        }

        if (!seEncontraronProductos) {
            System.out.println("No hay productos activos registrados.");
        }
    }

    // ==========================================
    // 3. SUBMENÚ USUARIOS
    // ==========================================
    private static void menuUsuarios() {
        int opcion;
        do {
            System.out.println("\n----------------------------------------");
            System.out.println("          GESTIÓN DE USUARIOS           ");
            System.out.println("----------------------------------------");
            System.out.println("1. Alta de usuario");
            System.out.println("2. Modificar usuario");
            System.out.println("3. Baja lógica de usuario");
            System.out.println("4. Listar usuarios");
            System.out.println("5. Buscar por mail");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> altaUsuario();
                case 2 -> modificarUsuario();
                case 3 -> bajaUsuario();
                case 4 -> listarUsuarios();
                case 5 -> buscarUsuarioPorMail();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private static void altaUsuario() {
        System.out.println("\n[Alta de Usuario]");
        System.out.print("Mail (único): ");
        String mail = scanner.nextLine().trim();

        if (usuarioRepo.buscarPorMail(mail).isPresent()) {
            System.out.println("Error: El mail ingresado ya se encuentra registrado y activo.");
            return;
        }

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("Celular (opcional): ");
        String celular = scanner.nextLine().trim();
        System.out.print("Contraseña: ");
        String pass = scanner.nextLine().trim();

        System.out.print("Rol (1. ADMIN / 2. USUARIO): ");
        int opRol = leerEntero();
        com.tp.jpa.model.enums.Rol rol = (opRol == 1) ? com.tp.jpa.model.enums.Rol.ADMIN : com.tp.jpa.model.enums.Rol.USUARIO;

        Usuario nuevo = Usuario.builder()
                .nombre(nombre).apellido(apellido).mail(mail)
                .celular(celular.isEmpty() ? null : celular)
                .contraseña(pass).rol(rol)
                .build();

        Usuario guardado = usuarioRepo.guardar(nuevo);
        System.out.println("Usuario registrado. ID generado: " + guardado.getId());
    }

    private static void modificarUsuario() {
        System.out.println("\n[Modificar Usuario]");
        listarUsuarios();
        System.out.print("Ingrese ID del usuario: ");
        Long id = leerLong();

        Optional<Usuario> optUser = usuarioRepo.buscarPorId(id);
        if (optUser.isEmpty() || optUser.get().isEliminado()) {
            System.out.println("Error: Usuario no encontrado o inactivo.");
            return;
        }
        Usuario u = optUser.get();

        System.out.print("Nuevo nombre (Enter conserva '" + u.getNombre() + "'): ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) u.setNombre(nombre);

        System.out.print("Nuevo apellido (Enter conserva '" + u.getApellido() + "'): ");
        String apellido = scanner.nextLine().trim();
        if (!apellido.isEmpty()) u.setApellido(apellido);

        System.out.print("Nuevo mail (Enter conserva '" + u.getMail() + "'): ");
        String mail = scanner.nextLine().trim();
        if (!mail.isEmpty() && !mail.equals(u.getMail())) {
            Optional<Usuario> existente = usuarioRepo.buscarPorMail(mail);
            if (existente.isPresent() && !existente.get().getId().equals(u.getId())) {
                System.out.println("Error: El mail ya está en uso. Operación cancelada.");
                return;
            }
            u.setMail(mail);
        }

        System.out.print("Nueva contraseña (Enter para conservar): ");
        String pass = scanner.nextLine().trim();
        if (!pass.isEmpty()) u.setContraseña(pass);

        usuarioRepo.guardar(u);
        System.out.println("Usuario actualizado.");
    }

    private static void bajaUsuario() {
        System.out.println("\n[Baja de Usuario]");
        System.out.print("Ingrese ID: ");
        Long id = leerLong();
        Optional<Usuario> optUser = usuarioRepo.buscarPorId(id);

        if (usuarioRepo.eliminarLogico(id)) {
            System.out.println("Usuario '" + optUser.get().getNombre() + " " + optUser.get().getApellido() + "' dado de baja. Sus pedidos se conservan.");
        } else {
            System.out.println("Error: Usuario no encontrado o inactivo.");
        }
    }

    private static void listarUsuarios() {
        System.out.println("\n--- Usuarios Activos ---");
        List<Usuario> usuarios = usuarioRepo.listarActivos();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        for (Usuario u : usuarios) {
            System.out.println("ID: " + u.getId() + " | " + u.getNombre() + " " + u.getApellido() + " | Mail: " + u.getMail() + " | Rol: " + u.getRol());
        }
    }

    private static void buscarUsuarioPorMail() {
        System.out.print("\nIngrese mail a buscar: ");
        String mail = scanner.nextLine().trim();
        Optional<Usuario> u = usuarioRepo.buscarPorMail(mail);
        if (u.isPresent()) {
            System.out.println("ID: " + u.get().getId() + " | " + u.get().getNombre() + " " + u.get().getApellido() + " | Cel: " + u.get().getCelular() + " | Rol: " + u.get().getRol());
        } else {
            System.out.println("No existe usuario activo con ese mail.");
        }
    }


    // ==========================================
    // 4. SUBMENÚ PEDIDOS
    // ==========================================
    private static void menuPedidos() {
        int opcion;
        do {
            System.out.println("\n----------------------------------------");
            System.out.println("          GESTIÓN DE PEDIDOS            ");
            System.out.println("----------------------------------------");
            System.out.println("1. Alta de pedido");
            System.out.println("2. Cambiar estado de pedido");
            System.out.println("3. Baja lógica de pedido");
            System.out.println("4. Listar pedidos activos");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> altaPedido();
                case 2 -> cambiarEstadoPedido(); // Aquí usamos pedidoRepo
                case 3 -> bajaPedido();          // Aquí usamos pedidoRepo
                case 4 -> listarPedidos();       // Aquí usamos pedidoRepo
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private static void cambiarEstadoPedido() {
        System.out.println("\n[Cambiar Estado de Pedido]");
        System.out.print("Ingrese el ID del pedido: ");
        Long id = leerLong();

        Optional<com.tp.jpa.model.Pedido> optPedido = pedidoRepo.buscarPorId(id);
        if (optPedido.isEmpty() || optPedido.get().isEliminado()) {
            System.out.println("Error: El pedido no existe o está dado de baja.");
            return;
        }

        com.tp.jpa.model.Pedido pedido = optPedido.get();
        System.out.println("Estado actual: " + pedido.getEstado());
        System.out.println("Nuevos estados: 1. PENDIENTE | 2. CONFIRMADO | 3. TERMINADO | 4. CANCELADO");
        System.out.print("Seleccione el nuevo estado: ");
        int opEstado = leerEntero();

        com.tp.jpa.model.enums.Estado nuevoEstado = switch (opEstado) {
            case 1 -> com.tp.jpa.model.enums.Estado.PENDIENTE;
            case 2 -> com.tp.jpa.model.enums.Estado.CONFIRMADO;
            case 3 -> com.tp.jpa.model.enums.Estado.TERMINADO;
            case 4 -> com.tp.jpa.model.enums.Estado.CANCELADO;
            default -> null;
        };

        if (nuevoEstado == null) {
            System.out.println("Opción inválida. Operación cancelada.");
            return;
        }

        pedido.setEstado(nuevoEstado);
        pedidoRepo.guardar(pedido);
        System.out.println("Pedido ID " + pedido.getId() + " actualizado a estado " + nuevoEstado + ".");
    }

    private static void bajaPedido() {
        System.out.println("\n[Baja Lógica de Pedido]");
        System.out.print("Ingrese el ID del pedido a dar de baja: ");
        Long id = leerLong();

        Optional<com.tp.jpa.model.Pedido> optPedido = pedidoRepo.buscarPorId(id);
        if (optPedido.isEmpty() || optPedido.get().isEliminado()) {
            System.out.println("Error: Pedido no encontrado o ya dado de baja.");
            return;
        }

        Double total = optPedido.get().getTotal();
        boolean ok = pedidoRepo.eliminarLogico(id);
        if (ok) {
            System.out.println("Pedido ID " + id + " con total $" + total + " dado de baja correctamente.");
            System.out.println("Nota: El stock de los productos no ha sido restaurado.");
        }
    }

    private static void listarPedidos() {
        System.out.println("\n--- LISTADO DE PEDIDOS ---");
        List<com.tp.jpa.model.Pedido> pedidos = pedidoRepo.listarActivos();
        if (pedidos.isEmpty()) {
            System.out.println("No se registran pedidos activos.");
            return;
        }
        for (com.tp.jpa.model.Pedido p : pedidos) {
            System.out.println("ID: " + p.getId()
                    + " | Fecha: " + p.getFecha()
                    + " | Estado: " + p.getEstado()
                    + " | Pago: " + p.getFormaPago()
                    + " | Total: $" + p.getTotal());
        }
    }

    private static void altaPedido() {
        System.out.println("\n[Alta de Pedido]");

        // 1. Listar usuarios activos (debes tener instanciado usuarioRepo)
        List<Usuario> usuarios = usuarioRepo.listarActivos();
        if (usuarios.isEmpty()) {
            System.out.println("Error: No existen usuarios activos. Registre uno antes de continuar.");
            return;
        }
        System.out.println("Usuarios activos:");
        for (Usuario u : usuarios) {
            System.out.println("ID: " + u.getId() + " | Nombre: " + u.getNombre() + " " + u.getApellido());
        }
        System.out.print("Ingrese el ID del usuario: ");
        Long idUsuario = leerLong();

        // 2. Seleccionar forma de pago
        System.out.println("\nFormas de pago:");
        System.out.println("1. TARJETA");
        System.out.println("2. TRANSFERENCIA");
        System.out.println("3. EFECTIVO");
        System.out.print("Seleccione la forma de pago (1-3): ");
        int opPago = leerEntero();
        FormaPago formaPago = (opPago == 1) ? FormaPago.TARJETA : (opPago == 2) ? FormaPago.TRANSFERENCIA : FormaPago.EFECTIVO;

        // 3. Crear lista temporal en memoria (HashMap: idProducto -> cantidad)
        // Solo guarda los IDs para no mezclar EntityManagers
        java.util.Map<Long, Integer> carrito = new java.util.HashMap<>();
        boolean agregarMas = true;

        while (agregarMas) {
            listarProductos();
            System.out.print("\nIngrese el ID del producto a agregar: ");
            Long idProducto = leerLong();

            Optional<Producto> optProd = productoRepo.buscarPorId(idProducto);
            if (optProd.isEmpty() || optProd.get().isEliminado() || !optProd.get().getDisponible()) {
                System.out.println("Error: El producto no existe, está dado de baja o no está disponible.");
                continue;
            }

            Producto prod = optProd.get();
            System.out.print("Ingrese la cantidad: ");
            int cantidad = leerEntero();

            if (cantidad <= 0 || cantidad > prod.getStock()) {
                System.out.println("Error: Cantidad inválida o supera el stock disponible (" + prod.getStock() + ").");
                continue;
            }

            // Agrega al carrito temporal
            carrito.put(idProducto, carrito.getOrDefault(idProducto, 0) + cantidad);

            System.out.print("¿Desea agregar otro producto? (S/N): ");
            agregarMas = scanner.nextLine().trim().equalsIgnoreCase("S");
        }

        if (carrito.isEmpty()) {
            System.out.println("Operación cancelada: El pedido debe contener al menos un producto.");
            return;
        }

        // 4. TRANSACCIÓN ATÓMICA
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 4.1 Recuperar el usuario gestionado
            Usuario usuario = em.find(Usuario.class, idUsuario);
            if (usuario == null || usuario.isEliminado()) {
                throw new IllegalArgumentException("El usuario seleccionado ya no es válido.");
            }

            // 4.2 Instanciar el Pedido
            Pedido pedido = Pedido.builder()
                    .fecha(java.time.LocalDate.now())
                    .estado(Estado.PENDIENTE)
                    .formaPago(formaPago)
                    .build();

            // 4.3 Procesar el carrito temporal
            for (java.util.Map.Entry<Long, Integer> entry : carrito.entrySet()) {
                Long pId = entry.getKey();
                int qty = entry.getValue();

                // Recuperar producto gestionado en ESTE EntityManager
                Producto pGestionado = em.find(Producto.class, pId);

                // Re-validar todo dentro de la transacción
                if (pGestionado == null || pGestionado.isEliminado() || !pGestionado.getDisponible() || pGestionado.getStock() < qty) {
                    throw new IllegalStateException("El producto ID " + pId + " cambió de estado o se quedó sin stock durante la transacción.");
                }

                // Generar detalle (calcula subtotal internamente)
                pedido.addDetallePedido(qty, pGestionado);

                // Reducir stock (se persiste solo al hacer commit porque pGestionado está atado al em)
                pGestionado.setStock(pGestionado.getStock() - qty);
            }

            // 4.4 Calcular total y asociar al usuario
            pedido.calcularTotal();
            em.persist(pedido); // Persistir el pedido (por cascada guarda los detalles)
            usuario.getPedidos().add(pedido); // Asocia unidireccionalmente el pedido al usuario

            tx.commit(); // Si todo sale bien, impacta en BD

            // 5. Confirmación en consola
            System.out.println("\n--- PEDIDO CREADO EXITOSAMENTE ---");
            System.out.println("ID Pedido: " + pedido.getId());
            System.out.println("Usuario: " + usuario.getNombre() + " " + usuario.getApellido());
            System.out.println("Fecha: " + pedido.getFecha());
            System.out.println("Total a Pagar: $" + pedido.getTotal());

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.out.println("ERROR: Se canceló el pedido completo. Causa: " + e.getMessage());
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }


    // ==========================================
    // 5. SUBMENÚ REPORTES
    // ==========================================
    private static void menuReportes() {
        int opcion;
        do {
            System.out.println("\n----------------------------------------");
            System.out.println("                REPORTES                ");
            System.out.println("----------------------------------------");
            System.out.println("1. Productos por categoría (JPQL)");
            System.out.println("2. Pedidos por usuario (JPQL)");
            System.out.println("3. Pedidos por estado (JPQL)");
            System.out.println("4. Total facturado");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> reporteProductosPorCategoria();
                case 2 -> reportePedidosPorUsuario();
                case 3 -> reportePedidosPorEstado();
                case 4 -> reporteTotalFacturado();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private static void reporteProductosPorCategoria() {
        System.out.println("\n[Reporte: Consulta JPQL de Productos por Categoría]");
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías activas disponibles para consultar.");
            return;
        }

        listarCategorias();
        System.out.print("Seleccione el ID de la categoría a consultar: ");
        Long catId = leerLong();

        Optional<Categoria> optCat = categoriaRepo.buscarPorId(catId);
        if (optCat.isEmpty() || optCat.get().isEliminado()) {
            System.out.println("Error: La categoría ingresada no existe o está dada de baja.");
            return;
        }

        List<Producto> productos = categoriaRepo.buscarProductosPorCategoria(catId);
        System.out.println("\nProductos activos en la categoría '" + optCat.get().getNombre() + "':");
        if (productos.isEmpty()) {
            System.out.println("No hay productos activos registrados para esta categoría.");
        } else {
            for (Producto p : productos) {
                System.out.println("ID: " + p.getId()
                        + " | Nombre: " + p.getNombre()
                        + " | Precio: $" + p.getPrecio()
                        + " | Stock: " + p.getStock());
            }
        }
    }

    private static void reportePedidosPorUsuario() {
        listarUsuarios();
        System.out.print("Ingrese el ID del usuario: ");
        Long id = leerLong();
        List<Pedido> pedidos = usuarioRepo.buscarPedidosPorUsuario(id);
        if (pedidos.isEmpty()) {
            System.out.println("El usuario no tiene pedidos activos.");
            return;
        }
        for (Pedido p : pedidos) {
            System.out.println("ID: " + p.getId() + " | Fecha: " + p.getFecha() + " | Estado: " + p.getEstado() + " | Total: $" + p.getTotal());
        }
    }

    private static void reportePedidosPorEstado() {
        System.out.println("Estados: 1. PENDIENTE | 2. CONFIRMADO | 3. TERMINADO | 4. CANCELADO");
        System.out.print("Seleccione estado: ");
        int op = leerEntero();
        Estado est = (op==1)?Estado.PENDIENTE : (op==2)?Estado.CONFIRMADO : (op==3)?Estado.TERMINADO : Estado.CANCELADO;

        List<Pedido> pedidos = pedidoRepo.buscarPorEstado(est);
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos en estado " + est + ".");
            return;
        }
        for (Pedido p : pedidos) {
            System.out.println("ID: " + p.getId() + " | Fecha: " + p.getFecha() + " | Pago: " + p.getFormaPago() + " | Total: $" + p.getTotal());
        }
    }

    private static void reporteTotalFacturado() {
        List<Pedido> terminados = pedidoRepo.buscarPorEstado(Estado.TERMINADO);
        double total = terminados.stream()
                .mapToDouble(p -> p.getTotal() != null ? p.getTotal() : 0.0)
                .sum();
        System.out.printf(java.util.Locale.US, "\nTotal facturado: $%.2f%n", total);
    }


    // ==========================================
    // MÉTODOS AUXILIARES DE ENTRADA
    // ==========================================
    private static int leerEntero() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese un valor numérico entero: ");
            }
        }
    }

    private static Long leerLong() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese un ID válido: ");
            }
        }
    }

    private static Double leerDouble() {
        try {
            String input = scanner.nextLine().trim();
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer leerInteger() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}