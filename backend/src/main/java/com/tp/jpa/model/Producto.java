package com.tp.jpa.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "PRODUCTOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Producto extends Base {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    private String descripcion;
    private int stock;
    private String imagen;

    @Builder.Default
    private Boolean disponible = true;
}