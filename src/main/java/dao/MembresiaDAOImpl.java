package dao;

import java.util.List;
import java.time.LocalDate;

import javax.persistence.EntityManager;

import interfaces.MembresiaDAO;
import model.Membresia;
import model.enums.EstadoMembresia;
import util.JPAUtil;

public class MembresiaDAOImpl implements MembresiaDAO {

    @Override
    public void registrarMembresia(Membresia membresia) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(membresia);
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
    public void actualizarMembresia(Membresia membresia) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(membresia);
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
    public Membresia obtenerMembresiaPorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.find(Membresia.class, id);

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Membresia> obtenerTodasLasMembresias() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT m FROM Membresia m",
                    Membresia.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Membresia> obtenerMembresiasPorCliente(Integer idCliente) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT m FROM Membresia m " +
                    "WHERE m.cliente.idCliente = :idCliente",
                    Membresia.class)
                    .setParameter("idCliente", idCliente)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Membresia> obtenerMembresiasPorEstado(
            EstadoMembresia estado) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT m FROM Membresia m " +
                    "WHERE m.estado = :estado",
                    Membresia.class)
                    .setParameter("estado", estado)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
    
    @Override
    public Membresia obtenerMembresiaVigentePorCliente(
            Integer idCliente,
            LocalDate fecha) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {

            List<Membresia> resultado = manager.createQuery(
                    "SELECT m FROM Membresia m " +
                    "WHERE m.cliente.idCliente = :idCliente " +
                    "AND m.estado = :estado " +
                    "AND :fecha BETWEEN m.fechaInicio AND m.fechaFin",
                    Membresia.class)
                    .setParameter("idCliente", idCliente)
                    .setParameter("estado", EstadoMembresia.ACTIVA)
                    .setParameter("fecha", fecha)
                    .setMaxResults(1)
                    .getResultList();

            return resultado.isEmpty()
                    ? null
                    : resultado.get(0);

        } finally {
            manager.close();
        }
    }
    
    @Override
    public boolean existeMembresiaActivaEnPeriodo(
            Integer idCliente,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {

            Long cantidad = manager.createQuery(
                    "SELECT COUNT(m) FROM Membresia m " +
                    "WHERE m.cliente.idCliente = :idCliente " +
                    "AND m.estado = :estado " +
                    "AND m.fechaInicio <= :fechaFin " +
                    "AND m.fechaFin >= :fechaInicio",
                    Long.class)
                    .setParameter("idCliente", idCliente)
                    .setParameter("estado", EstadoMembresia.ACTIVA)
                    .setParameter("fechaInicio", fechaInicio)
                    .setParameter("fechaFin", fechaFin)
                    .getSingleResult();

            return cantidad > 0;

        } finally {
            manager.close();
        }
    }
}