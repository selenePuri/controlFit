package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.TipoMembresia;
import model.enums.TipoPlan;

public class TipoMembresiaServiceTest {

    private final TipoMembresiaService tipoMembresiaService =
            new TipoMembresiaService();


    // RN-TM01, RN-TM02 y RN-TM03
    @Test
    void registrarTipoMembresiaCorrectamente() {

        /*
         * Como los cinco planes pueden existir en la BD,
         * comprobamos un registro ya existente.
         */
        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        assertNotNull(tipo);
        assertNotNull(tipo.getIdTipo());

        assertEquals(
                TipoPlan.MENSUAL,
                tipo.getNombre());

        assertTrue(
                tipo.getPrecio()
                        .compareTo(BigDecimal.ZERO) > 0);
    }


    // RN-TM03
    @Test
    void paseDiarioDebeDurarUnDia() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.PASE_DIARIO);

        tipo.setDuracionDias(999);

        tipoMembresiaService
                .actualizarTipoMembresia(tipo);

        TipoMembresia actualizado =
                tipoMembresiaService
                        .obtenerTipoMembresiaPorId(
                                tipo.getIdTipo());

        assertEquals(
                1,
                actualizado.getDuracionDias());
    }


    // RN-TM03
    @Test
    void mensualDebeDurarTreintaDias() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        tipo.setDuracionDias(999);

        tipoMembresiaService
                .actualizarTipoMembresia(tipo);

        TipoMembresia actualizado =
                tipoMembresiaService
                        .obtenerTipoMembresiaPorId(
                                tipo.getIdTipo());

        assertEquals(
                30,
                actualizado.getDuracionDias());
    }


    // RN-TM03
    @Test
    void trimestralDebeDurarNoventaDias() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.TRIMESTRAL);

        tipo.setDuracionDias(999);

        tipoMembresiaService
                .actualizarTipoMembresia(tipo);

        TipoMembresia actualizado =
                tipoMembresiaService
                        .obtenerTipoMembresiaPorId(
                                tipo.getIdTipo());

        assertEquals(
                90,
                actualizado.getDuracionDias());
    }


    // RN-TM03
    @Test
    void semestralDebeDurarCientoOchentaDias() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.SEMESTRAL);

        tipo.setDuracionDias(999);

        tipoMembresiaService
                .actualizarTipoMembresia(tipo);

        TipoMembresia actualizado =
                tipoMembresiaService
                        .obtenerTipoMembresiaPorId(
                                tipo.getIdTipo());

        assertEquals(
                180,
                actualizado.getDuracionDias());
    }


    // RN-TM03
    @Test
    void anualDebeDurarTrescientosSesentaYCincoDias() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.ANUAL);

        tipo.setDuracionDias(999);

        tipoMembresiaService
                .actualizarTipoMembresia(tipo);

        TipoMembresia actualizado =
                tipoMembresiaService
                        .obtenerTipoMembresiaPorId(
                                tipo.getIdTipo());

        assertEquals(
                365,
                actualizado.getDuracionDias());
    }


    // RN-TM02
    @Test
    void noDebeRegistrarTipoConPrecioCero() {

        TipoMembresia tipo =
                crearTipoPrueba(
                        TipoPlan.MENSUAL,
                        BigDecimal.ZERO);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> tipoMembresiaService
                                .registrarTipoMembresia(tipo));

        assertEquals(
                "El precio de la membresía debe ser mayor a 0.",
                excepcion.getMessage());
    }


    // RN-TM02
    @Test
    void noDebeRegistrarTipoConPrecioNegativo() {

        TipoMembresia tipo =
                crearTipoPrueba(
                        TipoPlan.MENSUAL,
                        new BigDecimal("-10.00"));

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> tipoMembresiaService
                                .registrarTipoMembresia(tipo));

        assertEquals(
                "El precio de la membresía debe ser mayor a 0.",
                excepcion.getMessage());
    }


    // RN-TM02
    @Test
    void noDebeRegistrarTipoSinPrecio() {

        TipoMembresia tipo =
                crearTipoPrueba(
                        TipoPlan.MENSUAL,
                        null);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> tipoMembresiaService
                                .registrarTipoMembresia(tipo));

        assertEquals(
                "El precio de la membresía es obligatorio.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarTipoSinPlan() {

        TipoMembresia tipo =
                crearTipoPrueba(
                        null,
                        new BigDecimal("80.00"));

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> tipoMembresiaService
                                .registrarTipoMembresia(tipo));

        assertEquals(
                "El plan de membresía es obligatorio.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarTipoNulo() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> tipoMembresiaService
                                .registrarTipoMembresia(null));

        assertEquals(
                "El tipo de membresía es obligatorio.",
                excepcion.getMessage());
    }


    @Test
    void obtenerTipoMembresiaPorIdCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        TipoMembresia encontrado =
                tipoMembresiaService
                        .obtenerTipoMembresiaPorId(
                                tipo.getIdTipo());

        assertNotNull(encontrado);

        assertEquals(
                tipo.getIdTipo(),
                encontrado.getIdTipo());

        assertEquals(
                TipoPlan.MENSUAL,
                encontrado.getNombre());
    }


    @Test
    void obtenerTodosLosTiposMembresiaCorrectamente() {

        List<TipoMembresia> tipos =
                tipoMembresiaService
                        .obtenerTodosLosTiposMembresia();

        assertNotNull(tipos);
        assertFalse(tipos.isEmpty());
    }


    // RN-TM01
    @Test
    void obtenerTiposMembresiaActivosCorrectamente() {

        List<TipoMembresia> activos =
                tipoMembresiaService
                        .obtenerTiposMembresiaActivos();

        assertNotNull(activos);

        assertTrue(
                activos.stream()
                        .allMatch(tipo ->
                                Boolean.TRUE.equals(
                                        tipo.getEstado())));
    }


    // RN-TM01
    @Test
    void desactivarTipoMembresiaCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        tipoMembresiaService
                .desactivarTipoMembresia(
                        tipo.getIdTipo());

        TipoMembresia desactivado =
                tipoMembresiaService
                        .obtenerTipoMembresiaPorId(
                                tipo.getIdTipo());

        assertFalse(desactivado.getEstado());

        // Dejamos el dato como estaba.
        tipoMembresiaService
                .activarTipoMembresia(
                        tipo.getIdTipo());
    }


    @Test
    void activarTipoMembresiaCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        tipoMembresiaService
                .desactivarTipoMembresia(
                        tipo.getIdTipo());

        tipoMembresiaService
                .activarTipoMembresia(
                        tipo.getIdTipo());

        TipoMembresia activado =
                tipoMembresiaService
                        .obtenerTipoMembresiaPorId(
                                tipo.getIdTipo());

        assertTrue(activado.getEstado());
    }


    // RN-TM01
    @Test
    void obtenerTipoMembresiaHabilitadoCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        tipoMembresiaService
                .activarTipoMembresia(
                        tipo.getIdTipo());

        TipoMembresia habilitado =
                tipoMembresiaService
                        .obtenerTipoMembresiaHabilitado(
                                tipo.getIdTipo());

        assertNotNull(habilitado);
        assertTrue(habilitado.getEstado());
    }


    // RN-TM01
    @Test
    void noDebeObtenerComoHabilitadoUnTipoInactivo() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        tipoMembresiaService
                .desactivarTipoMembresia(
                        tipo.getIdTipo());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> tipoMembresiaService
                                .obtenerTipoMembresiaHabilitado(
                                        tipo.getIdTipo()));

        assertEquals(
                "El tipo de membresía se encuentra inactivo.",
                excepcion.getMessage());

        // Dejamos el tipo activo nuevamente.
        tipoMembresiaService
                .activarTipoMembresia(
                        tipo.getIdTipo());
    }


    @Test
    void actualizarTipoMembresiaCorrectamente() {

        TipoMembresia tipo =
                buscarTipo(TipoPlan.MENSUAL);

        BigDecimal precioAnterior =
                tipo.getPrecio();

        String descripcionAnterior =
                tipo.getDescripcion();

        tipo.setDescripcion(
                "Plan mensual actualizado");

        tipo.setPrecio(
                new BigDecimal("85.00"));

        tipoMembresiaService
                .actualizarTipoMembresia(tipo);

        TipoMembresia actualizado =
                tipoMembresiaService
                        .obtenerTipoMembresiaPorId(
                                tipo.getIdTipo());

        assertEquals(
                "Plan mensual actualizado",
                actualizado.getDescripcion());

        assertEquals(
                0,
                new BigDecimal("85.00")
                        .compareTo(
                                actualizado.getPrecio()));

        assertEquals(
                30,
                actualizado.getDuracionDias());

        // Restauramos los datos originales.
        actualizado.setPrecio(precioAnterior);
        actualizado.setDescripcion(descripcionAnterior);

        tipoMembresiaService
                .actualizarTipoMembresia(actualizado);
    }


    @Test
    void noDebeObtenerTipoConIdInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> tipoMembresiaService
                                .obtenerTipoMembresiaPorId(0));

        assertEquals(
                "El código del tipo de membresía no es válido.",
                excepcion.getMessage());
    }


    private TipoMembresia crearTipoPrueba(
            TipoPlan plan,
            BigDecimal precio) {

        TipoMembresia tipo =
                new TipoMembresia();

        tipo.setNombre(plan);
        tipo.setDescripcion(
                "Tipo de membresía de prueba");

        tipo.setPrecio(precio);
        tipo.setEstado(true);

        return tipo;
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
    void noDebeRegistrarTipoMembresiaConPlanDuplicado() {

        TipoMembresia existente =
                tipoMembresiaService
                        .obtenerTodosLosTiposMembresia()
                        .stream()
                        .findFirst()
                        .orElseThrow();

        TipoMembresia duplicado =
                new TipoMembresia();

        duplicado.setNombre(
                existente.getNombre());

        duplicado.setPrecio(
                new BigDecimal("100.00"));

        duplicado.setEstado(true);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> tipoMembresiaService
                                .registrarTipoMembresia(
                                        duplicado));

        assertEquals(
                "Ya existe un tipo de membresía para ese plan.",
                excepcion.getMessage());
    }
}