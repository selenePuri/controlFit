package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import interfaces.EmpleadoDAO;
import model.Empleado;
import util.JPAUtil;

public class EmpleadoDAOImpl implements EmpleadoDAO {

    @Override
    public void registrarEmpleado(Empleado empleado) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(empleado);
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
    public void actualizarEmpleado(Empleado empleado) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(empleado);
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
    public Empleado obtenerEmpleadoPorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.find(Empleado.class, id);

        } finally {
            manager.close();
        }
    }

    @Override
    public Empleado obtenerEmpleadoPorDni(String dni) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT e FROM Empleado e WHERE e.dni = :dni",
                    Empleado.class)
                    .setParameter("dni", dni)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Empleado> obtenerTodosLosEmpleados() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT e FROM Empleado e",
                    Empleado.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
}