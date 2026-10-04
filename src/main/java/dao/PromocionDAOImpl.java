package dao;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import interfaces.PromocionDAO;
import model.Promocion;
import util.JPAUtil;

public class PromocionDAOImpl implements PromocionDAO {

    @Override
    public void registrarPromocion(Promocion promocion) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(promocion);
            manager.getTransaction().commit();

        } catch (Exception e) {

            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }

            throw e;

        } finally {
            manager.close();
        }
    }

    @Override
    public void actualizarPromocion(Promocion promocion) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(promocion);
            manager.getTransaction().commit();

        } catch (Exception e) {

            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }

            throw e;

        } finally {
            manager.close();
        }
    }

    @Override
    public Promocion obtenerPromocionPorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT DISTINCT p FROM Promocion p " +
                    "LEFT JOIN FETCH p.tiposMembresia " +
                    "WHERE p.idPromocion = :id",
                    Promocion.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Promocion> obtenerTodasLasPromociones() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT p FROM Promocion p",
                    Promocion.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Promocion> obtenerPromocionesVigentes(LocalDate fecha) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT p FROM Promocion p " +
                    "WHERE p.estado = true " +
                    "AND :fecha BETWEEN p.fechaInicio AND p.fechaFin",
                    Promocion.class)
                    .setParameter("fecha", fecha)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
    
    @Override
    public List<Promocion> obtenerPromocionesVigentesPorTipoMembresia(
            Integer idTipoMembresia, LocalDate fecha) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT DISTINCT p FROM Promocion p " +
                    "JOIN p.tiposMembresia tm " +
                    "WHERE tm.idTipo = :idTipo " +
                    "AND p.estado = true " +
                    "AND :fecha BETWEEN p.fechaInicio AND p.fechaFin",
                    Promocion.class)
                    .setParameter("idTipo", idTipoMembresia)
                    .setParameter("fecha", fecha)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
}