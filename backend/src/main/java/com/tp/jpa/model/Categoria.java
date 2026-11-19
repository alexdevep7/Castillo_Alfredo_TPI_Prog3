package com.tp.jpa.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CATEGORIAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Categoria extends Base {

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();

    public void addProducto(Producto producto) {
        productos.add(producto);
    }
}