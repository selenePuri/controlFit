package test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import dao.ClienteDAOImpl;
import dao.MembresiaDAOImpl;
import dao.TipoMembresiaDAOImpl;
import interfaces.ClienteDAO;
import interfaces.MembresiaDAO;
import interfaces.TipoMembresiaDAO;
import model.Cliente;
import model.Membresia;
import model.TipoMembresia;
import model.enums.EstadoMembresia;
import model.enums.TipoPlan;

public class MembresiaDAOTest {

    private final ClienteDAO clienteDAO =
            new ClienteDAOImpl();

    private final TipoMembresiaDAO tipoDAO =
            new TipoMembresiaDAOImpl();

    private final MembresiaDAO membresiaDAO =
            new MembresiaDAOImpl();

    private Cliente crearCliente() {

        String numero =
                String.valueOf(System.nanoTime());

        numero = numero.substring(
                numero.length() - 8
        );

        Cliente cliente = new Cliente();

        cliente.setDni(numero);
        cliente.setNombres("Cliente Membresia");
        cliente.setApellidos("Test");
        cliente.setEstado(true);

        clienteDAO.registrarCliente(cliente);

        return cliente;
    }

    private Membresia crearMembresia() {

        Cliente cliente = crearCliente();

        TipoMembresia tipo =
                tipoDAO.obtenerTipoMembresiaPorNombre(
                        TipoPlan.MENSUAL
                );

        assertNotNull(
                tipo,
                "Debe existir el plan MENSUAL"
        );

        Membresia membresia = new Membresia();

        membresia.setCliente(cliente);
        membresia.setTipoMembresia(tipo);
        membresia.setFechaInicio(LocalDate.now());
        membresia.setFechaFin(
                LocalDate.now().plusDays(30)
        );

        membresia.setPrecioBase(tipo.getPrecio());
        membresia.setDescuento(BigDecimal.ZERO);
        membresia.setPrecioFinal(tipo.getPrecio());
        membresia.setEstado(EstadoMembresia.ACTIVA);

        membresiaDAO.registrarMembresia(membresia);

        return membresia;
    }

    @Test
    void testRegistrarMembresia() {

        Membresia membresia = crearMembresia();

        assertNotNull(membresia.getIdMembresia());
    }

    @Test
    void testBuscarPorId() {

        Membresia membresia = crearMembresia();

        Membresia encontrada =
                membresiaDAO.obtenerMembresiaPorId(
                        membresia.getIdMembresia()
                );

        assertNotNull(encontrada);
    }

    @Test
    void testActualizarMembresia() {

        Membresia membresia = crearMembresia();

        membresia.setEstado(
                EstadoMembresia.CANCELADA
        );

        membresiaDAO.actualizarMembresia(membresia);

        Membresia actualizada =
                membresiaDAO.obtenerMembresiaPorId(
                        membresia.getIdMembresia()
                );

        assertEquals(
                EstadoMembresia.CANCELADA,
                actualizada.getEstado()
        );
    }

    @Test
    void testBuscarPorCliente() {

        Membresia membresia = crearMembresia();

        List<Membresia> resultado =
                membresiaDAO.obtenerMembresiasPorCliente(
                        membresia
                            .getCliente()
                            .getIdCliente()
                );

        assertFalse(resultado.isEmpty());
    }

    @Test
    void testBuscarPorEstado() {

        crearMembresia();

        List<Membresia> activas =
                membresiaDAO.obtenerMembresiasPorEstado(
                        EstadoMembresia.ACTIVA
                );

        assertFalse(activas.isEmpty());
    }
    
    @Test
    void testMembresiaPerteneceACliente() {

        Membresia membresia = crearMembresia();

        Membresia encontrada =
                membresiaDAO.obtenerMembresiaPorId(
                        membresia.getIdMembresia()
                );

        assertNotNull(encontrada);

        assertNotNull(encontrada.getCliente());

        assertEquals(
                membresia.getCliente().getIdCliente(),
                encontrada.getCliente().getIdCliente()
        );
    }
    
}