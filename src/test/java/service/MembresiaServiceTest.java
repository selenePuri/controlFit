package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Cliente;
import model.Membresia;
import model.Pago;
import model.TipoMembresia;
import model.enums.EstadoMembresia;
import model.enums.MetodoPago;
import model.enums.TipoPlan;

public class MembresiaServiceTest {

    private final MembresiaService membresiaService =
            new MembresiaService();

    private final ClienteService clienteService =
            new ClienteService();

    private final TipoMembresiaService tipoMembresiaService =
            new TipoMembresiaService();

    private final PagoService pagoService =
            new PagoService();


    // RN-ME01 a RN-ME08, RN-ME12 y RN-PA05
    @Test
    void contratarMembresiaCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        LocalDate.now());

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        assertNotNull(
                membresia.getIdMembresia());

        assertEquals(
                EstadoMembresia.ACTIVA,
                membresia.getEstado());

        assertEquals(
                LocalDate.now().plusDays(29),
                membresia.getFechaFin());

        assertEquals(
                0,
                tipo.getPrecio().compareTo(
                        membresia.getPrecioBase()));

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        membresia.getDescuento()));

        assertEquals(
                0,
                tipo.getPrecio().compareTo(
                        membresia.getPrecioFinal()));
    }


    // RN-ME03
    @Test
    void debeCalcularFechaFinSegunDuracionPlan() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.TRIMESTRAL);

        LocalDate inicio =
                LocalDate.now().plusDays(400);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        inicio);

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        assertEquals(
                inicio.plusDays(89),
                membresia.getFechaFin());
    }


    // RN-TM04
    @Test
    void paseDiarioDebeFinalizarElMismoDia() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.PASE_DIARIO);

        LocalDate inicio =
                LocalDate.now().plusDays(800);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        inicio);

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        assertEquals(
                inicio,
                membresia.getFechaFin());
    }


    // RN-ME01 y RN-CL09
    @Test
    void noDebeContratarMembresiaClienteInactivo() {

        Cliente cliente =
                crearClientePrueba();

        clienteService.desactivarCliente(
                cliente.getIdCliente());

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        LocalDate.now());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> membresiaService
                                .contratarMembresia(
                                        membresia,
                                        MetodoPago.EFECTIVO));

        assertEquals(
                "El cliente se encuentra inactivo y no puede adquirir una membresía.",
                excepcion.getMessage());
    }


    // RN-ME02 y RN-TM01
    @Test
    void noDebeContratarConTipoMembresiaInactivo() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.ANUAL);

        tipoMembresiaService
                .desactivarTipoMembresia(
                        tipo.getIdTipo());

        try {

            Membresia membresia =
                    crearMembresiaPrueba(
                            cliente,
                            tipo,
                            LocalDate.now());

            IllegalStateException excepcion =
                    assertThrows(
                            IllegalStateException.class,
                            () -> membresiaService
                                    .contratarMembresia(
                                            membresia,
                                            MetodoPago.EFECTIVO));

            assertEquals(
                    "El tipo de membresía se encuentra inactivo.",
                    excepcion.getMessage());

        } finally {

            tipoMembresiaService
                    .activarTipoMembresia(
                            tipo.getIdTipo());
        }
    }


    @Test
    void noDebeContratarMembresiaNula() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> membresiaService
                                .contratarMembresia(
                                        null,
                                        MetodoPago.EFECTIVO));

        assertEquals(
                "La membresía es obligatoria.",
                excepcion.getMessage());
    }


    @Test
    void noDebeContratarMembresiaSinCliente() {

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        Membresia membresia =
                new Membresia();

        membresia.setTipoMembresia(
                tipo);

        membresia.setFechaInicio(
                LocalDate.now());

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> membresiaService
                                .contratarMembresia(
                                        membresia,
                                        MetodoPago.EFECTIVO));

        assertEquals(
                "Debe seleccionar un cliente.",
                excepcion.getMessage());
    }


    @Test
    void noDebeContratarMembresiaSinTipo() {

        Cliente cliente =
                crearClientePrueba();

        Membresia membresia =
                new Membresia();

        membresia.setCliente(
                cliente);

        membresia.setFechaInicio(
                LocalDate.now());

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> membresiaService
                                .contratarMembresia(
                                        membresia,
                                        MetodoPago.EFECTIVO));

        assertEquals(
                "Debe seleccionar un tipo de membresía.",
                excepcion.getMessage());
    }


    // RN-ME03
    @Test
    void noDebeContratarMembresiaSinFechaInicio() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        null);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> membresiaService
                                .contratarMembresia(
                                        membresia,
                                        MetodoPago.EFECTIVO));

        assertEquals(
                "La fecha de inicio es obligatoria.",
                excepcion.getMessage());
    }


    @Test
    void noDebeContratarMembresiaSinMetodoPago() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        LocalDate.now());

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> membresiaService
                                .contratarMembresia(
                                        membresia,
                                        null));

        assertEquals(
                "El método de pago es obligatorio.",
                excepcion.getMessage());
    }


    // RN-ME04, RN-ME05 y RN-ME06
    @Test
    void datosEconomicosDebenSerCalculadosPorSistema() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        LocalDate inicio =
                LocalDate.now().plusDays(1200);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        inicio);

        // Valores incorrectos enviados desde la interfaz
        membresia.setPrecioBase(
                new BigDecimal("9999"));

        membresia.setDescuento(
                new BigDecimal("5000"));

        membresia.setPrecioFinal(
                new BigDecimal("1"));

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        assertEquals(
                0,
                tipo.getPrecio().compareTo(
                        membresia.getPrecioBase()));

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        membresia.getDescuento()));

        assertEquals(
                0,
                tipo.getPrecio().compareTo(
                        membresia.getPrecioFinal()));
    }


    // RN-ME12 y RN-PA05
    @Test
    void contratarMembresiaDebeCrearPagoInicial() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        LocalDate inicio =
                LocalDate.now().plusDays(1600);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        inicio);

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        List<Pago> pagos =
                pagoService
                        .obtenerPagosPorMembresia(
                                membresia.getIdMembresia());

        assertNotNull(
                pagos);

        assertFalse(
                pagos.isEmpty());

        Pago pago =
                pagos.get(0);

        assertEquals(
                membresia.getIdMembresia(),
                pago.getMembresia()
                        .getIdMembresia());

        assertEquals(
                0,
                membresia.getPrecioFinal()
                        .compareTo(
                                pago.getMonto()));

        assertEquals(
                MetodoPago.EFECTIVO,
                pago.getMetodoPago());
    }


    // RN-ME13
    @Test
    void noDebePermitirMembresiasActivasSuperpuestas() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        LocalDate inicio =
                LocalDate.now().plusDays(2000);

        Membresia primera =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        inicio);

        membresiaService.contratarMembresia(
                primera,
                MetodoPago.EFECTIVO);

        Membresia segunda =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        inicio.plusDays(10));

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> membresiaService
                                .contratarMembresia(
                                        segunda,
                                        MetodoPago.EFECTIVO));

        assertEquals(
                "El cliente ya tiene una membresía activa durante ese período.",
                excepcion.getMessage());
    }


    // RN-ME13
    @Test
    void debePermitirMembresiaSinSolapamiento() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        LocalDate inicio =
                LocalDate.now().plusDays(2400);

        Membresia primera =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        inicio);

        membresiaService.contratarMembresia(
                primera,
                MetodoPago.EFECTIVO);

        Membresia segunda =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        primera.getFechaFin()
                                .plusDays(1));

        membresiaService.contratarMembresia(
                segunda,
                MetodoPago.EFECTIVO);

        assertNotNull(
                segunda.getIdMembresia());
    }


    // RN-ME09
    @Test
    void obtenerMembresiaVigenteCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        LocalDate fecha =
                LocalDate.now().plusDays(2800);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        fecha);

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        Membresia vigente =
                membresiaService
                        .obtenerMembresiaVigente(
                                cliente.getIdCliente(),
                                fecha);

        assertNotNull(
                vigente);

        assertEquals(
                membresia.getIdMembresia(),
                vigente.getIdMembresia());
    }


    // RN-ME09
    @Test
    void tieneMembresiaVigenteDebeRetornarTrue() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        LocalDate fecha =
                LocalDate.now().plusDays(3200);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        fecha);

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        assertTrue(
                membresiaService
                        .tieneMembresiaVigente(
                                cliente.getIdCliente(),
                                fecha));
    }


    // RN-ME11
    @Test
    void membresiaFueraDeFechaNoDebeEstarVigente() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        LocalDate inicio =
                LocalDate.now().plusDays(3600);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        inicio);

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        LocalDate despues =
                membresia.getFechaFin()
                        .plusDays(1);

        assertFalse(
                membresiaService
                        .tieneMembresiaVigente(
                                cliente.getIdCliente(),
                                despues));
    }


    // RN-ME11
    @Test
    void membresiaCanceladaNoDebeEstarVigente() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        LocalDate fecha =
                LocalDate.now().plusDays(4000);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        fecha);

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        membresiaService.actualizarEstado(
                membresia.getIdMembresia(),
                EstadoMembresia.CANCELADA);

        assertFalse(
                membresiaService
                        .tieneMembresiaVigente(
                                cliente.getIdCliente(),
                                fecha));
    }


    // RN-ME10
    @Test
    void obtenerMembresiasPorClienteCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        LocalDate inicio =
                LocalDate.now().plusDays(4400);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        inicio);

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        List<Membresia> membresias =
                membresiaService
                        .obtenerMembresiasPorCliente(
                                cliente.getIdCliente());

        assertNotNull(
                membresias);

        assertTrue(
                membresias.stream()
                        .anyMatch(m ->
                                m.getIdMembresia()
                                        .equals(
                                                membresia
                                                        .getIdMembresia())));
    }


    // RN-ME10
    @Test
    void noDebeObtenerMembresiasConIdClienteInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> membresiaService
                                .obtenerMembresiasPorCliente(0));

        assertEquals(
                "El código del cliente no es válido.",
                excepcion.getMessage());
    }


    // RN-ME10
    @Test
    void noDebeObtenerMembresiasConClienteInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> membresiaService
                                .obtenerMembresiasPorCliente(
                                        Integer.MAX_VALUE));

        assertEquals(
                "El cliente no existe.",
                excepcion.getMessage());
    }


    @Test
    void obtenerMembresiasPorEstadoCorrectamente() {

        List<Membresia> activas =
                membresiaService
                        .obtenerMembresiasPorEstado(
                                EstadoMembresia.ACTIVA);

        assertNotNull(
                activas);

        assertTrue(
                activas.stream()
                        .allMatch(m ->
                                m.getEstado()
                                        == EstadoMembresia.ACTIVA));
    }


    @Test
    void obtenerTodasLasMembresiasCorrectamente() {

        List<Membresia> membresias =
                membresiaService
                        .obtenerTodasLasMembresias();

        assertNotNull(
                membresias);
    }


    @Test
    void obtenerMembresiaPorIdCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        TipoMembresia tipo =
                obtenerTipoActivo(
                        TipoPlan.MENSUAL);

        LocalDate inicio =
                LocalDate.now().plusDays(4800);

        Membresia membresia =
                crearMembresiaPrueba(
                        cliente,
                        tipo,
                        inicio);

        membresiaService.contratarMembresia(
                membresia,
                MetodoPago.EFECTIVO);

        Membresia encontrada =
                membresiaService
                        .obtenerMembresiaPorId(
                                membresia.getIdMembresia());

        assertNotNull(
                encontrada);

        assertEquals(
                membresia.getIdMembresia(),
                encontrada.getIdMembresia());
    }


    private Cliente crearClientePrueba() {

        Cliente cliente =
                new Cliente();

        cliente.setDni(
                generarDni());

        cliente.setNombres(
                "Cliente Membresía");

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


    private Membresia crearMembresiaPrueba(
            Cliente cliente,
            TipoMembresia tipo,
            LocalDate fechaInicio) {

        Membresia membresia =
                new Membresia();

        membresia.setCliente(
                cliente);

        membresia.setTipoMembresia(
                tipo);

        membresia.setFechaInicio(
                fechaInicio);

        return membresia;
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