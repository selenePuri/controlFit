package dao;

import java.util.List;

import javax.persistence.EntityManager;

import interfaces.PagoDAO;
import model.Pago;
import model.enums.EstadoPago;
import util.JPAUtil;

public class PagoDAOImpl implements PagoDAO {

    @Override
    public void registrarPago(Pago pago) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(pago);
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
    public void actualizarPago(Pago pago) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(pago);
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
    public Pago obtenerPagoPorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.find(Pago.class, id);

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Pago> obtenerTodosLosPagos() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT p FROM Pago p",
                    Pago.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Pago> obtenerPagosPorMembresia(Integer idMembresia) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT p FROM Pago p " +
                    "WHERE p.membresia.idMembresia = :idMembresia",
                    Pago.class)
                    .setParameter("idMembresia", idMembresia)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Pago> obtenerPagosPorEstado(EstadoPago estado) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT p FROM Pago p " +
                    "WHERE p.estado = :estado",
                    Pago.class)
                    .setParameter("estado", estado)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
}