package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import dao.EmpleadoDAOImpl;
import dao.UsuarioDAOImpl;
import interfaces.EmpleadoDAO;
import interfaces.UsuarioDAO;
import model.Empleado;
import model.Usuario;
import model.enums.Rol;

public class EmpleadoDAOTest {

    private final EmpleadoDAO empleadoDAO =
            new EmpleadoDAOImpl();

    private final UsuarioDAO usuarioDAO =
            new UsuarioDAOImpl();

    private Empleado crearEmpleado() {

        Usuario usuario = new Usuario();

        usuario.setUsuario(
                "empleado_" + System.nanoTime()
        );
        usuario.setClave("12345");
        usuario.setRol(Rol.RECEPCIONISTA);
        usuario.setEstado(true);

        usuarioDAO.registrarUsuario(usuario);

        String numero =
                String.valueOf(System.nanoTime());

        numero = numero.substring(
                numero.length() - 8
        );

        Empleado empleado = new Empleado();

        empleado.setDni(numero);
        empleado.setNombres("Empleado");
        empleado.setApellidos("Prueba");
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setEstado(true);
        empleado.setUsuario(usuario);

        empleadoDAO.registrarEmpleado(empleado);

        return empleado;
    }

    @Test
    void testRegistrarEmpleado() {

        Empleado empleado = crearEmpleado();

        assertNotNull(empleado.getIdEmpleado());
    }

    @Test
    void testBuscarPorId() {

        Empleado empleado = crearEmpleado();

        Empleado encontrado =
                empleadoDAO.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado()
                );

        assertNotNull(encontrado);
        assertEquals(
                empleado.getDni(),
                encontrado.getDni()
        );
    }

    @Test
    void testBuscarPorDni() {

        Empleado empleado = crearEmpleado();

        Empleado encontrado =
                empleadoDAO.obtenerEmpleadoPorDni(
                        empleado.getDni()
                );

        assertNotNull(encontrado);
    }

    @Test
    void testActualizarEmpleado() {

        Empleado empleado = crearEmpleado();

        empleado.setNombres("Empleado Actualizado");

        empleadoDAO.actualizarEmpleado(empleado);

        Empleado actualizado =
                empleadoDAO.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado()
                );

        assertEquals(
                "Empleado Actualizado",
                actualizado.getNombres()
        );
    }

    @Test
    void testListarEmpleados() {

        crearEmpleado();

        List<Empleado> empleados =
                empleadoDAO.obtenerTodosLosEmpleados();

        assertFalse(empleados.isEmpty());
    }
}