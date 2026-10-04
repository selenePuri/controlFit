package test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import dao.ClienteDAOImpl;
import dao.MembresiaDAOImpl;
import dao.PagoDAOImpl;
import dao.TipoMembresiaDAOImpl;
import interfaces.ClienteDAO;
import interfaces.MembresiaDAO;
import interfaces.PagoDAO;
import interfaces.TipoMembresiaDAO;
import model.Cliente;
import model.Membresia;
import model.Pago;
import model.TipoMembresia;
import model.enums.EstadoMembresia;
import model.enums.EstadoPago;
import model.enums.MetodoPago;
import model.enums.TipoPlan;

public class PagoDAOTest {

    private final ClienteDAO clienteDAO =
            new ClienteDAOImpl();

    private final MembresiaDAO membresiaDAO =
            new MembresiaDAOImpl();

    private final TipoMembresiaDAO tipoDAO =
            new TipoMembresiaDAOImpl();

    private final PagoDAO pagoDAO =
            new PagoDAOImpl();

    private Membresia crearMembresia() {

        String numero =
                String.valueOf(System.nanoTime());

        numero = numero.substring(
                numero.length() - 8
        );

        Cliente cliente = new Cliente();

        cliente.setDni(numero);
        cliente.setNombres("Cliente Pago");
        cliente.setApellidos("Test");
        cliente.setEstado(true);

        clienteDAO.registrarCliente(cliente);

        TipoMembresia tipo =
                tipoDAO.obtenerTipoMembresiaPorNombre(
                        TipoPlan.MENSUAL
                );

        assertNotNull(tipo);

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

    private Pago crearPago() {

        Membresia membresia = crearMembresia();

        Pago pago = new Pago();

        pago.setMembresia(membresia);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMonto(membresia.getPrecioFinal());
        pago.setMetodoPago(MetodoPago.YAPE);
        pago.setEstado(EstadoPago.PAGADO);

        pagoDAO.registrarPago(pago);

        return pago;
    }

    @Test
    void testRegistrarPago() {

        Pago pago = crearPago();

        assertNotNull(pago.getIdPago());
    }

    @Test
    void testBuscarPagoPorId() {

        Pago pago = crearPago();

        Pago encontrado =
                pagoDAO.obtenerPagoPorId(
                        pago.getIdPago()
                );

        assertNotNull(encontrado);
    }

    @Test
    void testActualizarPago() {

        Pago pago = crearPago();

        pago.setEstado(EstadoPago.ANULADO);

        pagoDAO.actualizarPago(pago);

        Pago actualizado =
                pagoDAO.obtenerPagoPorId(
                        pago.getIdPago()
                );

        assertEquals(
                EstadoPago.ANULADO,
                actualizado.getEstado()
        );
    }

    @Test
    void testPagosPorMembresia() {

        Pago pago = crearPago();

        List<Pago> pagos =
                pagoDAO.obtenerPagosPorMembresia(
                        pago
                            .getMembresia()
                            .getIdMembresia()
                );

        assertFalse(pagos.isEmpty());
    }

    @Test
    void testPagosPorEstado() {

        crearPago();

        List<Pago> pagos =
                pagoDAO.obtenerPagosPorEstado(
                        EstadoPago.PAGADO
                );

        assertFalse(pagos.isEmpty());
    }
}