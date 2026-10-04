package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import dao.ClienteDAOImpl;
import dao.EmpleadoDAOImpl;
import dao.EntrenadorDAOImpl;
import dao.RutinaClienteDAOImpl;
import dao.RutinaDAOImpl;
import dao.UsuarioDAOImpl;
import interfaces.ClienteDAO;
import interfaces.EmpleadoDAO;
import interfaces.EntrenadorDAO;
import interfaces.RutinaClienteDAO;
import interfaces.RutinaDAO;
import interfaces.UsuarioDAO;
import model.Cliente;
import model.Empleado;
import model.Entrenador;
import model.Rutina;
import model.RutinaCliente;
import model.Usuario;
import model.enums.Rol;

public class RutinaClienteDAOTest {

    private final UsuarioDAO usuarioDAO =
            new UsuarioDAOImpl();

    private final EmpleadoDAO empleadoDAO =
            new EmpleadoDAOImpl();

    private final EntrenadorDAO entrenadorDAO =
            new EntrenadorDAOImpl();

    private final ClienteDAO clienteDAO =
            new ClienteDAOImpl();

    private final RutinaDAO rutinaDAO =
            new RutinaDAOImpl();

    private final RutinaClienteDAO rutinaClienteDAO =
            new RutinaClienteDAOImpl();

    private String generarDni() {

        String numero =
                String.valueOf(System.nanoTime());

        return numero.substring(
                numero.length() - 8
        );
    }

    private Entrenador crearEntrenador() {

        Usuario usuario = new Usuario();

        usuario.setUsuario(
                "rc_ent_" + System.nanoTime()
        );
        usuario.setClave("12345");
        usuario.setRol(Rol.ENTRENADOR);
        usuario.setEstado(true);

        usuarioDAO.registrarUsuario(usuario);

        Empleado empleado = new Empleado();

        empleado.setDni(generarDni());
        empleado.setNombres("Entrenador");
        empleado.setApellidos("Test");
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setEstado(true);
        empleado.setUsuario(usuario);

        empleadoDAO.registrarEmpleado(empleado);

        Entrenador entrenador = new Entrenador();

        entrenador.setEmpleado(empleado);
        entrenador.setEspecialidad("Funcional");
        entrenador.setAnhosExperiencia(2);

        entrenadorDAO.registrarEntrenador(entrenador);

        return entrenador;
    }

    private Cliente crearCliente() {

        Cliente cliente = new Cliente();

        cliente.setDni(generarDni());
        cliente.setNombres("Cliente");
        cliente.setApellidos("Rutina");
        cliente.setEstado(true);

        clienteDAO.registrarCliente(cliente);

        return cliente;
    }

    private RutinaCliente crearAsignacion() {

        Rutina rutina = new Rutina();

        rutina.setEntrenador(crearEntrenador());
        rutina.setNombre(
                "Rutina RC " + System.nanoTime()
        );
        rutina.setDescripcion("Test");
        rutina.setObjetivo("Resistencia");
        rutina.setFechaCreacion(LocalDate.now());
        rutina.setEstado(true);

        rutinaDAO.registrarRutina(rutina);

        Cliente cliente = crearCliente();

        RutinaCliente asignacion =
                new RutinaCliente();

        asignacion.setRutina(rutina);
        asignacion.setCliente(cliente);
        asignacion.setFechaAsignacion(
                LocalDate.now()
        );
        asignacion.setFechaFin(
                LocalDate.now().plusMonths(1)
        );
        asignacion.setEstado(true);

        rutinaClienteDAO.registrarRutinaCliente(
                asignacion
        );

        return asignacion;
    }

    @Test
    void testRegistrarAsignacion() {

        RutinaCliente asignacion =
                crearAsignacion();

        assertNotNull(
                asignacion.getIdRutinaCliente()
        );
    }

    @Test
    void testBuscarPorId() {

        RutinaCliente asignacion =
                crearAsignacion();

        RutinaCliente encontrada =
                rutinaClienteDAO
                    .obtenerRutinaClientePorId(
                        asignacion
                            .getIdRutinaCliente()
                    );

        assertNotNull(encontrada);
    }

    @Test
    void testActualizarAsignacion() {

        RutinaCliente asignacion =
                crearAsignacion();

        asignacion.setEstado(false);

        rutinaClienteDAO.actualizarRutinaCliente(
                asignacion
        );

        RutinaCliente actualizada =
                rutinaClienteDAO
                    .obtenerRutinaClientePorId(
                        asignacion
                            .getIdRutinaCliente()
                    );

        assertFalse(actualizada.getEstado());
    }

    @Test
    void testRutinasPorCliente() {

        RutinaCliente asignacion =
                crearAsignacion();

        List<RutinaCliente> resultado =
                rutinaClienteDAO
                    .obtenerRutinasPorCliente(
                        asignacion
                            .getCliente()
                            .getIdCliente()
                    );

        assertFalse(resultado.isEmpty());
    }

    @Test
    void testClientesPorRutina() {

        RutinaCliente asignacion =
                crearAsignacion();

        List<RutinaCliente> resultado =
                rutinaClienteDAO
                    .obtenerClientesPorRutina(
                        asignacion
                            .getRutina()
                            .getIdRutina()
                    );

        assertFalse(resultado.isEmpty());
    }
}