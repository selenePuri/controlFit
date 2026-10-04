package dao;

import java.util.List;
import java.time.LocalDate;

import javax.persistence.EntityManager;

import interfaces.RutinaClienteDAO;
import model.RutinaCliente;
import util.JPAUtil;

public class RutinaClienteDAOImpl implements RutinaClienteDAO {

    @Override
    public void registrarRutinaCliente(RutinaCliente rutinaCliente) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(rutinaCliente);
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
    public void actualizarRutinaCliente(RutinaCliente rutinaCliente) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(rutinaCliente);
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
    public RutinaCliente obtenerRutinaClientePorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.find(RutinaCliente.class, id);

        } finally {
            manager.close();
        }
    }

    @Override
    public List<RutinaCliente> obtenerTodasLasRutinasCliente() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT rc FROM RutinaCliente rc",
                    RutinaCliente.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<RutinaCliente> obtenerRutinasPorCliente(Integer idCliente) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT rc FROM RutinaCliente rc " +
                    "WHERE rc.cliente.idCliente = :idCliente",
                    RutinaCliente.class)
                    .setParameter("idCliente", idCliente)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<RutinaCliente> obtenerClientesPorRutina(Integer idRutina) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT rc FROM RutinaCliente rc " +
                    "WHERE rc.rutina.idRutina = :idRutina",
                    RutinaCliente.class)
                    .setParameter("idRutina", idRutina)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
    
    @Override
    public RutinaCliente obtenerAsignacionActiva(
            Integer idCliente,
            Integer idRutina,
            LocalDate fecha) {

        EntityManager manager =
                JPAUtil.getEntityManager();

        try {

            List<RutinaCliente> resultado =
                    manager.createQuery(
                        "SELECT rc FROM RutinaCliente rc " +
                        "WHERE rc.cliente.idCliente = :idCliente " +
                        "AND rc.rutina.idRutina = :idRutina " +
                        "AND rc.estado = true " +
                        "AND rc.fechaAsignacion <= :fecha " +
                        "AND (rc.fechaFin IS NULL OR rc.fechaFin >= :fecha)",
                        RutinaCliente.class)
                    .setParameter(
                            "idCliente",
                            idCliente)
                    .setParameter(
                            "idRutina",
                            idRutina)
                    .setParameter(
                            "fecha",
                            fecha)
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