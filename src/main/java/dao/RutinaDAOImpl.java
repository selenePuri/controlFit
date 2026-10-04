package dao;

import java.util.List;

import javax.persistence.EntityManager;

import interfaces.RutinaDAO;
import model.Rutina;
import util.JPAUtil;

public class RutinaDAOImpl implements RutinaDAO {

    @Override
    public void registrarRutina(Rutina rutina) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(rutina);
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
    public void actualizarRutina(Rutina rutina) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(rutina);
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
    public Rutina obtenerRutinaPorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.find(Rutina.class, id);

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Rutina> obtenerTodasLasRutinas() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT r FROM Rutina r",
                    Rutina.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Rutina> obtenerRutinasPorEntrenador(Integer idEntrenador) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT r FROM Rutina r " +
                    "WHERE r.entrenador.idEntrenador = :idEntrenador",
                    Rutina.class)
                    .setParameter("idEntrenador", idEntrenador)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
    
    @Override
    public List<Rutina> obtenerRutinasActivas() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {

            return manager.createQuery(
                    "SELECT r FROM Rutina r " +
                    "WHERE r.estado = true",
                    Rutina.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
}