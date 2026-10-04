package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Cliente;
import model.Membresia;
import model.Pago;
import model.TipoMembresia;
import model.enums.EstadoPago;
import model.enums.MetodoPago;
import model.enums.TipoPlan;

public class PagoServiceTest {

    private final PagoService pagoService =
            new PagoService();

    private final MembresiaService membresiaService =
            new MembresiaService();

    private final ClienteService clienteService =
            new ClienteService();

    private final TipoMembresiaService tipoMembresiaService =
            new TipoMembresiaService();


    // RN-PA01, RN-PA02, RN-PA03 y RN-PA04
    @Test
    void registrarPagoCorrectamente() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pagoService.registrarPago(pago);

        assertNotNull(
                pago.getIdPago());

        assertNotNull(
                pago.getFechaPago());

        assertEquals(
                EstadoPago.PAGADO,
                pago.getEstado());

        assertEquals(
                MetodoPago.EFECTIVO,
                pago.getMetodoPago());

        assertEquals(
                0,
                new BigDecimal("50.00")
                        .compareTo(
                                pago.getMonto()));
    }


    // RN-PA04
    @Test
    void pagoSinEstadoDebeQuedarPagado() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pago.setEstado(null);

        pagoService.registrarPago(pago);

        assertEquals(
                EstadoPago.PAGADO,
                pago.getEstado());
    }


    @Test
    void pagoSinFechaDebeAsignarFechaAutomaticamente() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pago.setFechaPago(null);

        LocalDateTime antes =
                LocalDateTime.now();

        pagoService.registrarPago(pago);

        LocalDateTime despues =
                LocalDateTime.now();

        assertNotNull(
                pago.getFechaPago());

        assertFalse(
                pago.getFechaPago()
                        .isBefore(antes));

        assertFalse(
                pago.getFechaPago()
                        .isAfter(despues));
    }


    @Test
    void noDebeRegistrarPagoNulo() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .registrarPago(null));

        assertEquals(
                "El pago es obligatorio.",
                excepcion.getMessage());
    }


    // RN-PA01
    @Test
    void noDebeRegistrarPagoSinMembresia() {

        Pago pago =
                new Pago();

        pago.setMonto(
                new BigDecimal("50.00"));

        pago.setMetodoPago(
                MetodoPago.EFECTIVO);

        pago.setEstado(
                EstadoPago.PAGADO);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .registrarPago(pago));

        assertEquals(
                "El pago debe estar asociado a una membresía.",
                excepcion.getMessage());
    }


    // RN-PA01
    @Test
    void noDebeRegistrarPagoConMembresiaSinId() {

        Membresia membresia =
                new Membresia();

        Pago pago =
                new Pago();

        pago.setMembresia(
                membresia);

        pago.setMonto(
                new BigDecimal("50.00"));

        pago.setMetodoPago(
                MetodoPago.EFECTIVO);

        pago.setEstado(
                EstadoPago.PAGADO);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .registrarPago(pago));

        assertEquals(
                "El pago debe estar asociado a una membresía.",
                excepcion.getMessage());
    }


    // RN-PA01
    @Test
    void noDebeRegistrarPagoConMembresiaInexistente() {

        Membresia membresia =
                new Membresia();

        membresia.setIdMembresia(
                Integer.MAX_VALUE);

        Pago pago =
                crearPagoPrueba(
                        membresia);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .registrarPago(pago));

        assertEquals(
                "La membresía asociada al pago no existe.",
                excepcion.getMessage());
    }


    // RN-PA02
    @Test
    void noDebeRegistrarPagoConMontoCero() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pago.setMonto(
                BigDecimal.ZERO);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .registrarPago(pago));

        assertEquals(
                "El monto del pago debe ser mayor a 0.",
                excepcion.getMessage());
    }


    // RN-PA02
    @Test
    void noDebeRegistrarPagoConMontoNegativo() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pago.setMonto(
                new BigDecimal("-10.00"));

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .registrarPago(pago));

        assertEquals(
                "El monto del pago debe ser mayor a 0.",
                excepcion.getMessage());
    }


    // RN-PA02
    @Test
    void noDebeRegistrarPagoSinMonto() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pago.setMonto(null);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .registrarPago(pago));

        assertEquals(
                "El monto del pago debe ser mayor a 0.",
                excepcion.getMessage());
    }


    // RN-PA03
    @Test
    void noDebeRegistrarPagoSinMetodoPago() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pago.setMetodoPago(null);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .registrarPago(pago));

        assertEquals(
                "El método de pago es obligatorio.",
                excepcion.getMessage());
    }


    @Test
    void obtenerPagoPorIdCorrectamente() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pagoService.registrarPago(pago);

        Pago encontrado =
                pagoService.obtenerPagoPorId(
                        pago.getIdPago());

        assertNotNull(
                encontrado);

        assertEquals(
                pago.getIdPago(),
                encontrado.getIdPago());
    }


    @Test
    void noDebeObtenerPagoConIdInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .obtenerPagoPorId(0));

        assertEquals(
                "El código del pago no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeObtenerPagoInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .obtenerPagoPorId(
                                        Integer.MAX_VALUE));

        assertEquals(
                "El pago no existe.",
                excepcion.getMessage());
    }


    @Test
    void obtenerTodosLosPagosCorrectamente() {

        List<Pago> pagos =
                pagoService
                        .obtenerTodosLosPagos();

        assertNotNull(
                pagos);
    }


    @Test
    void obtenerPagosPorMembresiaCorrectamente() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pagoService.registrarPago(pago);

        List<Pago> pagos =
                pagoService
                        .obtenerPagosPorMembresia(
                                membresia
                                        .getIdMembresia());

        assertNotNull(
                pagos);

        assertTrue(
                pagos.stream()
                        .anyMatch(p ->
                                p.getIdPago()
                                        .equals(
                                                pago.getIdPago())));
    }


    @Test
    void noDebeBuscarPagosConIdMembresiaInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .obtenerPagosPorMembresia(0));

        assertEquals(
                "El código de la membresía no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeBuscarPagosConMembresiaInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .obtenerPagosPorMembresia(
                                        Integer.MAX_VALUE));

        assertEquals(
                "La membresía no existe.",
                excepcion.getMessage());
    }


    // RN-PA04
    @Test
    void obtenerPagosPorEstadoCorrectamente() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pagoService.registrarPago(pago);

        List<Pago> pagos =
                pagoService
                        .obtenerPagosPorEstado(
                                EstadoPago.PAGADO);

        assertNotNull(
                pagos);

        assertTrue(
                pagos.stream()
                        .anyMatch(p ->
                                p.getIdPago()
                                        .equals(
                                                pago.getIdPago())));

        assertTrue(
                pagos.stream()
                        .allMatch(p ->
                                p.getEstado()
                                        == EstadoPago.PAGADO));
    }


    @Test
    void noDebeBuscarPagosSinEstado() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .obtenerPagosPorEstado(null));

        assertEquals(
                "El estado del pago es obligatorio.",
                excepcion.getMessage());
    }


    @Test
    void actualizarPagoCorrectamente() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(membresia);

        pagoService.registrarPago(pago);

        pago.setMonto(
                new BigDecimal("75.00"));

        pagoService.actualizarPago(pago);

        Pago actualizado =
                pagoService
                        .obtenerPagoPorId(
                                pago.getIdPago());

        assertEquals(
                0,
                new BigDecimal("75.00")
                        .compareTo(
                                actualizado.getMonto()));
    }


    @Test
    void noDebeActualizarPagoSinId() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(
                        membresia);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .actualizarPago(pago));

        assertEquals(
                "El pago que desea actualizar no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeActualizarPagoInexistente() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(
                        membresia);

        pago.setIdPago(
                Integer.MAX_VALUE);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .actualizarPago(pago));

        assertEquals(
                "El pago no existe.",
                excepcion.getMessage());
    }


    // RN-PA01
    @Test
    void noDebeActualizarPagoConMembresiaInexistente() {

        Membresia membresia =
                crearMembresiaPrueba();

        Pago pago =
                crearPagoPrueba(
                        membresia);

        pagoService.registrarPago(
                pago);

        Membresia inexistente =
                new Membresia();

        inexistente.setIdMembresia(
                Integer.MAX_VALUE);

        pago.setMembresia(
                inexistente);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pagoService
                                .actualizarPago(pago));

        assertEquals(
                "La membresía asociada al pago no existe.",
                excepcion.getMessage());
    }


    private Pago crearPagoPrueba(
            Membresia membresia) {

        Pago pago =
                new Pago();

        pago.setMembresia(
                membresia);

        pago.setMonto(
                new BigDecimal("50.00"));

        pago.setMetodoPago(
                MetodoPago.EFECTIVO);

        pago.setEstado(
                EstadoPago.PAGADO);

        return pago;
    }


    private Membresia crearMembresiaPrueba() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        // Cada cliente es nuevo para evitar solapamientos
        Membresia membresia =
                new Membresia();

        membresia.setCliente(
                cliente);

        membresia.setTipoMembresia(
                tipo);

        membresia.setFechaInicio(
                LocalDate.now());

        membresiaService
                .contratarMembresia(
                        membresia,
                        MetodoPago.EFECTIVO);

        return membresia;
    }


    private Cliente crearClientePrueba() {

        Cliente cliente =
                new Cliente();

        cliente.setDni(
                generarDni());

        cliente.setNombres(
                "Cliente Pago");

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