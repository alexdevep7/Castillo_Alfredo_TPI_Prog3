package com.tp.jpa.repository;

import com.tp.jpa.model.Base;
import com.tp.jpa.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T extends Base> {

    private final Class<T> entityClass;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * Guarda una entidad: si su id es null ejecuta persist() (alta).
     * Si ya tiene id asignado ejecuta merge() (actualización).
     * Retorna la entidad gestionada para garantizar que el ID generado sea legible.
     */
    public T guardar(T entity) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T managedEntity;
            if (entity.getId() == null) {
                em.persist(entity);
                managedEntity = entity;
            } else {
                managedEntity = em.merge(entity);
            }
            tx.commit();
            return managedEntity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al guardar la entidad: " + entityClass.getSimpleName(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Busca la entidad por su clave primaria ID y la retorna dentro de un Optional.
     */
    public Optional<T> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Retorna la lista de entidades activas (eliminado = false).
     */
    public List<T> listarActivos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.eliminado = false";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Realiza la baja lógica asignando eliminado = true sin eliminar físicamente el registro.
     * Retorna false si el registro no existe o ya estaba dado de baja.
     */
    public boolean eliminarLogico(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            T entity = em.find(entityClass, id);
            if (entity == null || entity.isEliminado()) {
                return false;
            }

            tx.begin();
            entity.setEliminado(true);
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error en baja lógica de la entidad ID: " + id, e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}