package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import interfaces.TipoMembresiaDAO;
import model.TipoMembresia;
import model.enums.TipoPlan;
import util.JPAUtil;

public class TipoMembresiaDAOImpl implements TipoMembresiaDAO {

    @Override
    public void registrarTipoMembresia(TipoMembresia tipoMembresia) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.persist(tipoMembresia);
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
    public void actualizarTipoMembresia(TipoMembresia tipoMembresia) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            manager.getTransaction().begin();
            manager.merge(tipoMembresia);
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
    public TipoMembresia obtenerTipoMembresiaPorId(Integer id) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.find(TipoMembresia.class, id);

        } finally {
            manager.close();
        }
    }

    @Override
    public TipoMembresia obtenerTipoMembresiaPorNombre(TipoPlan nombre) {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT t FROM TipoMembresia t " +
                    "WHERE t.nombre = :nombre",
                    TipoMembresia.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;

        } finally {
            manager.close();
        }
    }

    @Override
    public List<TipoMembresia> obtenerTodosLosTiposMembresia() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {
            return manager.createQuery(
                    "SELECT t FROM TipoMembresia t",
                    TipoMembresia.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
    
    @Override
    public List<TipoMembresia> obtenerTiposMembresiaActivos() {

        EntityManager manager = JPAUtil.getEntityManager();

        try {

            return manager.createQuery(
                    "SELECT t FROM TipoMembresia t " +
                    "WHERE t.estado = true",
                    TipoMembresia.class)
                    .getResultList();

        } finally {
            manager.close();
        }
    }
}