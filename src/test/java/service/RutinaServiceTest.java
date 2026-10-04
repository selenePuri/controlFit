package service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Cliente;
import model.Empleado;
import model.Entrenador;
import model.Rutina;
import model.RutinaCliente;
import model.Usuario;
import model.enums.Rol;

public class RutinaServiceTest {

    private final RutinaService rutinaService =
            new RutinaService();

    private final UsuarioService usuarioService =
            new UsuarioService();

    private final EmpleadoService empleadoService =
            new EmpleadoService();

    private final EntrenadorService entrenadorService =
            new EntrenadorService();

    private final ClienteService clienteService =
            new ClienteService();


    // RN-RU01 y RN-RU02
    @Test
    void registrarRutinaCorrectamente() {

        Entrenador entrenador =
                crearEntrenadorPrueba();

        Rutina rutina =
                crearRutinaPrueba(entrenador);

        rutinaService.registrarRutina(rutina);

        assertNotNull(
                rutina.getIdRutina());

        assertEquals(
                "Rutina de prueba",
                rutina.getNombre());

        assertNotNull(
                rutina.getFechaCreacion());

        assertTrue(
                Boolean.TRUE.equals(
                        rutina.getEstado()));

        assertEquals(
                entrenador.getIdEntrenador(),
                rutina.getEntrenador()
                        .getIdEntrenador());
    }


    // RN-RU01
    @Test
    void noDebeRegistrarRutinaSinEntrenador() {

        Rutina rutina =
                new Rutina();

        rutina.setNombre(
                "Rutina sin entrenador");

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .registrarRutina(rutina));

        assertEquals(
                "La rutina debe estar asociada a un entrenador.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarRutinaSinNombre() {

        Entrenador entrenador =
                crearEntrenadorPrueba();

        Rutina rutina =
                new Rutina();

        rutina.setEntrenador(
                entrenador);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .registrarRutina(rutina));

        assertEquals(
                "El nombre de la rutina es obligatorio.",
                excepcion.getMessage());
    }


    // RN-RU02
    @Test
    void noDebeRegistrarRutinaConEntrenadorInhabilitado() {

        Entrenador entrenador =
                crearEntrenadorPrueba();

        empleadoService.desactivarEmpleado(
                entrenador.getEmpleado()
                        .getIdEmpleado());

        Rutina rutina =
                crearRutinaPrueba(
                        entrenador);

        assertThrows(
                IllegalStateException.class,
                () -> rutinaService
                        .registrarRutina(rutina));
    }


    @Test
    void obtenerRutinaPorIdCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        Rutina encontrada =
                rutinaService.obtenerRutinaPorId(
                        rutina.getIdRutina());

        assertNotNull(encontrada);

        assertEquals(
                rutina.getIdRutina(),
                encontrada.getIdRutina());
    }


    @Test
    void noDebeObtenerRutinaConIdInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .obtenerRutinaPorId(0));

        assertEquals(
                "El código de la rutina no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeObtenerRutinaInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .obtenerRutinaPorId(
                                        Integer.MAX_VALUE));

        assertEquals(
                "La rutina no existe.",
                excepcion.getMessage());
    }


    @Test
    void obtenerTodasLasRutinasCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        List<Rutina> rutinas =
                rutinaService
                        .obtenerTodasLasRutinas();

        assertNotNull(rutinas);

        assertTrue(
                rutinas.stream()
                        .anyMatch(r ->
                                r.getIdRutina()
                                        .equals(
                                                rutina.getIdRutina())));
    }


    @Test
    void obtenerRutinasPorEntrenadorCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        Integer idEntrenador =
                rutina.getEntrenador()
                        .getIdEntrenador();

        List<Rutina> rutinas =
                rutinaService
                        .obtenerRutinasPorEntrenador(
                                idEntrenador);

        assertNotNull(rutinas);

        assertTrue(
                rutinas.stream()
                        .anyMatch(r ->
                                r.getIdRutina()
                                        .equals(
                                                rutina.getIdRutina())));
    }


    @Test
    void noDebeBuscarRutinasConIdEntrenadorInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .obtenerRutinasPorEntrenador(0));

        assertEquals(
                "El código del entrenador no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeBuscarRutinasDeEntrenadorInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .obtenerRutinasPorEntrenador(
                                        Integer.MAX_VALUE));

        assertEquals(
                "El entrenador no existe.",
                excepcion.getMessage());
    }


    @Test
    void obtenerRutinasActivasCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        List<Rutina> activas =
                rutinaService
                        .obtenerRutinasActivas();

        assertNotNull(activas);

        assertTrue(
                activas.stream()
                        .anyMatch(r ->
                                r.getIdRutina()
                                        .equals(
                                                rutina.getIdRutina())));

        assertTrue(
                activas.stream()
                        .allMatch(r ->
                                Boolean.TRUE.equals(
                                        r.getEstado())));
    }


    @Test
    void desactivarRutinaCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        rutinaService.desactivarRutina(
                rutina.getIdRutina());

        Rutina actualizada =
                rutinaService.obtenerRutinaPorId(
                        rutina.getIdRutina());

        assertFalse(
                Boolean.TRUE.equals(
                        actualizada.getEstado()));
    }


    @Test
    void activarRutinaCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        rutinaService.desactivarRutina(
                rutina.getIdRutina());

        rutinaService.activarRutina(
                rutina.getIdRutina());

        Rutina actualizada =
                rutinaService.obtenerRutinaPorId(
                        rutina.getIdRutina());

        assertTrue(
                Boolean.TRUE.equals(
                        actualizada.getEstado()));
    }


    // RN-RC01 y RN-RC02
    @Test
    void asignarRutinaCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        LocalDate fecha =
                LocalDate.now();

        rutinaService.asignarRutina(
                rutina.getIdRutina(),
                cliente.getIdCliente(),
                fecha,
                fecha.plusDays(30));

        List<RutinaCliente> asignaciones =
                rutinaService
                        .obtenerRutinasPorCliente(
                                cliente.getIdCliente());

        assertNotNull(asignaciones);

        assertFalse(
                asignaciones.isEmpty());

        RutinaCliente asignacion =
                asignaciones.stream()
                        .filter(rc ->
                                rc.getRutina()
                                        .getIdRutina()
                                        .equals(
                                                rutina.getIdRutina()))
                        .findFirst()
                        .orElse(null);

        assertNotNull(asignacion);

        assertEquals(
                fecha,
                asignacion.getFechaAsignacion());

        assertEquals(
                fecha.plusDays(30),
                asignacion.getFechaFin());

        assertTrue(
                Boolean.TRUE.equals(
                        asignacion.getEstado()));
    }


    // RN-RC01
    @Test
    void noDebeAsignarRutinaClienteInactivo() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        clienteService.desactivarCliente(
                cliente.getIdCliente());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> rutinaService
                                .asignarRutina(
                                        rutina.getIdRutina(),
                                        cliente.getIdCliente(),
                                        LocalDate.now(),
                                        null));

        assertEquals(
                "El cliente se encuentra inactivo y no puede recibir nuevas rutinas.",
                excepcion.getMessage());
    }


    @Test
    void noDebeAsignarRutinaAClienteInexistente() {

        Rutina rutina =
                crearRutinaRegistrada();

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .asignarRutina(
                                        rutina.getIdRutina(),
                                        Integer.MAX_VALUE,
                                        LocalDate.now(),
                                        null));

        assertEquals(
                "El cliente no existe.",
                excepcion.getMessage());
    }


    // RN-RC02 y RN-RU03
    @Test
    void noDebeAsignarRutinaInactiva() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        rutinaService.desactivarRutina(
                rutina.getIdRutina());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> rutinaService
                                .asignarRutina(
                                        rutina.getIdRutina(),
                                        cliente.getIdCliente(),
                                        LocalDate.now(),
                                        null));

        assertEquals(
                "La rutina se encuentra inactiva y no puede ser asignada.",
                excepcion.getMessage());
    }


    @Test
    void noDebeAsignarRutinaInexistente() {

        Cliente cliente =
                crearClientePrueba();

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .asignarRutina(
                                        Integer.MAX_VALUE,
                                        cliente.getIdCliente(),
                                        LocalDate.now(),
                                        null));

        assertEquals(
                "La rutina no existe.",
                excepcion.getMessage());
    }


    // RN-RC03
    @Test
    void noDebeAsignarConFechaFinAnterior() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        LocalDate inicio =
                LocalDate.now();

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .asignarRutina(
                                        rutina.getIdRutina(),
                                        cliente.getIdCliente(),
                                        inicio,
                                        inicio.minusDays(1)));

        assertEquals(
                "La fecha de fin no puede ser anterior a la fecha de asignación.",
                excepcion.getMessage());
    }


    // RN-RC04
    @Test
    void noDebeDuplicarAsignacionActiva() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        LocalDate fecha =
                LocalDate.now();

        rutinaService.asignarRutina(
                rutina.getIdRutina(),
                cliente.getIdCliente(),
                fecha,
                null);

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> rutinaService
                                .asignarRutina(
                                        rutina.getIdRutina(),
                                        cliente.getIdCliente(),
                                        fecha,
                                        null));

        assertEquals(
                "El cliente ya tiene esta rutina asignada actualmente.",
                excepcion.getMessage());
    }


    @Test
    void obtenerAsignacionPorIdCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        rutinaService.asignarRutina(
                rutina.getIdRutina(),
                cliente.getIdCliente(),
                LocalDate.now(),
                null);

        RutinaCliente asignacion =
                rutinaService
                        .obtenerRutinasPorCliente(
                                cliente.getIdCliente())
                        .stream()
                        .filter(rc ->
                                rc.getRutina()
                                        .getIdRutina()
                                        .equals(
                                                rutina.getIdRutina()))
                        .findFirst()
                        .orElseThrow();

        RutinaCliente encontrada =
                rutinaService
                        .obtenerAsignacionPorId(
                                asignacion.getIdRutinaCliente());

        assertNotNull(encontrada);

        assertEquals(
                asignacion.getIdRutinaCliente(),
                encontrada.getIdRutinaCliente());
    }


    @Test
    void noDebeObtenerAsignacionConIdInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .obtenerAsignacionPorId(0));

        assertEquals(
                "El código de la asignación no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeObtenerAsignacionInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .obtenerAsignacionPorId(
                                        Integer.MAX_VALUE));

        assertEquals(
                "La asignación de rutina no existe.",
                excepcion.getMessage());
    }


    @Test
    void obtenerTodasLasAsignacionesCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        rutinaService.asignarRutina(
                rutina.getIdRutina(),
                cliente.getIdCliente(),
                LocalDate.now(),
                null);

        List<RutinaCliente> asignaciones =
                rutinaService
                        .obtenerTodasLasAsignaciones();

        assertNotNull(asignaciones);

        assertTrue(
                asignaciones.stream()
                        .anyMatch(rc ->
                                rc.getRutina()
                                        .getIdRutina()
                                        .equals(
                                                rutina.getIdRutina())
                                && rc.getCliente()
                                        .getIdCliente()
                                        .equals(
                                                cliente.getIdCliente())));
    }


    @Test
    void obtenerRutinasPorClienteCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        rutinaService.asignarRutina(
                rutina.getIdRutina(),
                cliente.getIdCliente(),
                LocalDate.now(),
                null);

        List<RutinaCliente> asignaciones =
                rutinaService
                        .obtenerRutinasPorCliente(
                                cliente.getIdCliente());

        assertNotNull(asignaciones);

        assertTrue(
                asignaciones.stream()
                        .anyMatch(rc ->
                                rc.getRutina()
                                        .getIdRutina()
                                        .equals(
                                                rutina.getIdRutina())));
    }


    @Test
    void noDebeBuscarRutinasConIdClienteInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .obtenerRutinasPorCliente(0));

        assertEquals(
                "El código del cliente no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeBuscarRutinasDeClienteInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .obtenerRutinasPorCliente(
                                        Integer.MAX_VALUE));

        assertEquals(
                "El cliente no existe.",
                excepcion.getMessage());
    }


    @Test
    void obtenerClientesPorRutinaCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        rutinaService.asignarRutina(
                rutina.getIdRutina(),
                cliente.getIdCliente(),
                LocalDate.now(),
                null);

        List<RutinaCliente> asignaciones =
                rutinaService
                        .obtenerClientesPorRutina(
                                rutina.getIdRutina());

        assertNotNull(asignaciones);

        assertTrue(
                asignaciones.stream()
                        .anyMatch(rc ->
                                rc.getCliente()
                                        .getIdCliente()
                                        .equals(
                                                cliente.getIdCliente())));
    }


    @Test
    void noDebeBuscarClientesConIdRutinaInvalido() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .obtenerClientesPorRutina(0));

        assertEquals(
                "El código de la rutina no es válido.",
                excepcion.getMessage());
    }


    @Test
    void noDebeBuscarClientesDeRutinaInexistente() {

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .obtenerClientesPorRutina(
                                        Integer.MAX_VALUE));

        assertEquals(
                "La rutina no existe.",
                excepcion.getMessage());
    }


    @Test
    void finalizarAsignacionCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        LocalDate inicio =
                LocalDate.now();

        rutinaService.asignarRutina(
                rutina.getIdRutina(),
                cliente.getIdCliente(),
                inicio,
                null);

        RutinaCliente asignacion =
                rutinaService
                        .obtenerRutinasPorCliente(
                                cliente.getIdCliente())
                        .stream()
                        .filter(rc ->
                                rc.getRutina()
                                        .getIdRutina()
                                        .equals(
                                                rutina.getIdRutina()))
                        .findFirst()
                        .orElseThrow();

        LocalDate fechaFin =
                inicio.plusDays(15);

        rutinaService.finalizarAsignacion(
                asignacion.getIdRutinaCliente(),
                fechaFin);

        RutinaCliente finalizada =
                rutinaService.obtenerAsignacionPorId(
                        asignacion.getIdRutinaCliente());

        assertFalse(
                Boolean.TRUE.equals(
                        finalizada.getEstado()));

        assertEquals(
                fechaFin,
                finalizada.getFechaFin());
    }


    @Test
    void noDebeFinalizarDosVecesLaMismaAsignacion() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        LocalDate inicio =
                LocalDate.now();

        rutinaService.asignarRutina(
                rutina.getIdRutina(),
                cliente.getIdCliente(),
                inicio,
                null);

        RutinaCliente asignacion =
                rutinaService
                        .obtenerRutinasPorCliente(
                                cliente.getIdCliente())
                        .stream()
                        .filter(rc ->
                                rc.getRutina()
                                        .getIdRutina()
                                        .equals(
                                                rutina.getIdRutina()))
                        .findFirst()
                        .orElseThrow();

        rutinaService.finalizarAsignacion(
                asignacion.getIdRutinaCliente(),
                inicio.plusDays(10));

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> rutinaService
                                .finalizarAsignacion(
                                        asignacion
                                                .getIdRutinaCliente(),
                                        inicio.plusDays(15)));

        assertEquals(
                "La asignación ya se encuentra inactiva.",
                excepcion.getMessage());
    }


    // RN-RC03
    @Test
    void noDebeFinalizarConFechaAnteriorALaAsignacion() {

        Rutina rutina =
                crearRutinaRegistrada();

        Cliente cliente =
                crearClientePrueba();

        LocalDate inicio =
                LocalDate.now();

        rutinaService.asignarRutina(
                rutina.getIdRutina(),
                cliente.getIdCliente(),
                inicio,
                null);

        RutinaCliente asignacion =
                rutinaService
                        .obtenerRutinasPorCliente(
                                cliente.getIdCliente())
                        .stream()
                        .filter(rc ->
                                rc.getRutina()
                                        .getIdRutina()
                                        .equals(
                                                rutina.getIdRutina()))
                        .findFirst()
                        .orElseThrow();

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rutinaService
                                .finalizarAsignacion(
                                        asignacion
                                                .getIdRutinaCliente(),
                                        inicio.minusDays(1)));

        assertEquals(
                "La fecha de fin no puede ser anterior a la fecha de asignación.",
                excepcion.getMessage());
    }


    @Test
    void actualizarRutinaCorrectamente() {

        Rutina rutina =
                crearRutinaRegistrada();

        rutina.setNombre(
                "Rutina actualizada");

        rutina.setObjetivo(
                "Mejorar resistencia");

        rutinaService.actualizarRutina(
                rutina);

        Rutina actualizada =
                rutinaService.obtenerRutinaPorId(
                        rutina.getIdRutina());

        assertEquals(
                "Rutina actualizada",
                actualizada.getNombre());

        assertEquals(
                "Mejorar resistencia",
                actualizada.getObjetivo());
    }


    private Rutina crearRutinaRegistrada() {

        Entrenador entrenador =
                crearEntrenadorPrueba();

        Rutina rutina =
                crearRutinaPrueba(
                        entrenador);

        rutinaService.registrarRutina(
                rutina);

        return rutina;
    }


    private Rutina crearRutinaPrueba(
            Entrenador entrenador) {

        Rutina rutina =
                new Rutina();

        rutina.setEntrenador(
                entrenador);

        rutina.setNombre(
                "Rutina de prueba");

        rutina.setDescripcion(
                "Rutina creada para pruebas");

        rutina.setObjetivo(
                "Acondicionamiento físico");

        rutina.setEstado(true);

        return rutina;
    }


    private Entrenador crearEntrenadorPrueba() {

        Usuario usuario =
                crearUsuarioEntrenador();

        Empleado empleado =
                new Empleado();

        empleado.setDni(
                generarDni());

        empleado.setNombres(
                "Entrenador");

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

        Entrenador entrenador =
                new Entrenador();

        entrenador.setEmpleado(
                empleado);

        entrenador.setEspecialidad(
                "Entrenamiento funcional");

        entrenador.setCertificacion(
                "Certificación de prueba");

        entrenador.setAnhosExperiencia(2);

        entrenadorService.registrarEntrenador(
                entrenador);

        return entrenador;
    }


    private Usuario crearUsuarioEntrenador() {

        Usuario usuario =
                new Usuario();

        usuario.setUsuario(
                generarNombreUsuario());

        usuario.setClave(
                "Clave123");

        usuario.setRol(
                Rol.ENTRENADOR);

        usuario.setEstado(true);

        usuarioService.registrarUsuario(
                usuario);

        return usuario;
    }


    private Cliente crearClientePrueba() {

        Cliente cliente =
                new Cliente();

        cliente.setDni(
                generarDni());

        cliente.setNombres(
                "Cliente Rutina");

        cliente.setApellidos(
                "Prueba Service");

        cliente.setTelefono(
                "987654321");

        cliente.setEstado(true);

        clienteService.registrarCliente(
                cliente);

        return cliente;
    }


    private String generarNombreUsuario() {

        return "ent"
                + Math.abs(
                        System.nanoTime());
    }


    private String generarDni() {

        long numero =
                Math.abs(
                        System.nanoTime())
                        % 90_000_000L
                        + 10_000_000L;

        return String.valueOf(
                numero);
    }
}