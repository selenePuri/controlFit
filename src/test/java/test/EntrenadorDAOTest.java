package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import dao.EmpleadoDAOImpl;
import dao.EntrenadorDAOImpl;
import dao.UsuarioDAOImpl;
import interfaces.EmpleadoDAO;
import interfaces.EntrenadorDAO;
import interfaces.UsuarioDAO;
import model.Empleado;
import model.Entrenador;
import model.Usuario;
import model.enums.Rol;

public class EntrenadorDAOTest {

    private final UsuarioDAO usuarioDAO =
            new UsuarioDAOImpl();

    private final EmpleadoDAO empleadoDAO =
            new EmpleadoDAOImpl();

    private final EntrenadorDAO entrenadorDAO =
            new EntrenadorDAOImpl();

    private Entrenador crearEntrenador() {

        Usuario usuario = new Usuario();

        usuario.setUsuario(
                "entrenador_" + System.nanoTime()
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
        empleado.setApellidos("Prueba");
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setEstado(true);
        empleado.setUsuario(usuario);

        empleadoDAO.registrarEmpleado(empleado);

        Entrenador entrenador = new Entrenador();

        entrenador.setEmpleado(empleado);
        entrenador.setEspecialidad("Musculacion");
        entrenador.setCertificacion("Certificacion Test");
        entrenador.setAnhosExperiencia(3);

        entrenadorDAO.registrarEntrenador(entrenador);

        return entrenador;
    }

    @Test
    void testRegistrarEntrenador() {

        Entrenador entrenador = crearEntrenador();

        assertNotNull(entrenador.getIdEntrenador());
    }

    @Test
    void testBuscarEntrenadorPorId() {

        Entrenador entrenador = crearEntrenador();

        Entrenador encontrado =
                entrenadorDAO.obtenerEntrenadorPorId(
                        entrenador.getIdEntrenador()
                );

        assertNotNull(encontrado);
    }

    @Test
    void testActualizarEntrenador() {

        Entrenador entrenador = crearEntrenador();

        entrenador.setEspecialidad("CrossFit");

        entrenadorDAO.actualizarEntrenador(entrenador);

        Entrenador actualizado =
                entrenadorDAO.obtenerEntrenadorPorId(
                        entrenador.getIdEntrenador()
                );

        assertEquals(
                "CrossFit",
                actualizado.getEspecialidad()
        );
    }

    @Test
    void testBuscarPorEspecialidad() {

        crearEntrenador();

        List<Entrenador> entrenadores =
                entrenadorDAO
                    .obtenerEntrenadoresPorEspecialidad(
                            "Musculacion"
                    );

        assertFalse(entrenadores.isEmpty());
    }

    @Test
    void testListarEntrenadores() {

        crearEntrenador();

        assertFalse(
                entrenadorDAO
                    .obtenerTodosLosEntrenadores()
                    .isEmpty()
        );
    }
}