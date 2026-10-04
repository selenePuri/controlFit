package dao;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import interfaces.AsistenciaDAO;
import model.Asistencia;
import util.JPAUtil;

public class AsistenciaDAOImpl implements AsistenciaDAO {

    @Override
    public void registrarAsistencia(Asistencia asistencia) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(asistencia);
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
    public void actualizarAsistencia(Asistencia asistencia) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(asistencia);
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
    public Asistencia obtenerAsistenciaPorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.find(Asistencia.class, id);

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Asistencia> obtenerTodasLasAsistencias() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT a FROM Asistencia a",
                    Asistencia.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Asistencia> obtenerAsistenciasPorCliente(Integer idCliente) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT a FROM Asistencia a " +
                    "WHERE a.cliente.idCliente = :idCliente",
                    Asistencia.class)
                    .setParameter("idCliente", idCliente)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Asistencia> obtenerAsistenciasPorFecha(LocalDate fecha) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT a FROM Asistencia a " +
                    "WHERE a.fecha = :fecha",
                    Asistencia.class)
                    .setParameter("fecha", fecha)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
    
    @Override
    public Asistencia obtenerAsistenciaAbiertaPorCliente(
            Integer idCliente) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {

            List<Asistencia> resultado = manager.createQuery(
                    "SELECT a FROM Asistencia a " +
                    "WHERE a.cliente.idCliente = :idCliente " +
                    "AND a.horaSalida IS NULL " +
                    "ORDER BY a.fecha DESC",
                    Asistencia.class)
                    .setParameter("idCliente", idCliente)
                    .setMaxResults(1)
                    .getResultList();

            return resultado.isEmpty()
                    ? null
                    : resultado.get(0);

        } finally {
            manager.close();
        }
    }
}