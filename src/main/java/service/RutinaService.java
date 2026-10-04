package service;

import java.time.LocalDate;
import java.util.List;

import dao.ClienteDAOImpl;
import dao.RutinaClienteDAOImpl;
import dao.RutinaDAOImpl;
import interfaces.ClienteDAO;
import interfaces.RutinaClienteDAO;
import interfaces.RutinaDAO;
import model.Cliente;
import model.Entrenador;
import model.Rutina;
import model.RutinaCliente;

public class RutinaService {

    private final RutinaDAO rutinaDAO;
    private final RutinaClienteDAO rutinaClienteDAO;
    private final ClienteDAO clienteDAO;
    private final EntrenadorService entrenadorService;

    public RutinaService() {
        this.rutinaDAO = new RutinaDAOImpl();
        this.rutinaClienteDAO = new RutinaClienteDAOImpl();
        this.clienteDAO = new ClienteDAOImpl();
        this.entrenadorService = new EntrenadorService();
    }


    // RN-RU01 y RN-RU02
    public void registrarRutina(Rutina rutina) {

        validarDatosRutina(rutina);

        Entrenador entrenador =
                entrenadorService
                        .obtenerEntrenadorHabilitado(
                                rutina.getEntrenador()
                                        .getIdEntrenador());

        rutina.setEntrenador(
                entrenador);

        if (rutina.getFechaCreacion() == null) {
            rutina.setFechaCreacion(
                    LocalDate.now());
        }

        if (rutina.getEstado() == null) {
            rutina.setEstado(true);
        }

        rutinaDAO.registrarRutina(
                rutina);
    }


    // RN-RU01 y RN-RU02
    public void actualizarRutina(Rutina rutina) {

        if (rutina == null
                || rutina.getIdRutina() == null) {

            throw new IllegalArgumentException(
                    "La rutina que desea actualizar no es válida.");
        }

        Rutina existente =
                rutinaDAO.obtenerRutinaPorId(
                        rutina.getIdRutina());

        if (existente == null) {
            throw new IllegalArgumentException(
                    "La rutina no existe.");
        }

        validarDatosRutina(rutina);

        Entrenador entrenador =
                entrenadorService
                        .obtenerEntrenadorHabilitado(
                                rutina.getEntrenador()
                                        .getIdEntrenador());

        rutina.setEntrenador(
                entrenador);

        rutinaDAO.actualizarRutina(
                rutina);
    }


    public Rutina obtenerRutinaPorId(
            Integer idRutina) {

        if (idRutina == null
                || idRutina <= 0) {

            throw new IllegalArgumentException(
                    "El código de la rutina no es válido.");
        }

        Rutina rutina =
                rutinaDAO.obtenerRutinaPorId(
                        idRutina);

        if (rutina == null) {
            throw new IllegalArgumentException(
                    "La rutina no existe.");
        }

        return rutina;
    }


    public List<Rutina> obtenerTodasLasRutinas() {

        return rutinaDAO
                .obtenerTodasLasRutinas();
    }


    public List<Rutina> obtenerRutinasPorEntrenador(
            Integer idEntrenador) {

        if (idEntrenador == null
                || idEntrenador <= 0) {

            throw new IllegalArgumentException(
                    "El código del entrenador no es válido.");
        }

        // El entrenador debe existir
        entrenadorService
                .obtenerEntrenadorPorId(
                        idEntrenador);

        return rutinaDAO
                .obtenerRutinasPorEntrenador(
                        idEntrenador);
    }


    public List<Rutina> obtenerRutinasActivas() {

        return rutinaDAO
                .obtenerRutinasActivas();
    }


    public void desactivarRutina(
            Integer idRutina) {

        Rutina rutina =
                obtenerRutinaPorId(
                        idRutina);

        rutina.setEstado(false);

        rutinaDAO.actualizarRutina(
                rutina);
    }


    public void activarRutina(
            Integer idRutina) {

        Rutina rutina =
                obtenerRutinaPorId(
                        idRutina);

        // RN-RU02
        entrenadorService
                .obtenerEntrenadorHabilitado(
                        rutina.getEntrenador()
                                .getIdEntrenador());

        rutina.setEstado(true);

        rutinaDAO.actualizarRutina(
                rutina);
    }


    // RN-RC01 a RN-RC04
    public void asignarRutina(
            Integer idRutina,
            Integer idCliente,
            LocalDate fechaAsignacion,
            LocalDate fechaFin) {

        // RN-RC01
        Cliente cliente =
                obtenerClienteActivo(
                        idCliente);

        // RN-RC02 y RN-RU03
        Rutina rutina =
                obtenerRutinaActiva(
                        idRutina);

        if (fechaAsignacion == null) {
            fechaAsignacion =
                    LocalDate.now();
        }

        // RN-RC03
        if (fechaFin != null
                && fechaFin.isBefore(
                        fechaAsignacion)) {

            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la fecha de asignación.");
        }

        // RN-RC04
        RutinaCliente asignacionExistente =
                rutinaClienteDAO
                        .obtenerAsignacionActiva(
                                cliente.getIdCliente(),
                                rutina.getIdRutina(),
                                fechaAsignacion);

        if (asignacionExistente != null) {
            throw new IllegalStateException(
                    "El cliente ya tiene esta rutina asignada actualmente.");
        }

        RutinaCliente asignacion =
                new RutinaCliente();

        asignacion.setRutina(
                rutina);

        asignacion.setCliente(
                cliente);

        asignacion.setFechaAsignacion(
                fechaAsignacion);

        asignacion.setFechaFin(
                fechaFin);

        asignacion.setEstado(true);

        rutinaClienteDAO
                .registrarRutinaCliente(
                        asignacion);
    }


    public RutinaCliente obtenerAsignacionPorId(
            Integer idRutinaCliente) {

        if (idRutinaCliente == null
                || idRutinaCliente <= 0) {

            throw new IllegalArgumentException(
                    "El código de la asignación no es válido.");
        }

        RutinaCliente asignacion =
                rutinaClienteDAO
                        .obtenerRutinaClientePorId(
                                idRutinaCliente);

        if (asignacion == null) {
            throw new IllegalArgumentException(
                    "La asignación de rutina no existe.");
        }

        return asignacion;
    }


    public List<RutinaCliente>
            obtenerTodasLasAsignaciones() {

        return rutinaClienteDAO
                .obtenerTodasLasRutinasCliente();
    }


    public List<RutinaCliente>
            obtenerRutinasPorCliente(
                    Integer idCliente) {

        Cliente cliente =
                obtenerClienteExistente(
                        idCliente);

        return rutinaClienteDAO
                .obtenerRutinasPorCliente(
                        cliente.getIdCliente());
    }


    public List<RutinaCliente>
            obtenerClientesPorRutina(
                    Integer idRutina) {

        Rutina rutina =
                obtenerRutinaPorId(
                        idRutina);

        return rutinaClienteDAO
                .obtenerClientesPorRutina(
                        rutina.getIdRutina());
    }


    public void finalizarAsignacion(
            Integer idRutinaCliente,
            LocalDate fechaFin) {

        RutinaCliente asignacion =
                obtenerAsignacionPorId(
                        idRutinaCliente);

        if (!Boolean.TRUE.equals(
                asignacion.getEstado())) {

            throw new IllegalStateException(
                    "La asignación ya se encuentra inactiva.");
        }

        if (fechaFin == null) {
            fechaFin =
                    LocalDate.now();
        }

        // RN-RC03
        if (fechaFin.isBefore(
                asignacion.getFechaAsignacion())) {

            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la fecha de asignación.");
        }

        asignacion.setFechaFin(
                fechaFin);

        asignacion.setEstado(false);

        rutinaClienteDAO
                .actualizarRutinaCliente(
                        asignacion);
    }


    // RN-RU03
    private Rutina obtenerRutinaActiva(
            Integer idRutina) {

        Rutina rutina =
                obtenerRutinaPorId(
                        idRutina);

        if (!Boolean.TRUE.equals(
                rutina.getEstado())) {

            throw new IllegalStateException(
                    "La rutina se encuentra inactiva y no puede ser asignada.");
        }

        // RN-RU02
        entrenadorService
                .obtenerEntrenadorHabilitado(
                        rutina.getEntrenador()
                                .getIdEntrenador());

        return rutina;
    }


    // RN-RC01
    private Cliente obtenerClienteActivo(
            Integer idCliente) {

        Cliente cliente =
                obtenerClienteExistente(
                        idCliente);

        if (!Boolean.TRUE.equals(
                cliente.getEstado())) {

            throw new IllegalStateException(
                    "El cliente se encuentra inactivo y no puede recibir nuevas rutinas.");
        }

        return cliente;
    }


    private Cliente obtenerClienteExistente(
            Integer idCliente) {

        if (idCliente == null
                || idCliente <= 0) {

            throw new IllegalArgumentException(
                    "El código del cliente no es válido.");
        }

        Cliente cliente =
                clienteDAO
                        .obtenerClientePorId(
                                idCliente);

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no existe.");
        }

        return cliente;
    }


    // RN-RU01
    private void validarDatosRutina(
            Rutina rutina) {

        if (rutina == null) {
            throw new IllegalArgumentException(
                    "La rutina es obligatoria.");
        }

        if (rutina.getEntrenador() == null
                || rutina.getEntrenador()
                        .getIdEntrenador() == null) {

            throw new IllegalArgumentException(
                    "La rutina debe estar asociada a un entrenador.");
        }

        if (rutina.getNombre() == null
                || rutina.getNombre()
                        .isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de la rutina es obligatorio.");
        }
    }
}