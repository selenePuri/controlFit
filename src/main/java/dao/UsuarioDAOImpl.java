package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import interfaces.UsuarioDAO;
import model.Usuario;
import util.JPAUtil;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public void registrarUsuario(Usuario usuario) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(usuario);
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
    public void actualizarUsuario(Usuario usuario) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(usuario);
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
    public Usuario obtenerUsuarioPorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.find(Usuario.class, id);

        } finally {
            manager.close();
        }
    }

    @Override
    public Usuario obtenerUsuarioPorNombre(String nombreUsuario) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT u FROM Usuario u WHERE u.usuario = :usuario",
                    Usuario.class)
                    .setParameter("usuario", nombreUsuario)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;

        } finally {
            manager.close();
        }
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT u FROM Usuario u",
                    Usuario.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }

    @Override
    public Usuario validarLogin(String nombreUsuario, String clave) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT u FROM Usuario u " +
                    "WHERE u.usuario = :usuario " +
                    "AND u.clave = :clave " +
                    "AND u.estado = true",
                    Usuario.class)
                    .setParameter("usuario", nombreUsuario)
                    .setParameter("clave", clave)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;

        } finally {
            manager.close();
        }
    }
    
    @Override
    public boolean existeNombreUsuario(String nombreUsuario) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {

            Long cantidad = manager.createQuery(
                    "SELECT COUNT(u) FROM Usuario u " +
                    "WHERE u.usuario = :nombreUsuario",
                    Long.class)
                    .setParameter("nombreUsuario", nombreUsuario)
                    .getSingleResult();

            return cantidad > 0;

        } finally {
            manager.close();
        }
    }
}