package service;

import java.util.List;

import dao.EmpleadoDAOImpl;
import dao.UsuarioDAOImpl;
import interfaces.EmpleadoDAO;
import interfaces.UsuarioDAO;
import model.Empleado;
import model.Usuario;
import model.enums.Rol;

public class EmpleadoService {

    private final EmpleadoDAO empleadoDAO;
    private final UsuarioDAO usuarioDAO;

    public EmpleadoService() {
        this.empleadoDAO = new EmpleadoDAOImpl();
        this.usuarioDAO = new UsuarioDAOImpl();
    }


    // RN-EM01, RN-EM02 y RN-EM03
    public void registrarEmpleado(Empleado empleado) {

        validarEmpleado(empleado);

        // RN-EM02
        Empleado existente =
                empleadoDAO.obtenerEmpleadoPorDni(
                        empleado.getDni());

        if (existente != null) {
            throw new IllegalArgumentException(
                    "Ya existe un empleado registrado con ese DNI.");
        }

        // RN-EM01 y RN-EM03
        Usuario usuario =
                obtenerUsuarioValido(
                        empleado.getUsuario());

        empleado.setUsuario(usuario);

        if (empleado.getEstado() == null) {
            empleado.setEstado(true);
        }

        empleadoDAO.registrarEmpleado(empleado);
    }


    public void actualizarEmpleado(Empleado empleado) {

        if (empleado == null
                || empleado.getIdEmpleado() == null) {

            throw new IllegalArgumentException(
                    "El empleado que desea actualizar no es válido.");
        }

        Empleado actual =
                empleadoDAO.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado());

        if (actual == null) {
            throw new IllegalArgumentException(
                    "El empleado no existe.");
        }

        validarEmpleado(empleado);

        // RN-EM02
        Empleado mismoDni =
                empleadoDAO.obtenerEmpleadoPorDni(
                        empleado.getDni());

        if (mismoDni != null
                && !mismoDni.getIdEmpleado()
                        .equals(empleado.getIdEmpleado())) {

            throw new IllegalArgumentException(
                    "El DNI pertenece a otro empleado.");
        }

        // RN-EM01 y RN-EM03
        Usuario usuario =
                obtenerUsuarioValido(
                        empleado.getUsuario());

        empleado.setUsuario(usuario);

        empleadoDAO.actualizarEmpleado(empleado);
    }


    public Empleado obtenerEmpleadoPorId(
            Integer idEmpleado) {

        if (idEmpleado == null
                || idEmpleado <= 0) {

            throw new IllegalArgumentException(
                    "El código del empleado no es válido.");
        }

        Empleado empleado =
                empleadoDAO.obtenerEmpleadoPorId(
                        idEmpleado);

        if (empleado == null) {
            throw new IllegalArgumentException(
                    "El empleado no existe.");
        }

        return empleado;
    }


    public Empleado obtenerEmpleadoPorDni(
            String dni) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException(
                    "El DNI es obligatorio.");
        }

        return empleadoDAO.obtenerEmpleadoPorDni(
                dni);
    }


    public List<Empleado> obtenerTodosLosEmpleados() {

        return empleadoDAO
                .obtenerTodosLosEmpleados();
    }


    // RN-EM04
    public void desactivarEmpleado(
            Integer idEmpleado) {

        Empleado empleado =
                obtenerEmpleadoPorId(
                        idEmpleado);

        empleado.setEstado(false);

        empleadoDAO.actualizarEmpleado(
                empleado);
    }


    public void activarEmpleado(
            Integer idEmpleado) {

        Empleado empleado =
                obtenerEmpleadoPorId(
                        idEmpleado);

        empleado.setEstado(true);

        empleadoDAO.actualizarEmpleado(
                empleado);
    }


    // RN-EM04
    public Empleado obtenerEmpleadoActivo(
            Integer idEmpleado) {

        Empleado empleado =
                obtenerEmpleadoPorId(
                        idEmpleado);

        if (!Boolean.TRUE.equals(
                empleado.getEstado())) {

            throw new IllegalStateException(
                    "El empleado se encuentra inactivo.");
        }

        return empleado;
    }


    private void validarEmpleado(
            Empleado empleado) {

        if (empleado == null) {
            throw new IllegalArgumentException(
                    "El empleado es obligatorio.");
        }

        if (empleado.getDni() == null
                || empleado.getDni().isBlank()) {

            throw new IllegalArgumentException(
                    "El DNI del empleado es obligatorio.");
        }

        if (!empleado.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "El DNI debe contener exactamente 8 números.");
        }

        // RN-EM01
        if (empleado.getUsuario() == null
                || empleado.getUsuario()
                        .getIdUsuario() == null) {

            throw new IllegalArgumentException(
                    "El empleado debe estar asociado a un usuario registrado.");
        }
    }


    // RN-EM01 y RN-EM03
    private Usuario obtenerUsuarioValido(
            Usuario usuario) {

        if (usuario == null
                || usuario.getIdUsuario() == null) {

            throw new IllegalArgumentException(
                    "El empleado debe estar asociado a un usuario registrado.");
        }

        Usuario existente =
                usuarioDAO.obtenerUsuarioPorId(
                        usuario.getIdUsuario());

        if (existente == null) {
            throw new IllegalArgumentException(
                    "El usuario asociado al empleado no existe.");
        }

        validarRolEmpleado(existente);

        return existente;
    }


    // RN-EM03
    private void validarRolEmpleado(
            Usuario usuario) {

        if (usuario.getRol() == null) {
            throw new IllegalArgumentException(
                    "El usuario del empleado debe tener un rol.");
        }

        Rol rol =
                usuario.getRol();

        if (rol != Rol.ADMINISTRADOR
                && rol != Rol.RECEPCIONISTA
                && rol != Rol.ENTRENADOR) {

            throw new IllegalArgumentException(
                    "El usuario asociado no tiene un rol válido para un empleado.");
        }
    }
}