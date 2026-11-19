package com.tp.jpa.repository;

import com.tp.jpa.model.Categoria;
import com.tp.jpa.model.Producto;
import com.tp.jpa.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CategoriaRepository extends BaseRepository<Categoria> {

    public CategoriaRepository() {
        super(Categoria.class);
    }

    // Consulta JPQL: retorna los productos activos de una categoria.
    // Como la relación es unidireccional y Categoria es la dueña, se
    // navega desde Categoria hacia su colección c.productos mediante JOIN.
    // Se filtra por el id de la categoría (parámetro nombrado:catId) y
    // por p.eliminado = false para excluir las bajas lógicas.
    public List<Producto> buscarProductosPorCategoria(Long categoriaId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT p FROM Categoria c JOIN c.productos p "
                    + "WHERE c.id = :catId AND p.eliminado = false";
            TypedQuery<Producto> query = em.createQuery(jpql, Producto.class);
            query.setParameter("catId", categoriaId);
            return query.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Persiste primero el producto para asignarle su ID y luego lo asocia
     * a la colección de la categoría en una única transacción atómica.
     */
    public Producto agregarProducto(Long categoriaId, Producto producto) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Categoria categoria = em.find(Categoria.class, categoriaId);
            if (categoria == null || categoria.isEliminado()) {
                tx.rollback();
                throw new IllegalArgumentException("La categoría indicada no existe o está dada de baja.");
            }

            em.persist(producto);
            categoria.addProducto(producto);

            tx.commit();
            return producto;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}