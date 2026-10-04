package service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Empleado;
import model.Usuario;
import model.enums.Rol;

public class EmpleadoServiceTest {

    private final EmpleadoService empleadoService =
            new EmpleadoService();

    private final UsuarioService usuarioService =
            new UsuarioService();


    // RN-EM01, RN-EM02 y RN-EM03
    @Test
    void registrarEmpleadoCorrectamente() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        assertNotNull(empleado.getIdEmpleado());

        Empleado guardado =
                empleadoService.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado());

        assertNotNull(guardado);

        assertEquals(
                empleado.getDni(),
                guardado.getDni());

        assertEquals(
                usuario.getIdUsuario(),
                guardado.getUsuario().getIdUsuario());

        assertEquals(
                Rol.RECEPCIONISTA,
                guardado.getUsuario().getRol());

        assertTrue(guardado.getEstado());
    }


    // RN-EM02
    @Test
    void noDebeRegistrarEmpleadoConDniDuplicado() {

        String dni = generarDni();

        Usuario usuario1 =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado1 =
                crearEmpleadoPrueba(usuario1);

        empleado1.setDni(dni);

        empleadoService.registrarEmpleado(empleado1);


        Usuario usuario2 =
                crearUsuarioPrueba(Rol.ADMINISTRADOR);

        Empleado empleado2 =
                crearEmpleadoPrueba(usuario2);

        empleado2.setDni(dni);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> empleadoService
                                .registrarEmpleado(empleado2));

        assertEquals(
                "Ya existe un empleado registrado con ese DNI.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarEmpleadoConDniInvalido() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleado.setDni("1234");

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> empleadoService
                                .registrarEmpleado(empleado));

        assertEquals(
                "El DNI debe contener exactamente 8 números.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarEmpleadoSinDni() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleado.setDni("");

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> empleadoService
                                .registrarEmpleado(empleado));

        assertEquals(
                "El DNI del empleado es obligatorio.",
                excepcion.getMessage());
    }


    // RN-EM01
    @Test
    void noDebeRegistrarEmpleadoSinUsuario() {

        Empleado empleado =
                new Empleado();

        empleado.setDni(
                generarDni());

        empleado.setNombres(
                "Empleado");

        empleado.setApellidos(
                "Sin Usuario");

        empleado.setFechaContratacion(
                LocalDate.now());

        empleado.setEstado(true);

        empleado.setUsuario(null);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> empleadoService
                                .registrarEmpleado(
                                        empleado));

        assertEquals(
                "El empleado debe estar asociado a un usuario registrado.",
                excepcion.getMessage());
    }

    // RN-EM03
    @Test
    void noDebeRegistrarEmpleadoConRolCliente() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.CLIENTE);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> empleadoService
                                .registrarEmpleado(empleado));

        assertEquals(
                "El usuario asociado no tiene un rol válido para un empleado.",
                excepcion.getMessage());
    }


    // RN-EM03
    @Test
    void debePermitirEmpleadoAdministrador() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.ADMINISTRADOR);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        Empleado guardado =
                empleadoService.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado());

        assertNotNull(guardado);

        assertEquals(
                Rol.ADMINISTRADOR,
                guardado.getUsuario().getRol());
    }


    // RN-EM03
    @Test
    void debePermitirEmpleadoRecepcionista() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        Empleado guardado =
                empleadoService.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado());

        assertEquals(
                Rol.RECEPCIONISTA,
                guardado.getUsuario().getRol());
    }


    // RN-EM03
    @Test
    void debePermitirEmpleadoEntrenador() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.ENTRENADOR);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        Empleado guardado =
                empleadoService.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado());

        assertEquals(
                Rol.ENTRENADOR,
                guardado.getUsuario().getRol());
    }


    @Test
    void obtenerEmpleadoPorIdCorrectamente() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        Empleado encontrado =
                empleadoService.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado());

        assertNotNull(encontrado);

        assertEquals(
                empleado.getIdEmpleado(),
                encontrado.getIdEmpleado());

        assertEquals(
                empleado.getDni(),
                encontrado.getDni());
    }


    @Test
    void obtenerEmpleadoPorDniCorrectamente() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        Empleado encontrado =
                empleadoService.obtenerEmpleadoPorDni(
                        empleado.getDni());

        assertNotNull(encontrado);

        assertEquals(
                empleado.getIdEmpleado(),
                encontrado.getIdEmpleado());
    }


    @Test
    void obtenerTodosLosEmpleadosCorrectamente() {

        List<Empleado> empleados =
                empleadoService.obtenerTodosLosEmpleados();

        assertNotNull(empleados);
    }


    // RN-EM04
    @Test
    void desactivarEmpleadoCorrectamente() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        empleadoService.desactivarEmpleado(
                empleado.getIdEmpleado());

        Empleado desactivado =
                empleadoService.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado());

        assertFalse(desactivado.getEstado());
    }


    @Test
    void activarEmpleadoCorrectamente() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        empleadoService.desactivarEmpleado(
                empleado.getIdEmpleado());

        empleadoService.activarEmpleado(
                empleado.getIdEmpleado());

        Empleado activado =
                empleadoService.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado());

        assertTrue(activado.getEstado());
    }


    // RN-EM04
    @Test
    void obtenerEmpleadoActivoCorrectamente() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        Empleado activo =
                empleadoService.obtenerEmpleadoActivo(
                        empleado.getIdEmpleado());

        assertNotNull(activo);
        assertTrue(activo.getEstado());
    }


    // RN-EM04
    @Test
    void noDebeObtenerComoActivoUnEmpleadoInactivo() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        empleadoService.desactivarEmpleado(
                empleado.getIdEmpleado());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> empleadoService
                                .obtenerEmpleadoActivo(
                                        empleado.getIdEmpleado()));

        assertEquals(
                "El empleado se encuentra inactivo.",
                excepcion.getMessage());
    }


    @Test
    void actualizarEmpleadoCorrectamente() {

        Usuario usuario =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado =
                crearEmpleadoPrueba(usuario);

        empleadoService.registrarEmpleado(empleado);

        empleado.setNombres("Nombre Actualizado");
        empleado.setApellidos("Apellido Actualizado");
        empleado.setTelefono("912345678");

        empleadoService.actualizarEmpleado(empleado);

        Empleado actualizado =
                empleadoService.obtenerEmpleadoPorId(
                        empleado.getIdEmpleado());

        assertEquals(
                "Nombre Actualizado",
                actualizado.getNombres());

        assertEquals(
                "Apellido Actualizado",
                actualizado.getApellidos());

        assertEquals(
                "912345678",
                actualizado.getTelefono());
    }


    // RN-EM02
    @Test
    void noDebeActualizarEmpleadoConDniDeOtroEmpleado() {

        Usuario usuario1 =
                crearUsuarioPrueba(Rol.RECEPCIONISTA);

        Empleado empleado1 =
                crearEmpleadoPrueba(usuario1);

        empleadoService.registrarEmpleado(empleado1);


        Usuario usuario2 =
                crearUsuarioPrueba(Rol.ADMINISTRADOR);

        Empleado empleado2 =
                crearEmpleadoPrueba(usuario2);

        empleadoService.registrarEmpleado(empleado2);


        empleado2.setDni(empleado1.getDni());

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> empleadoService
                                .actualizarEmpleado(empleado2));

        assertEquals(
                "El DNI pertenece a otro empleado.",
                excepcion.getMessage());
    }


    private Usuario crearUsuarioPrueba(Rol rol) {

        Usuario usuario = new Usuario();

        usuario.setUsuario(
                generarNombreUsuario());

        usuario.setClave("Clave123");
        usuario.setRol(rol);
        usuario.setEstado(true);

        usuarioService.registrarUsuario(usuario);

        return usuario;
    }


    private Empleado crearEmpleadoPrueba(
            Usuario usuario) {

        Empleado empleado = new Empleado();

        empleado.setDni(generarDni());
        empleado.setNombres("Empleado Prueba");
        empleado.setApellidos("Apellido Prueba");
        empleado.setTelefono("987654321");
        empleado.setFechaContratacion(
                LocalDate.now());

        empleado.setEstado(true);
        empleado.setUsuario(usuario);

        return empleado;
    }


    private String generarNombreUsuario() {

        return "emp_" + System.nanoTime();
    }


    private String generarDni() {

        long numero =
                Math.abs(System.nanoTime())
                        % 90_000_000L
                        + 10_000_000L;

        return String.valueOf(numero);
    }
    
    // RN-EM01
    @Test
    void noDebeRegistrarEmpleadoConUsuarioInexistente() {

        Usuario usuario = new Usuario();

        usuario.setIdUsuario(
                Integer.MAX_VALUE);

        usuario.setRol(
                Rol.RECEPCIONISTA);

        Empleado empleado =
                new Empleado();

        empleado.setDni(
                generarDni());

        empleado.setNombres(
                "Empleado");

        empleado.setApellidos(
                "Usuario Inexistente");

        empleado.setFechaContratacion(
                LocalDate.now());

        empleado.setEstado(true);

        empleado.setUsuario(
                usuario);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> empleadoService
                                .registrarEmpleado(
                                        empleado));

        assertEquals(
                "El usuario asociado al empleado no existe.",
                excepcion.getMessage());
    }
}