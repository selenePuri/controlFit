package service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Asistencia;
import model.Cliente;
import model.Membresia;
import model.TipoMembresia;
import model.enums.MetodoPago;
import model.enums.TipoPlan;

public class AsistenciaServiceTest {

    private final AsistenciaService asistenciaService =
            new AsistenciaService();

    private final ClienteService clienteService =
            new ClienteService();

    private final MembresiaService membresiaService =
            new MembresiaService();

    private final TipoMembresiaService tipoMembresiaService =
            new TipoMembresiaService();


    // RN-AS01 y RN-AS02
    @Test
    void registrarEntradaCorrectamente() {

        Cliente cliente =
                crearClienteConMembresiaVigente();

        asistenciaService.registrarEntrada(
                cliente.getIdCliente());

        Asistencia asistencia =
                asistenciaService.obtenerAsistenciaAbierta(
                        cliente.getIdCliente());

        assertNotNull(asistencia);

        assertNotNull(
                asistencia.getIdAsistencia());

        assertEquals(
                cliente.getIdCliente(),
                asistencia.getCliente()
                        .getIdCliente());

        assertEquals(
                LocalDate.now(),
                asistencia.getFecha());

        assertNotNull(
                asistencia.getHoraEntrada());

        assertNull(
                asistencia.getHoraSalida());
    }


    // RN-AS02
    @Test
    void noDebeRegistrarEntradaSinMembresiaVigente() {

        Cliente cliente =
                crearClientePrueba();

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> asistenciaService
                                .registrarEntrada(
                                        cliente.getIdCliente()));

        assertEquals(
                "El cliente no tiene una membresía vigente y no puede ingresar.",
                excepcion.getMessage());
    }


    // RN-AS01
    @Test
    void noDebeRegistrarEntradaClienteInactivo() {

        Cliente cliente =
                crearClienteConMembresiaVigente();

        clienteService.desactivarCliente(
                cliente.getIdCliente());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> asistenciaService
                                .registrarEntrada(
                                        cliente.getIdCliente()));

        assertEquals(
                "El cliente se encuentra inactivo y no puede registrar asistencia.",
                excepcion.getMessage());
    }


    // RN-AS03
    @Test
    void noDebeRegistrarDosEntradasAbiertas() {

        Cliente cliente =
                crearClienteConMembresiaVigente();

        asistenciaService.registrarEntrada(
                cliente.getIdCliente());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> asistenciaService
                                .registrarEntrada(
                                        cliente.getIdCliente()));

        assertEquals(
                "El cliente ya tiene una entrada registrada y aún no ha registrado su salida.",
                excepcion.getMessage());
    }


    // RN-AS04 y RN-AS05
    @Test
    void registrarSalidaCorrectamente() {

        Cliente cliente =
                crearClienteConMembresiaVigente();

        asistenciaService.registrarEntrada(
                cliente.getIdCliente());

        Asistencia entrada =
                asistenciaService.obtenerAsistenciaAbierta(
                        cliente.getIdCliente());

        assertNotNull(entrada);

        assertNull(
                entrada.getHoraSalida());

        asistenciaService.registrarSalida(
                cliente.getIdCliente());

        Asistencia actualizada =
                asistenciaService.obtenerAsistenciaPorId(
                        entrada.getIdAsistencia());

        assertNotNull(
                actualizada.getHoraSalida());

        assertFalse(
                actualizada.getHoraSalida()
                        .isBefore(
                                actualizada.getHoraEntrada()));

        assertNull(
                asistenciaService.obtenerAsistenciaAbierta(
                        cliente.getIdCliente()));
    }


    // RN-AS05
    @Test
    void noDebeRegistrarSalidaSinEntradaAbierta() {

        Cliente cliente =
                crearClientePrueba();

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> asistenciaService
                                .registrarSalida(
                                        cliente.getIdCliente()));

        assertEquals(
                "El cliente no tiene una entrada pendiente de salida.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarEntradaConIdInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .registrarEntrada(0));

        assertEquals(
                "El código del cliente no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarSalidaConIdInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .registrarSalida(0));

        assertEquals(
                "El código del cliente no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarEntradaClienteInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .registrarEntrada(
                                        Integer.MAX_VALUE));

        assertEquals(
                "El cliente no existe.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarSalidaClienteInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .registrarSalida(
                                        Integer.MAX_VALUE));

        assertEquals(
                "El cliente no existe.",
                excepcion.getMessage());
    }


    @Test
    void obtenerAsistenciaPorIdCorrectamente() {

        Cliente cliente =
                crearClienteConMembresiaVigente();

        asistenciaService.registrarEntrada(
                cliente.getIdCliente());

        Asistencia abierta =
                asistenciaService.obtenerAsistenciaAbierta(
                        cliente.getIdCliente());

        Asistencia encontrada =
                asistenciaService.obtenerAsistenciaPorId(
                        abierta.getIdAsistencia());

        assertNotNull(encontrada);

        assertEquals(
                abierta.getIdAsistencia(),
                encontrada.getIdAsistencia());
    }


    @Test
    void noDebeObtenerAsistenciaConIdInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .obtenerAsistenciaPorId(0));

        assertEquals(
                "El código de la asistencia no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeObtenerAsistenciaInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .obtenerAsistenciaPorId(
                                        Integer.MAX_VALUE));

        assertEquals(
                "La asistencia no existe.",
                excepcion.getMessage());
    }


    @Test
    void obtenerTodasLasAsistenciasCorrectamente() {

        Cliente cliente =
                crearClienteConMembresiaVigente();

        asistenciaService.registrarEntrada(
                cliente.getIdCliente());

        List<Asistencia> asistencias =
                asistenciaService
                        .obtenerTodasLasAsistencias();

        assertNotNull(asistencias);

        assertTrue(
                asistencias.stream()
                        .anyMatch(a ->
                                a.getCliente()
                                        .getIdCliente()
                                        .equals(
                                                cliente
                                                        .getIdCliente())));
    }


    @Test
    void obtenerAsistenciasPorClienteCorrectamente() {

        Cliente cliente =
                crearClienteConMembresiaVigente();

        asistenciaService.registrarEntrada(
                cliente.getIdCliente());

        List<Asistencia> asistencias =
                asistenciaService
                        .obtenerAsistenciasPorCliente(
                                cliente.getIdCliente());

        assertNotNull(asistencias);

        assertFalse(
                asistencias.isEmpty());

        assertTrue(
                asistencias.stream()
                        .allMatch(a ->
                                a.getCliente()
                                        .getIdCliente()
                                        .equals(
                                                cliente
                                                        .getIdCliente())));
    }


    @Test
    void noDebeBuscarAsistenciasConIdClienteInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .obtenerAsistenciasPorCliente(0));

        assertEquals(
                "El código del cliente no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeBuscarAsistenciasDeClienteInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .obtenerAsistenciasPorCliente(
                                        Integer.MAX_VALUE));

        assertEquals(
                "El cliente no existe.",
                excepcion.getMessage());
    }


    @Test
    void obtenerAsistenciasPorFechaCorrectamente() {

        Cliente cliente =
                crearClienteConMembresiaVigente();

        asistenciaService.registrarEntrada(
                cliente.getIdCliente());

        List<Asistencia> asistencias =
                asistenciaService
                        .obtenerAsistenciasPorFecha(
                                LocalDate.now());

        assertNotNull(asistencias);

        assertTrue(
                asistencias.stream()
                        .anyMatch(a ->
                                a.getCliente()
                                        .getIdCliente()
                                        .equals(
                                                cliente
                                                        .getIdCliente())));
    }


    @Test
    void noDebeBuscarAsistenciasSinFecha() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .obtenerAsistenciasPorFecha(
                                        null));

        assertEquals(
                "La fecha es obligatoria.",
                excepcion.getMessage());
    }


    @Test
    void obtenerAsistenciaAbiertaCorrectamente() {

        Cliente cliente =
                crearClienteConMembresiaVigente();

        asistenciaService.registrarEntrada(
                cliente.getIdCliente());

        Asistencia asistencia =
                asistenciaService
                        .obtenerAsistenciaAbierta(
                                cliente.getIdCliente());

        assertNotNull(asistencia);

        assertNull(
                asistencia.getHoraSalida());
    }


    @Test
    void obtenerAsistenciaAbiertaDebeRetornarNullSiNoExiste() {

        Cliente cliente =
                crearClientePrueba();

        Asistencia asistencia =
                asistenciaService
                        .obtenerAsistenciaAbierta(
                                cliente.getIdCliente());

        assertNull(asistencia);
    }


    @Test
    void noDebeBuscarAsistenciaAbiertaConIdClienteInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .obtenerAsistenciaAbierta(0));

        assertEquals(
                "El código del cliente no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeBuscarAsistenciaAbiertaDeClienteInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> asistenciaService
                                .obtenerAsistenciaAbierta(
                                        Integer.MAX_VALUE));

        assertEquals(
                "El cliente no existe.",
                excepcion.getMessage());
    }


    @Test
    void despuesDeRegistrarSalidaNoDebeExistirAsistenciaAbierta() {

        Cliente cliente =
                crearClienteConMembresiaVigente();

        asistenciaService.registrarEntrada(
                cliente.getIdCliente());

        asistenciaService.registrarSalida(
                cliente.getIdCliente());

        Asistencia abierta =
                asistenciaService
                        .obtenerAsistenciaAbierta(
                                cliente.getIdCliente());

        assertNull(abierta);
    }


    private Cliente crearClienteConMembresiaVigente() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        Membresia membresia =
                new Membresia();

        membresia.setCliente(
                cliente);

        membresia.setTipoMembresia(
                tipo);

        membresia.setFechaInicio(
                LocalDate.now());

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        return cliente;
    }


    private Cliente crearClientePrueba() {

        Cliente cliente =
                new Cliente();

        cliente.setDni(
                generarDni());

        cliente.setNombres(
                "Cliente Asistencia");

        cliente.setApellidos(
                "Prueba Service");

        cliente.setTelefono(
                "987654321");

        cliente.setEstado(
                true);

        clienteService.registrarCliente(
                cliente);

        return cliente;
    }


    private TipoMembresia obtenerTipoActivo(
            TipoPlan plan) {

        TipoMembresia tipo =
                tipoMembresiaService
                        .obtenerTodosLosTiposMembresia()
                        .stream()
                        .filter(t ->
                                t.getNombre() == plan)
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No existe el plan "
                                                + plan
                                                + " en la base de datos."));

        if (!Boolean.TRUE.equals(
                tipo.getEstado())) {

            tipoMembresiaService
                    .activarTipoMembresia(
                            tipo.getIdTipo());

            tipo =
                    tipoMembresiaService
                            .obtenerTipoMembresiaPorId(
                                    tipo.getIdTipo());
        }

        return tipo;
    }


    private String generarDni() {

        long numero =
                Math.abs(
                        System.nanoTime())
                        % 90_000_000L
                        + 10_000_000L;

        return String.valueOf(
                numero);
    }
}