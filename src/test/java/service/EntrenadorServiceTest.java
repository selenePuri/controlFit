package service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Empleado;
import model.Entrenador;
import model.Usuario;
import model.enums.Rol;

public class EntrenadorServiceTest {

    private final EntrenadorService entrenadorService =
            new EntrenadorService();

    private final EmpleadoService empleadoService =
            new EmpleadoService();

    private final UsuarioService usuarioService =
            new UsuarioService();


    // RN-EN01 y RN-EN02
    @Test
    void registrarEntrenadorCorrectamente() {

        Empleado empleado =
                crearEmpleadoPrueba(
                        Rol.ENTRENADOR);

        Entrenador entrenador =
                crearEntrenadorPrueba(
                        empleado);

        entrenadorService.registrarEntrenador(
                entrenador);

        assertNotNull(
                entrenador.getIdEntrenador());

        assertNotNull(
                entrenador.getEmpleado());

        assertEquals(
                empleado.getIdEmpleado(),
                entrenador.getEmpleado()
                        .getIdEmpleado());

        assertEquals(
                Rol.ENTRENADOR,
                entrenador.getEmpleado()
                        .getUsuario()
                        .getRol());
    }


    // RN-EN01
    @Test
    void noDebeRegistrarEntrenadorSinEmpleado() {

        Entrenador entrenador =
                new Entrenador();

        entrenador.setEspecialidad(
                "Musculación");

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> entrenadorService
                                .registrarEntrenador(
                                        entrenador));

        assertEquals(
                "El entrenador debe estar asociado a un empleado registrado.",
                excepcion.getMessage());
    }


    // RN-EN01
    @Test
    void noDebeRegistrarEntrenadorConEmpleadoInexistente() {

        Empleado empleado =
                new Empleado();

        empleado.setIdEmpleado(
                Integer.MAX_VALUE);

        Entrenador entrenador =
                new Entrenador();

        entrenador.setEmpleado(
                empleado);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> entrenadorService
                                .registrarEntrenador(
                                        entrenador));

        assertEquals(
                "El empleado no existe.",
                excepcion.getMessage());
    }


    // RN-EN02
    @Test
    void noDebeRegistrarEntrenadorConRolRecepcionista() {

        Empleado empleado =
                crearEmpleadoPrueba(
                        Rol.RECEPCIONISTA);

        Entrenador entrenador =
                crearEntrenadorPrueba(
                        empleado);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> entrenadorService
                                .registrarEntrenador(
                                        entrenador));

        assertEquals(
                "El usuario asociado debe tener rol ENTRENADOR.",
                excepcion.getMessage());
    }


    // RN-EN02
    @Test
    void noDebeRegistrarEntrenadorConRolAdministrador() {

        Empleado empleado =
                crearEmpleadoPrueba(
                        Rol.ADMINISTRADOR);

        Entrenador entrenador =
                crearEntrenadorPrueba(
                        empleado);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> entrenadorService
                                .registrarEntrenador(
                                        entrenador));

        assertEquals(
                "El usuario asociado debe tener rol ENTRENADOR.",
                excepcion.getMessage());
    }


    @Test
    void obtenerEntrenadorPorIdCorrectamente() {

        Entrenador entrenador =
                crearEntrenadorRegistrado();

        Entrenador encontrado =
                entrenadorService
                        .obtenerEntrenadorPorId(
                                entrenador.getIdEntrenador());

        assertNotNull(encontrado);

        assertEquals(
                entrenador.getIdEntrenador(),
                encontrado.getIdEntrenador());
    }


    @Test
    void noDebeObtenerEntrenadorConIdInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> entrenadorService
                                .obtenerEntrenadorPorId(0));

        assertEquals(
                "El código del entrenador no es válido.",
                excepcion.getMessage());
    }


    @Test
    void obtenerTodosLosEntrenadoresCorrectamente() {

        Entrenador entrenador =
                crearEntrenadorRegistrado();

        List<Entrenador> entrenadores =
                entrenadorService
                        .obtenerTodosLosEntrenadores();

        assertNotNull(entrenadores);

        assertTrue(
                entrenadores.stream()
                        .anyMatch(e ->
                                e.getIdEntrenador()
                                        .equals(
                                                entrenador
                                                        .getIdEntrenador())));
    }


    @Test
    void obtenerEntrenadoresPorEspecialidadCorrectamente() {

        Empleado empleado =
                crearEmpleadoPrueba(
                        Rol.ENTRENADOR);

        String especialidad =
                "Funcional "
                        + System.nanoTime();

        Entrenador entrenador =
                crearEntrenadorPrueba(
                        empleado);

        entrenador.setEspecialidad(
                especialidad);

        entrenadorService.registrarEntrenador(
                entrenador);

        List<Entrenador> entrenadores =
                entrenadorService
                        .obtenerPorEspecialidad(
                                especialidad);

        assertNotNull(entrenadores);

        assertTrue(
                entrenadores.stream()
                        .anyMatch(e ->
                                e.getIdEntrenador()
                                        .equals(
                                                entrenador
                                                        .getIdEntrenador())));
    }


    @Test
    void noDebeBuscarEspecialidadVacia() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> entrenadorService
                                .obtenerPorEspecialidad(""));

        assertEquals(
                "La especialidad es obligatoria.",
                excepcion.getMessage());
    }


    @Test
    void actualizarEntrenadorCorrectamente() {

        Entrenador entrenador =
                crearEntrenadorRegistrado();

        entrenador.setEspecialidad(
                "Entrenamiento funcional");

        entrenador.setCertificacion(
                "Certificación actualizada");

        entrenador.setAnhosExperiencia(5);

        entrenadorService.actualizarEntrenador(
                entrenador);

        Entrenador actualizado =
                entrenadorService
                        .obtenerEntrenadorPorId(
                                entrenador.getIdEntrenador());

        assertEquals(
                "Entrenamiento funcional",
                actualizado.getEspecialidad());

        assertEquals(
                "Certificación actualizada",
                actualizado.getCertificacion());

        assertEquals(
                5,
                actualizado.getAnhosExperiencia());
    }


    // RN-EN03
    @Test
    void obtenerEntrenadorHabilitadoCorrectamente() {

        Entrenador entrenador =
                crearEntrenadorRegistrado();

        Entrenador habilitado =
                entrenadorService
                        .obtenerEntrenadorHabilitado(
                                entrenador.getIdEntrenador());

        assertNotNull(habilitado);

        assertEquals(
                entrenador.getIdEntrenador(),
                habilitado.getIdEntrenador());

        assertTrue(
                Boolean.TRUE.equals(
                        habilitado.getEmpleado()
                                .getEstado()));

        assertTrue(
                Boolean.TRUE.equals(
                        habilitado.getEmpleado()
                                .getUsuario()
                                .getEstado()));

        assertEquals(
                Rol.ENTRENADOR,
                habilitado.getEmpleado()
                        .getUsuario()
                        .getRol());
    }


    // RN-EN03
    @Test
    void noDebeHabilitarEntrenadorConEmpleadoInactivo() {

        Entrenador entrenador =
                crearEntrenadorRegistrado();

        empleadoService.desactivarEmpleado(
                entrenador.getEmpleado()
                        .getIdEmpleado());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> entrenadorService
                                .obtenerEntrenadorHabilitado(
                                        entrenador
                                                .getIdEntrenador()));

        assertEquals(
                "El empleado se encuentra inactivo.",
                excepcion.getMessage());
    }


    // RN-EN03
    @Test
    void noDebeHabilitarEntrenadorConCuentaInactiva() {

        Entrenador entrenador =
                crearEntrenadorRegistrado();

        usuarioService.desactivarUsuario(
                entrenador.getEmpleado()
                        .getUsuario()
                        .getIdUsuario());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> entrenadorService
                                .obtenerEntrenadorHabilitado(
                                        entrenador
                                                .getIdEntrenador()));

        assertEquals(
                "La cuenta del entrenador se encuentra inactiva.",
                excepcion.getMessage());
    }


    @Test
    void obtenerEntrenadoresActivosCorrectamente() {

        Entrenador entrenador =
                crearEntrenadorRegistrado();

        List<Entrenador> entrenadores =
                entrenadorService
                        .obtenerEntrenadoresActivos();

        assertNotNull(entrenadores);

        assertTrue(
                entrenadores.stream()
                        .anyMatch(e ->
                                e.getIdEntrenador()
                                        .equals(
                                                entrenador
                                                        .getIdEntrenador())));
    }


    private Entrenador crearEntrenadorRegistrado() {

        Empleado empleado =
                crearEmpleadoPrueba(
                        Rol.ENTRENADOR);

        Entrenador entrenador =
                crearEntrenadorPrueba(
                        empleado);

        entrenadorService.registrarEntrenador(
                entrenador);

        return entrenador;
    }


    private Entrenador crearEntrenadorPrueba(
            Empleado empleado) {

        Entrenador entrenador =
                new Entrenador();

        entrenador.setEmpleado(
                empleado);

        entrenador.setEspecialidad(
                "Musculación");

        entrenador.setCertificacion(
                "Certificación de prueba");

        entrenador.setAnhosExperiencia(3);

        return entrenador;
    }


    private Empleado crearEmpleadoPrueba(
            Rol rol) {

        Usuario usuario =
                crearUsuarioPrueba(
                        rol);

        Empleado empleado =
                new Empleado();

        empleado.setDni(
                generarDni());

        empleado.setNombres(
                "Empleado");

        empleado.setApellidos(
                "Prueba");

        empleado.setTelefono(
                "987654321");

        empleado.setFechaContratacion(
                LocalDate.now());

        empleado.setEstado(true);

        empleado.setUsuario(
                usuario);

        empleadoService.registrarEmpleado(
                empleado);

        return empleado;
    }


    private Usuario crearUsuarioPrueba(
            Rol rol) {

        Usuario usuario =
                new Usuario();

        usuario.setUsuario(
                "usuario"
                        + Math.abs(
                                System.nanoTime()));

        usuario.setClave(
                "Clave123");

        usuario.setRol(
                rol);

        usuario.setEstado(true);

        usuarioService.registrarUsuario(
                usuario);

        return usuario;
    }


    private String generarDni() {

        long numero =
                Math.abs(
                        System.nanoTime())
                        % 90_000_000L
                        + 10_000_000L;

        return String.valueOf(numero);
    }
}