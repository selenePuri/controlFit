package dao;

import java.util.List;

import javax.persistence.EntityManager;

import interfaces.EntrenadorDAO;
import model.Entrenador;
import util.JPAUtil;

public class EntrenadorDAOImpl implements EntrenadorDAO {

    @Override
    public void registrarEntrenador(Entrenador entrenador) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(entrenador);
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
    public void actualizarEntrenador(Entrenador entrenador) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(entrenador);
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
    public Entrenador obtenerEntrenadorPorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.find(Entrenador.class, id);

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Entrenador> obtenerTodosLosEntrenadores() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT e FROM Entrenador e",
                    Entrenador.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Entrenador> obtenerEntrenadoresPorEspecialidad(String especialidad) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT e FROM Entrenador e " +
                    "WHERE e.especialidad = :especialidad",
                    Entrenador.class)
                    .setParameter("especialidad", especialidad)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
    
    @Override
    public List<Entrenador> obtenerEntrenadoresActivos() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {

            return manager.createQuery(
                    "SELECT e FROM Entrenador e " +
                    "WHERE e.empleado.estado = true",
                    Entrenador.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
}