package test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import dao.PromocionDAOImpl;
import interfaces.PromocionDAO;
import model.Promocion;
import model.enums.TipoDescuento;

public class PromocionDAOTest {

    private final PromocionDAO dao =
            new PromocionDAOImpl();

    private Promocion crearPromocion() {

        Promocion promocion = new Promocion();

        promocion.setNombre(
                "Promocion Test " + System.nanoTime()
        );

        promocion.setDescripcion(
                "Promocion creada por JUnit"
        );

        promocion.setTipoDescuento(
                TipoDescuento.PORCENTAJE
        );

        promocion.setValorDescuento(
                new BigDecimal("10.00")
        );

        promocion.setFechaInicio(
                LocalDate.now().minusDays(1)
        );

        promocion.setFechaFin(
                LocalDate.now().plusDays(10)
        );

        promocion.setEstado(true);

        dao.registrarPromocion(promocion);

        return promocion;
    }

    @Test
    void testRegistrarPromocion() {

        Promocion promocion = crearPromocion();

        assertNotNull(promocion.getIdPromocion());
    }

    @Test
    void testBuscarPorId() {

        Promocion promocion = crearPromocion();

        Promocion encontrada =
                dao.obtenerPromocionPorId(
                        promocion.getIdPromocion()
                );

        assertNotNull(encontrada);
    }

    @Test
    void testActualizarPromocion() {

        Promocion promocion = crearPromocion();

        promocion.setValorDescuento(
                new BigDecimal("15.00")
        );

        dao.actualizarPromocion(promocion);

        Promocion actualizada =
                dao.obtenerPromocionPorId(
                        promocion.getIdPromocion()
                );

        assertEquals(
                0,
                new BigDecimal("15.00")
                    .compareTo(actualizada.getValorDescuento())
        );
    }

    @Test
    void testPromocionesVigentes() {

        crearPromocion();

        List<Promocion> promociones =
                dao.obtenerPromocionesVigentes(
                        LocalDate.now()
                );

        assertFalse(promociones.isEmpty());
    }
}