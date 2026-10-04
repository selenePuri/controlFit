package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import dao.TipoMembresiaDAOImpl;
import interfaces.TipoMembresiaDAO;
import model.TipoMembresia;
import model.enums.TipoPlan;

public class TipoMembresiaDAOTest {

    private final TipoMembresiaDAO dao =
            new TipoMembresiaDAOImpl();

    @Test
    void testBuscarPorNombre() {

        TipoMembresia mensual =
                dao.obtenerTipoMembresiaPorNombre(
                        TipoPlan.MENSUAL
                );

        assertNotNull(mensual);
        assertEquals(
                TipoPlan.MENSUAL,
                mensual.getNombre()
        );
    }

    @Test
    void testBuscarPorId() {

        TipoMembresia mensual =
                dao.obtenerTipoMembresiaPorNombre(
                        TipoPlan.MENSUAL
                );

        assertNotNull(mensual);

        TipoMembresia encontrado =
                dao.obtenerTipoMembresiaPorId(
                        mensual.getIdTipo()
                );

        assertNotNull(encontrado);
    }

    @Test
    void testListarTipos() {

        List<TipoMembresia> tipos =
                dao.obtenerTodosLosTiposMembresia();

        assertNotNull(tipos);

        // Aquí estan los 5 planes insertados 
        assertTrue(tipos.size() >= 5);
    }
}