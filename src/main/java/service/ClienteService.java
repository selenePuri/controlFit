package service;

import java.util.List;

import javax.persistence.EntityManager;

import dao.ClienteDAOImpl;
import dao.UsuarioDAOImpl;
import interfaces.ClienteDAO;
import interfaces.UsuarioDAO;
import model.Cliente;
import model.Usuario;
import model.enums.Rol;
import util.JPAUtil;
import util.PasswordUtil;

public class ClienteService {

    private final ClienteDAO clienteDAO;
    private final UsuarioDAO usuarioDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAOImpl();
        this.usuarioDAO = new UsuarioDAOImpl();
    }


    // RN-CL01 y RN-CL02
    public void registrarCliente(Cliente cliente) {

        validarCliente(cliente);

        if (clienteDAO.existeClientePorDni(
                cliente.getDni())) {

            throw new IllegalArgumentException(
                    "Ya existe un cliente registrado con ese DNI.");
        }

        // RN-CL02
        cliente.setUsuario(null);

        if (cliente.getEstado() == null) {
            cliente.setEstado(true);
        }

        clienteDAO.registrarCliente(cliente);
    }


    // RN-CL03, RN-CL05 y RN-CL10
    public void crearCuentaCliente(
            String dni,
            Usuario usuario) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException(
                    "El DNI es obligatorio.");
        }

        Cliente cliente =
                clienteDAO.obtenerClientePorDni(dni);

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "No existe un cliente registrado con ese DNI.");
        }

        // RN-CL05
        if (cliente.getUsuario() != null) {
            throw new IllegalStateException(
                    "El cliente ya tiene una cuenta de usuario.");
        }

        validarDatosCuenta(usuario);
        validarNombreUsuarioDisponible(usuario);

        // RN-CL10
        prepararCuentaCliente(usuario);

        EntityManager manager =
                JPAUtil.getEntityManager();

        try {

            manager.getTransaction().begin();

            manager.persist(usuario);

            cliente.setUsuario(usuario);

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


    // RN-CL04, RN-CL06, RN-CL07 y RN-CL08
    public void registrarClienteConCuenta(
            Cliente cliente,
            Usuario usuario) {

        validarCliente(cliente);
        validarDatosCuenta(usuario);
        validarNombreUsuarioDisponible(usuario);

        Cliente existente =
                clienteDAO.obtenerClientePorDni(
                        cliente.getDni());

        // RN-CL06
        if (existente != null) {

            if (existente.getUsuario() != null) {
                throw new IllegalStateException(
                        "El cliente ya tiene una cuenta de usuario.");
            }

            prepararCuentaCliente(usuario);

            EntityManager manager =
                    JPAUtil.getEntityManager();

            try {

                manager.getTransaction().begin();

                manager.persist(usuario);

                existente.setUsuario(usuario);

                manager.merge(existente);

                manager.getTransaction().commit();

            } catch (Exception e) {

                if (manager.getTransaction().isActive()) {
                    manager.getTransaction().rollback();
                }

                throw e;

            } finally {
                manager.close();
            }

            // El objeto recibido representa al cliente reutilizado
            cliente.setIdCliente(
                    existente.getIdCliente());

            cliente.setUsuario(usuario);
            cliente.setEstado(
                    existente.getEstado());

            return;
        }

        // RN-CL07
        prepararCuentaCliente(usuario);

        cliente.setEstado(true);
        cliente.setUsuario(usuario);

        // RN-CL08
        EntityManager manager =
                JPAUtil.getEntityManager();

        try {

            manager.getTransaction().begin();

            manager.persist(usuario);
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


    public Cliente obtenerClientePorId(
            Integer idCliente) {

        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException(
                    "El código del cliente no es válido.");
        }

        Cliente cliente =
                clienteDAO.obtenerClientePorId(
                        idCliente);

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no existe.");
        }

        return cliente;
    }


    public Cliente obtenerClientePorDni(
            String dni) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException(
                    "El DNI es obligatorio.");
        }

        return clienteDAO.obtenerClientePorDni(
                dni);
    }


    public List<Cliente> obtenerTodosLosClientes() {

        return clienteDAO
                .obtenerTodosLosClientes();
    }


    public void desactivarCliente(
            Integer idCliente) {

        Cliente cliente =
                obtenerClientePorId(idCliente);

        cliente.setEstado(false);

        clienteDAO.actualizarCliente(cliente);
    }


    public void activarCliente(
            Integer idCliente) {

        Cliente cliente =
                obtenerClientePorId(idCliente);

        cliente.setEstado(true);

        clienteDAO.actualizarCliente(cliente);
    }


    private void validarCliente(
            Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio.");
        }

        if (cliente.getDni() == null
                || cliente.getDni().isBlank()) {

            throw new IllegalArgumentException(
                    "El DNI es obligatorio.");
        }

        if (!cliente.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "El DNI debe contener exactamente 8 números.");
        }
    }


    private void validarDatosCuenta(
            Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Los datos de la cuenta son obligatorios.");
        }

        if (usuario.getUsuario() == null
                || usuario.getUsuario().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio.");
        }

        if (usuario.getClave() == null
                || usuario.getClave().isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }
    }


    // RN-US01
    private void validarNombreUsuarioDisponible(
            Usuario usuario) {

        if (usuarioDAO.existeNombreUsuario(
                usuario.getUsuario())) {

            throw new IllegalArgumentException(
                    "El nombre de usuario ya se encuentra registrado.");
        }
    }


    // RN-CL04, RN-CL10 y RN-US07
    private void prepararCuentaCliente(
            Usuario usuario) {

        usuario.setRol(Rol.CLIENTE);
        usuario.setEstado(true);

        usuario.setClave(
                PasswordUtil.generarHash(
                        usuario.getClave()));
    }
}