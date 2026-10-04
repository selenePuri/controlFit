package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import dao.AsistenciaDAOImpl;
import dao.ClienteDAOImpl;
import interfaces.AsistenciaDAO;
import interfaces.ClienteDAO;
import model.Asistencia;
import model.Cliente;

public class AsistenciaDAOTest {

    private final ClienteDAO clienteDAO =
            new ClienteDAOImpl();

    private final AsistenciaDAO asistenciaDAO =
            new AsistenciaDAOImpl();

    private Cliente crearCliente() {

        String numero =
                String.valueOf(System.nanoTime());

        numero = numero.substring(
                numero.length() - 8
        );

        Cliente cliente = new Cliente();

        cliente.setDni(numero);
        cliente.setNombres("Cliente Asistencia");
        cliente.setApellidos("Test");
        cliente.setEstado(true);

        clienteDAO.registrarCliente(cliente);

        return cliente;
    }

    private Asistencia crearAsistencia() {

        Cliente cliente = crearCliente();

        Asistencia asistencia = new Asistencia();

        asistencia.setCliente(cliente);
        asistencia.setFecha(LocalDate.now());
        asistencia.setHoraEntrada(LocalTime.of(10, 0));

        asistenciaDAO.registrarAsistencia(asistencia);

        return asistencia;
    }

    @Test
    void testRegistrarAsistencia() {

        Asistencia asistencia = crearAsistencia();

        assertNotNull(asistencia.getIdAsistencia());
    }

    @Test
    void testBuscarPorId() {

        Asistencia asistencia = crearAsistencia();

        Asistencia encontrada =
                asistenciaDAO.obtenerAsistenciaPorId(
                        asistencia.getIdAsistencia()
                );

        assertNotNull(encontrada);
    }

    @Test
    void testActualizarSalida() {

        Asistencia asistencia = crearAsistencia();

        asistencia.setHoraSalida(
                LocalTime.of(12, 0)
        );

        asistenciaDAO.actualizarAsistencia(
                asistencia
        );

        Asistencia actualizada =
                asistenciaDAO.obtenerAsistenciaPorId(
                        asistencia.getIdAsistencia()
                );

        assertEquals(
                LocalTime.of(12, 0),
                actualizada.getHoraSalida()
        );
    }

    @Test
    void testAsistenciasPorCliente() {

        Asistencia asistencia = crearAsistencia();

        List<Asistencia> resultado =
                asistenciaDAO.obtenerAsistenciasPorCliente(
                        asistencia
                            .getCliente()
                            .getIdCliente()
                );

        assertFalse(resultado.isEmpty());
    }

    @Test
    void testAsistenciasPorFecha() {

        crearAsistencia();

        List<Asistencia> resultado =
                asistenciaDAO.obtenerAsistenciasPorFecha(
                        LocalDate.now()
                );

        assertFalse(resultado.isEmpty());
    }
}