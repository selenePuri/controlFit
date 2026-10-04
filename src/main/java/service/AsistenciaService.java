package service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import dao.AsistenciaDAOImpl;
import dao.ClienteDAOImpl;
import interfaces.AsistenciaDAO;
import interfaces.ClienteDAO;
import model.Asistencia;
import model.Cliente;

public class AsistenciaService {

    private final AsistenciaDAO asistenciaDAO;
    private final ClienteDAO clienteDAO;
    private final MembresiaService membresiaService;

    public AsistenciaService() {
        this.asistenciaDAO = new AsistenciaDAOImpl();
        this.clienteDAO = new ClienteDAOImpl();
        this.membresiaService = new MembresiaService();
    }


    // RN-AS01, RN-AS02 y RN-AS03
    public void registrarEntrada(Integer idCliente) {

        // RN-AS01
        Cliente cliente =
                obtenerClienteActivo(idCliente);

        LocalDate fechaActual =
                LocalDate.now();

        LocalTime horaActual =
                LocalTime.now();


        // RN-AS02
        boolean tieneMembresia =
                membresiaService
                        .tieneMembresiaVigente(
                                cliente.getIdCliente(),
                                fechaActual);

        if (!tieneMembresia) {
            throw new IllegalStateException(
                    "El cliente no tiene una membresía vigente y no puede ingresar.");
        }


        // RN-AS03
        Asistencia asistenciaAbierta =
                asistenciaDAO
                        .obtenerAsistenciaAbiertaPorCliente(
                                cliente.getIdCliente());

        if (asistenciaAbierta != null) {
            throw new IllegalStateException(
                    "El cliente ya tiene una entrada registrada y aún no ha registrado su salida.");
        }


        Asistencia asistencia =
                new Asistencia();

        asistencia.setCliente(
                cliente);

        asistencia.setFecha(
                fechaActual);

        asistencia.setHoraEntrada(
                horaActual);

        asistencia.setHoraSalida(
                null);

        asistenciaDAO
                .registrarAsistencia(
                        asistencia);
    }


    // RN-AS04 y RN-AS05
    public void registrarSalida(Integer idCliente) {

        Cliente cliente =
                obtenerClienteExistente(
                        idCliente);


        // RN-AS05
        Asistencia asistencia =
                asistenciaDAO
                        .obtenerAsistenciaAbiertaPorCliente(
                                cliente.getIdCliente());

        if (asistencia == null) {
            throw new IllegalStateException(
                    "El cliente no tiene una entrada pendiente de salida.");
        }


        LocalDate fechaActual =
                LocalDate.now();

        LocalTime horaActual =
                LocalTime.now();


        // El modelo actual registra entrada y salida el mismo día
        if (!fechaActual.equals(
                asistencia.getFecha())) {

            throw new IllegalStateException(
                    "La salida no puede registrarse en una fecha diferente a la fecha de entrada.");
        }


        // RN-AS04
        if (horaActual.isBefore(
                asistencia.getHoraEntrada())) {

            throw new IllegalStateException(
                    "La hora de salida no puede ser anterior a la hora de entrada.");
        }


        asistencia.setHoraSalida(
                horaActual);

        asistenciaDAO
                .actualizarAsistencia(
                        asistencia);
    }


    public Asistencia obtenerAsistenciaPorId(
            Integer idAsistencia) {

        if (idAsistencia == null
                || idAsistencia <= 0) {

            throw new IllegalArgumentException(
                    "El código de la asistencia no es válido.");
        }

        Asistencia asistencia =
                asistenciaDAO
                        .obtenerAsistenciaPorId(
                                idAsistencia);

        if (asistencia == null) {
            throw new IllegalArgumentException(
                    "La asistencia no existe.");
        }

        return asistencia;
    }


    public List<Asistencia>
            obtenerTodasLasAsistencias() {

        return asistenciaDAO
                .obtenerTodasLasAsistencias();
    }


    public List<Asistencia>
            obtenerAsistenciasPorCliente(
                    Integer idCliente) {

        Cliente cliente =
                obtenerClienteExistente(
                        idCliente);

        return asistenciaDAO
                .obtenerAsistenciasPorCliente(
                        cliente.getIdCliente());
    }


    public List<Asistencia>
            obtenerAsistenciasPorFecha(
                    LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }

        return asistenciaDAO
                .obtenerAsistenciasPorFecha(
                        fecha);
    }


    public Asistencia obtenerAsistenciaAbierta(
            Integer idCliente) {

        Cliente cliente =
                obtenerClienteExistente(
                        idCliente);

        return asistenciaDAO
                .obtenerAsistenciaAbiertaPorCliente(
                        cliente.getIdCliente());
    }


    // RN-AS01
    private Cliente obtenerClienteActivo(
            Integer idCliente) {

        Cliente cliente =
                obtenerClienteExistente(
                        idCliente);

        if (!Boolean.TRUE.equals(
                cliente.getEstado())) {

            throw new IllegalStateException(
                    "El cliente se encuentra inactivo y no puede registrar asistencia.");
        }

        return cliente;
    }


    private Cliente obtenerClienteExistente(
            Integer idCliente) {

        validarIdCliente(
                idCliente);

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


    private void validarIdCliente(
            Integer idCliente) {

        if (idCliente == null
                || idCliente <= 0) {

            throw new IllegalArgumentException(
                    "El código del cliente no es válido.");
        }
    }
}