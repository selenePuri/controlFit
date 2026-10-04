package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import dao.ClienteDAOImpl;
import interfaces.ClienteDAO;
import model.Cliente;

public class ClienteDAOTest {

    private final ClienteDAO dao = new ClienteDAOImpl();

    private Cliente crearCliente() {

        Cliente cliente = new Cliente();

        String numero =
                String.valueOf(System.nanoTime());

        numero = numero.substring(
                numero.length() - 8
        );

        cliente.setDni(numero);
        cliente.setNombres("Cliente");
        cliente.setApellidos("Prueba");
        cliente.setFechaNacimiento(
                LocalDate.of(2000, 1, 1)
        );
        cliente.setEstado(true);

        dao.registrarCliente(cliente);

        return cliente;
    }

    @Test
    void testRegistrarCliente() {

        Cliente cliente = crearCliente();

        assertNotNull(cliente.getIdCliente());
    }

    @Test
    void testObtenerClientePorId() {

        Cliente cliente = crearCliente();

        Cliente encontrado =
                dao.obtenerClientePorId(
                        cliente.getIdCliente()
                );

        assertNotNull(encontrado);
        assertEquals(
                cliente.getDni(),
                encontrado.getDni()
        );
    }

    @Test
    void testBuscarPorDni() {

        Cliente cliente = crearCliente();

        Cliente encontrado =
                dao.obtenerClientePorDni(
                        cliente.getDni()
                );

        assertNotNull(encontrado);
        assertEquals(
                cliente.getIdCliente(),
                encontrado.getIdCliente()
        );
    }

    @Test
    void testActualizarCliente() {

        Cliente cliente = crearCliente();

        cliente.setNombres("Cliente Actualizado");

        dao.actualizarCliente(cliente);

        Cliente actualizado =
                dao.obtenerClientePorId(
                        cliente.getIdCliente()
                );

        assertEquals(
                "Cliente Actualizado",
                actualizado.getNombres()
        );
    }

    @Test
    void testListarClientes() {

        crearCliente();

        List<Cliente> clientes =
                dao.obtenerTodosLosClientes();

        assertNotNull(clientes);
        assertFalse(clientes.isEmpty());
    }
}