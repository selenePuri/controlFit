package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Promocion;
import model.TipoMembresia;
import model.enums.TipoDescuento;
import model.enums.TipoPlan;

public class PromocionServiceTest {

    private final PromocionService promocionService =
            new PromocionService();

    private final TipoMembresiaService tipoMembresiaService =
            new TipoMembresiaService();


    // RN-PR01, RN-PR02, RN-PR03 y RN-PR05
    @Test
    void registrarPromocionCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                crearPromocionPrueba(
                        TipoDescuento.PORCENTAJE,
                        new BigDecimal("20.00"),
                        tipo);

        promocionService.registrarPromocion(promocion);

        assertNotNull(promocion.getIdPromocion());

        Promocion guardada =
                promocionService.obtenerPromocionPorId(
                        promocion.getIdPromocion());

        assertNotNull(guardada);
        assertTrue(guardada.getEstado());

        assertEquals(
                promocion.getNombre(),
                guardada.getNombre());

        assertEquals(
                TipoDescuento.PORCENTAJE,
                guardada.getTipoDescuento());

        assertTrue(
                guardada.getTiposMembresia()
                        .stream()
                        .anyMatch(t ->
                                t.getIdTipo()
                                        .equals(tipo.getIdTipo())));
    }


    @Test
    void noDebeRegistrarPromocionSinNombre() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                crearPromocionPrueba(
                        TipoDescuento.PORCENTAJE,
                        new BigDecimal("20"),
                        tipo);

        promocion.setNombre("");

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> promocionService
                                .registrarPromocion(promocion));

        assertEquals(
                "El nombre de la promoción es obligatorio.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarPromocionSinTipoDescuento() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                crearPromocionPrueba(
                        TipoDescuento.PORCENTAJE,
                        new BigDecimal("20"),
                        tipo);

        promocion.setTipoDescuento(null);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> promocionService
                                .registrarPromocion(promocion));

        assertEquals(
                "El tipo de descuento es obligatorio.",
                excepcion.getMessage());
    }


    // RN-PR05
    @Test
    void noDebeRegistrarPromocionConDescuentoCero() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                crearPromocionPrueba(
                        TipoDescuento.PORCENTAJE,
                        BigDecimal.ZERO,
                        tipo);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> promocionService
                                .registrarPromocion(promocion));

        assertEquals(
                "El valor del descuento debe ser mayor a 0.",
                excepcion.getMessage());
    }


    // RN-PR05
    @Test
    void noDebeRegistrarPorcentajeMayorACien() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                crearPromocionPrueba(
                        TipoDescuento.PORCENTAJE,
                        new BigDecimal("101"),
                        tipo);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> promocionService
                                .registrarPromocion(promocion));

        assertEquals(
                "El porcentaje de descuento no puede ser mayor a 100.",
                excepcion.getMessage());
    }


    // RN-PR02
    @Test
    void noDebeRegistrarPromocionConFechasInvalidas() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                crearPromocionPrueba(
                        TipoDescuento.PORCENTAJE,
                        new BigDecimal("20"),
                        tipo);

        promocion.setFechaInicio(
                LocalDate.now().plusDays(10));

        promocion.setFechaFin(
                LocalDate.now());

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> promocionService
                                .registrarPromocion(promocion));

        assertEquals(
                "La fecha de fin no puede ser anterior a la fecha de inicio.",
                excepcion.getMessage());
    }


    // RN-PR03
    @Test
    void noDebeRegistrarPromocionSinTipoMembresia() {

        Promocion promocion =
                new Promocion();

        promocion.setNombre(
                "Promoción sin membresía");

        promocion.setTipoDescuento(
                TipoDescuento.PORCENTAJE);

        promocion.setValorDescuento(
                new BigDecimal("20"));

        promocion.setFechaInicio(
                LocalDate.now().minusDays(1));

        promocion.setFechaFin(
                LocalDate.now().plusDays(10));

        promocion.setEstado(true);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> promocionService
                                .registrarPromocion(promocion));

        assertEquals(
                "La promoción debe estar asociada al menos a un tipo de membresía.",
                excepcion.getMessage());
    }


    // RN-PR04
    @Test
    void calcularDescuentoPorcentajeCorrectamente() {

        Promocion promocion = new Promocion();

        promocion.setTipoDescuento(
                TipoDescuento.PORCENTAJE);

        promocion.setValorDescuento(
                new BigDecimal("20"));

        BigDecimal descuento =
                promocionService.calcularDescuento(
                        promocion,
                        new BigDecimal("100.00"));

        assertEquals(
                0,
                new BigDecimal("20.00")
                        .compareTo(descuento));
    }


    // RN-PR04
    @Test
    void calcularDescuentoMontoFijoCorrectamente() {

        Promocion promocion = new Promocion();

        promocion.setTipoDescuento(
                TipoDescuento.MONTO_FIJO);

        promocion.setValorDescuento(
                new BigDecimal("25.00"));

        BigDecimal descuento =
                promocionService.calcularDescuento(
                        promocion,
                        new BigDecimal("100.00"));

        assertEquals(
                0,
                new BigDecimal("25.00")
                        .compareTo(descuento));
    }


    @Test
    void promocionNulaDebeDarDescuentoCero() {

        BigDecimal descuento =
                promocionService.calcularDescuento(
                        null,
                        new BigDecimal("100.00"));

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(descuento));
    }


    // RN-PR05 y RN-PR06
    @Test
    void noDebePermitirDescuentoMayorAlPrecio() {

        Promocion promocion = new Promocion();

        promocion.setTipoDescuento(
                TipoDescuento.MONTO_FIJO);

        promocion.setValorDescuento(
                new BigDecimal("150.00"));

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> promocionService
                                .calcularDescuento(
                                        promocion,
                                        new BigDecimal("100.00")));

        assertEquals(
                "El descuento no puede ser mayor al precio de la membresía.",
                excepcion.getMessage());
    }


    @Test
    void noDebeCalcularDescuentoConPrecioBaseCero() {

        Promocion promocion = new Promocion();

        promocion.setTipoDescuento(
                TipoDescuento.PORCENTAJE);

        promocion.setValorDescuento(
                new BigDecimal("20"));

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> promocionService
                                .calcularDescuento(
                                        promocion,
                                        BigDecimal.ZERO));

        assertEquals(
                "El precio base debe ser mayor a 0.",
                excepcion.getMessage());
    }


    // RN-PR01, RN-PR02 y RN-PR03
    @Test
    void obtenerPromocionValidaCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                registrarPromocionVigente(tipo);

        Promocion valida =
                promocionService.obtenerPromocionValida(
                        promocion.getIdPromocion(),
                        tipo,
                        LocalDate.now());

        assertNotNull(valida);

        assertEquals(
                promocion.getIdPromocion(),
                valida.getIdPromocion());
    }


    // RN-PR01
    @Test
    void noDebeAceptarPromocionInactiva() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                registrarPromocionVigente(tipo);

        promocionService.desactivarPromocion(
                promocion.getIdPromocion());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> promocionService
                                .obtenerPromocionValida(
                                        promocion.getIdPromocion(),
                                        tipo,
                                        LocalDate.now()));

        assertEquals(
                "La promoción se encuentra inactiva.",
                excepcion.getMessage());
    }


    // RN-PR02
    @Test
    void noDebeAceptarPromocionFueraDeVigencia() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                registrarPromocionVigente(tipo);

        LocalDate fechaFuera =
                promocion.getFechaFin().plusDays(1);

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> promocionService
                                .obtenerPromocionValida(
                                        promocion.getIdPromocion(),
                                        tipo,
                                        fechaFuera));

        assertEquals(
                "La promoción no se encuentra vigente.",
                excepcion.getMessage());
    }


    // RN-PR03
    @Test
    void noDebeAceptarPromocionParaTipoNoCompatible() {

        TipoMembresia mensual =
                buscarTipo(TipoPlan.MENSUAL);

        TipoMembresia anual =
                buscarTipo(TipoPlan.ANUAL);

        Promocion promocion =
                registrarPromocionVigente(mensual);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> promocionService
                                .obtenerPromocionValida(
                                        promocion.getIdPromocion(),
                                        anual,
                                        LocalDate.now()));

        assertEquals(
                "La promoción no aplica al tipo de membresía seleccionado.",
                excepcion.getMessage());
    }


    @Test
    void obtenerPromocionesVigentesCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                registrarPromocionVigente(tipo);

        List<Promocion> promociones =
                promocionService
                        .obtenerPromocionesVigentes(
                                LocalDate.now());

        assertNotNull(promociones);

        assertTrue(
                promociones.stream()
                        .anyMatch(p ->
                                p.getIdPromocion()
                                        .equals(
                                                promocion
                                                        .getIdPromocion())));
    }


    @Test
    void obtenerPromocionesVigentesPorTipoCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                registrarPromocionVigente(tipo);

        List<Promocion> promociones =
                promocionService
                        .obtenerPromocionesVigentesPorTipoMembresia(
                                tipo.getIdTipo(),
                                LocalDate.now());

        assertNotNull(promociones);

        assertTrue(
                promociones.stream()
                        .anyMatch(p ->
                                p.getIdPromocion()
                                        .equals(
                                                promocion
                                                        .getIdPromocion())));
    }


    @Test
    void actualizarPromocionCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                registrarPromocionVigente(tipo);

        promocion.setDescripcion(
                "Promoción actualizada");

        promocion.setValorDescuento(
                new BigDecimal("15"));

        promocionService
                .actualizarPromocion(promocion);

        Promocion actualizada =
                promocionService.obtenerPromocionPorId(
                        promocion.getIdPromocion());

        assertEquals(
                "Promoción actualizada",
                actualizada.getDescripcion());

        assertEquals(
                0,
                new BigDecimal("15")
                        .compareTo(
                                actualizada
                                        .getValorDescuento()));
    }


    @Test
    void activarYDesactivarPromocionCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        Promocion promocion =
                registrarPromocionVigente(tipo);

        promocionService.desactivarPromocion(
                promocion.getIdPromocion());

        Promocion desactivada =
                promocionService.obtenerPromocionPorId(
                        promocion.getIdPromocion());

        assertFalse(desactivada.getEstado());

        promocionService.activarPromocion(
                promocion.getIdPromocion());

        Promocion activada =
                promocionService.obtenerPromocionPorId(
                        promocion.getIdPromocion());

        assertTrue(activada.getEstado());
    }


    private Promocion registrarPromocionVigente(
            TipoMembresia tipo) {

        Promocion promocion =
                crearPromocionPrueba(
                        TipoDescuento.PORCENTAJE,
                        new BigDecimal("10"),
                        tipo);

        promocionService.registrarPromocion(promocion);

        return promocion;
    }


    private Promocion crearPromocionPrueba(
            TipoDescuento tipoDescuento,
            BigDecimal valor,
            TipoMembresia tipoMembresia) {

        Promocion promocion = new Promocion();

        promocion.setNombre(
                "Promo_" + System.nanoTime());

        promocion.setDescripcion(
                "Promoción de prueba");

        promocion.setTipoDescuento(
                tipoDescuento);

        promocion.setValorDescuento(valor);

        promocion.setFechaInicio(
                LocalDate.now().minusDays(1));

        promocion.setFechaFin(
                LocalDate.now().plusDays(30));

        promocion.setEstado(true);

        if (tipoMembresia != null) {
            promocion.getTiposMembresia()
                    .add(tipoMembresia);
        }

        return promocion;
    }


    private TipoMembresia buscarTipo(
            TipoPlan plan) {

        return tipoMembresiaService
                .obtenerTodosLosTiposMembresia()
                .stream()
                .filter(tipo ->
                        tipo.getNombre() == plan)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe el plan "
                                        + plan
                                        + " en la base de datos."));
    }
    
    @Test
    void noDebeRegistrarPromocionConTipoMembresiaInexistente() {

        TipoMembresia tipo =
                new TipoMembresia();

        tipo.setIdTipo(
                Integer.MAX_VALUE);

        Promocion promocion =
                new Promocion();

        promocion.setNombre(
                "Promoción inválida");

        promocion.setTipoDescuento(
                TipoDescuento.PORCENTAJE);

        promocion.setValorDescuento(
                new BigDecimal("10"));

        promocion.setFechaInicio(
                LocalDate.now());

        promocion.setFechaFin(
                LocalDate.now().plusDays(10));

        promocion.getTiposMembresia()
                .add(tipo);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> promocionService
                                .registrarPromocion(
                                        promocion));

        assertEquals(
                "El tipo de membresía no existe.",
                excepcion.getMessage());
    }
}