package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import interfaces.ClienteDAO;
import model.Cliente;
import util.JPAUtil;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void registrarCliente(Cliente cliente) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(cliente);
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
    public void actualizarCliente(Cliente cliente) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(cliente);
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
    public Cliente obtenerClientePorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.find(Cliente.class, id);

        } finally {
            manager.close();
        }
    }

    @Override
    public Cliente obtenerClientePorDni(String dni) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT c FROM Cliente c WHERE c.dni = :dni",
                    Cliente.class)
                    .setParameter("dni", dni)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Cliente> obtenerTodosLosClientes() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT c FROM Cliente c",
                    Cliente.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
    
    @Override
    public boolean existeClientePorDni(String dni) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {

            Long cantidad = manager.createQuery(
                    "SELECT COUNT(c) FROM Cliente c " +
                    "WHERE c.dni = :dni",
                    Long.class)
                    .setParameter("dni", dni)
                    .getSingleResult();

            return cantidad > 0;

        } finally {
            manager.close();
        }
    }
}