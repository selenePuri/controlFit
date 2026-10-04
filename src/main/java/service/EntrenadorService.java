package service;

import java.util.List;

import dao.EntrenadorDAOImpl;
import interfaces.EntrenadorDAO;
import model.Empleado;
import model.Entrenador;
import model.Usuario;
import model.enums.Rol;

public class EntrenadorService {

    private final EntrenadorDAO entrenadorDAO;
    private final EmpleadoService empleadoService;

    public EntrenadorService() {
        this.entrenadorDAO = new EntrenadorDAOImpl();
        this.empleadoService = new EmpleadoService();
    }


    // RN-EN01 y RN-EN02
    public void registrarEntrenador(
            Entrenador entrenador) {

        validarDatosEntrenador(entrenador);

        Empleado empleado =
                obtenerEmpleadoValido(
                        entrenador.getEmpleado());

        entrenador.setEmpleado(empleado);

        entrenadorDAO.registrarEntrenador(
                entrenador);
    }


    // RN-EN01 y RN-EN02
    public void actualizarEntrenador(
            Entrenador entrenador) {

        if (entrenador == null
                || entrenador.getIdEntrenador() == null) {

            throw new IllegalArgumentException(
                    "El entrenador que desea actualizar no es válido.");
        }

        Entrenador existente =
                entrenadorDAO.obtenerEntrenadorPorId(
                        entrenador.getIdEntrenador());

        if (existente == null) {
            throw new IllegalArgumentException(
                    "El entrenador no existe.");
        }

        validarDatosEntrenador(entrenador);

        Empleado empleado =
                obtenerEmpleadoValido(
                        entrenador.getEmpleado());

        entrenador.setEmpleado(empleado);

        entrenadorDAO.actualizarEntrenador(
                entrenador);
    }


    public Entrenador obtenerEntrenadorPorId(
            Integer idEntrenador) {

        if (idEntrenador == null
                || idEntrenador <= 0) {

            throw new IllegalArgumentException(
                    "El código del entrenador no es válido.");
        }

        Entrenador entrenador =
                entrenadorDAO.obtenerEntrenadorPorId(
                        idEntrenador);

        if (entrenador == null) {
            throw new IllegalArgumentException(
                    "El entrenador no existe.");
        }

        return entrenador;
    }


    public List<Entrenador> obtenerTodosLosEntrenadores() {

        return entrenadorDAO
                .obtenerTodosLosEntrenadores();
    }


    public List<Entrenador> obtenerPorEspecialidad(
            String especialidad) {

        if (especialidad == null
                || especialidad.isBlank()) {

            throw new IllegalArgumentException(
                    "La especialidad es obligatoria.");
        }

        return entrenadorDAO
                .obtenerEntrenadoresPorEspecialidad(
                        especialidad);
    }


    public List<Entrenador> obtenerEntrenadoresActivos() {

        return entrenadorDAO
                .obtenerEntrenadoresActivos();
    }


    // RN-EN03
    public Entrenador obtenerEntrenadorHabilitado(
            Integer idEntrenador) {

        Entrenador entrenador =
                obtenerEntrenadorPorId(
                        idEntrenador);

        Empleado empleado =
                entrenador.getEmpleado();

        // RN-EN01 y RN-EN03
        empleado =
                empleadoService.obtenerEmpleadoActivo(
                        empleado.getIdEmpleado());

        // RN-EN02
        validarRolEntrenador(
                empleado.getUsuario());

        // RN-EN03
        if (!Boolean.TRUE.equals(
                empleado.getUsuario()
                        .getEstado())) {

            throw new IllegalStateException(
                    "La cuenta del entrenador se encuentra inactiva.");
        }

        entrenador.setEmpleado(
                empleado);

        return entrenador;
    }


    private void validarDatosEntrenador(
            Entrenador entrenador) {

        if (entrenador == null) {
            throw new IllegalArgumentException(
                    "El entrenador es obligatorio.");
        }

        // RN-EN01
        if (entrenador.getEmpleado() == null
                || entrenador.getEmpleado()
                        .getIdEmpleado() == null) {

            throw new IllegalArgumentException(
                    "El entrenador debe estar asociado a un empleado registrado.");
        }
    }


    // RN-EN01 y RN-EN02
    private Empleado obtenerEmpleadoValido(
            Empleado empleado) {

        if (empleado == null
                || empleado.getIdEmpleado() == null) {

            throw new IllegalArgumentException(
                    "El entrenador debe estar asociado a un empleado registrado.");
        }

        Empleado existente =
                empleadoService.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado());

        Usuario usuario =
                existente.getUsuario();

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El entrenador debe tener una cuenta de usuario asociada.");
        }

        validarRolEntrenador(usuario);

        return existente;
    }


    // RN-EN02
    private void validarRolEntrenador(
            Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El entrenador debe tener una cuenta de usuario asociada.");
        }

        if (usuario.getRol() != Rol.ENTRENADOR) {
            throw new IllegalArgumentException(
                    "El usuario asociado debe tener rol ENTRENADOR.");
        }
    }
}