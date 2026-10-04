package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import dao.EmpleadoDAOImpl;
import dao.EntrenadorDAOImpl;
import dao.RutinaDAOImpl;
import dao.UsuarioDAOImpl;
import interfaces.EmpleadoDAO;
import interfaces.EntrenadorDAO;
import interfaces.RutinaDAO;
import interfaces.UsuarioDAO;
import model.Empleado;
import model.Entrenador;
import model.Rutina;
import model.Usuario;
import model.enums.Rol;

public class RutinaDAOTest {

    private final UsuarioDAO usuarioDAO =
            new UsuarioDAOImpl();

    private final EmpleadoDAO empleadoDAO =
            new EmpleadoDAOImpl();

    private final EntrenadorDAO entrenadorDAO =
            new EntrenadorDAOImpl();

    private final RutinaDAO rutinaDAO =
            new RutinaDAOImpl();

    private Entrenador crearEntrenador() {

        Usuario usuario = new Usuario();

        usuario.setUsuario(
                "rutina_ent_" + System.nanoTime()
        );
        usuario.setClave("12345");
        usuario.setRol(Rol.ENTRENADOR);
        usuario.setEstado(true);

        usuarioDAO.registrarUsuario(usuario);

        String numero =
                String.valueOf(System.nanoTime());

        numero = numero.substring(
                numero.length() - 8
        );

        Empleado empleado = new Empleado();

        empleado.setDni(numero);
        empleado.setNombres("Entrenador");
        empleado.setApellidos("Rutina");
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setEstado(true);
        empleado.setUsuario(usuario);

        empleadoDAO.registrarEmpleado(empleado);

        Entrenador entrenador = new Entrenador();

        entrenador.setEmpleado(empleado);
        entrenador.setEspecialidad("Fuerza");
        entrenador.setAnhosExperiencia(5);

        entrenadorDAO.registrarEntrenador(entrenador);

        return entrenador;
    }

    private Rutina crearRutina() {

        Rutina rutina = new Rutina();

        rutina.setEntrenador(crearEntrenador());
        rutina.setNombre(
                "Rutina Test " + System.nanoTime()
        );
        rutina.setDescripcion("Rutina de prueba");
        rutina.setObjetivo("Fuerza");
        rutina.setFechaCreacion(LocalDate.now());
        rutina.setEstado(true);

        rutinaDAO.registrarRutina(rutina);

        return rutina;
    }

    @Test
    void testRegistrarRutina() {

        Rutina rutina = crearRutina();

        assertNotNull(rutina.getIdRutina());
    }

    @Test
    void testBuscarRutinaPorId() {

        Rutina rutina = crearRutina();

        Rutina encontrada =
                rutinaDAO.obtenerRutinaPorId(
                        rutina.getIdRutina()
                );

        assertNotNull(encontrada);
    }

    @Test
    void testActualizarRutina() {

        Rutina rutina = crearRutina();

        rutina.setObjetivo("Hipertrofia");

        rutinaDAO.actualizarRutina(rutina);

        Rutina actualizada =
                rutinaDAO.obtenerRutinaPorId(
                        rutina.getIdRutina()
                );

        assertEquals(
                "Hipertrofia",
                actualizada.getObjetivo()
        );
    }

    @Test
    void testRutinasPorEntrenador() {

        Rutina rutina = crearRutina();

        List<Rutina> resultado =
                rutinaDAO.obtenerRutinasPorEntrenador(
                        rutina
                            .getEntrenador()
                            .getIdEntrenador()
                );

        assertFalse(resultado.isEmpty());
    }
}