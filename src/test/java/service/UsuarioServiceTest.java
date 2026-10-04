package service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import interfaces.UsuarioDAO;
import dao.UsuarioDAOImpl;
import model.Usuario;
import model.enums.Rol;
import util.PasswordUtil;

public class UsuarioServiceTest {

    private final UsuarioService usuarioService =
            new UsuarioService();

    private final UsuarioDAO usuarioDAO =
            new UsuarioDAOImpl();


    // RN-US01, RN-US04 y RN-US07
    @Test
    void registrarUsuarioCorrectamente() {

        String nombreUsuario =
                "test_usuario_" + System.currentTimeMillis();

        Usuario usuario = new Usuario();

        usuario.setUsuario(nombreUsuario);
        usuario.setClave("Clave123");
        usuario.setRol(Rol.CLIENTE);
        usuario.setEstado(true);

        usuarioService.registrarUsuario(usuario);

        assertNotNull(usuario.getIdUsuario());

        Usuario guardado =
                usuarioDAO.obtenerUsuarioPorNombre(
                        nombreUsuario);

        assertNotNull(guardado);
        assertEquals(nombreUsuario, guardado.getUsuario());
        assertEquals(Rol.CLIENTE, guardado.getRol());
        assertTrue(guardado.getEstado());

        // RN-US07
        assertNotEquals(
                "Clave123",
                guardado.getClave());

        assertTrue(
                PasswordUtil.verificar(
                        "Clave123",
                        guardado.getClave()));
    }


    // RN-US01
    @Test
    void noDebeRegistrarUsuarioDuplicado() {

        String nombreUsuario =
                "test_duplicado_" + System.currentTimeMillis();

        Usuario usuario1 = new Usuario();

        usuario1.setUsuario(nombreUsuario);
        usuario1.setClave("Clave123");
        usuario1.setRol(Rol.CLIENTE);
        usuario1.setEstado(true);

        usuarioService.registrarUsuario(usuario1);

        Usuario usuario2 = new Usuario();

        usuario2.setUsuario(nombreUsuario);
        usuario2.setClave("OtraClave456");
        usuario2.setRol(Rol.CLIENTE);
        usuario2.setEstado(true);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> usuarioService
                                .registrarUsuario(usuario2));

        assertEquals(
                "El nombre de usuario ya se encuentra registrado.",
                excepcion.getMessage());
    }


    // RN-US04
    @Test
    void noDebeRegistrarUsuarioSinRol() {

        Usuario usuario = new Usuario();

        usuario.setUsuario(
                "test_sin_rol_" + System.currentTimeMillis());

        usuario.setClave("Clave123");
        usuario.setRol(null);
        usuario.setEstado(true);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> usuarioService
                                .registrarUsuario(usuario));

        assertEquals(
                "El usuario debe tener un rol.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarUsuarioSinNombre() {

        Usuario usuario = new Usuario();

        usuario.setUsuario("");
        usuario.setClave("Clave123");
        usuario.setRol(Rol.CLIENTE);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> usuarioService
                                .registrarUsuario(usuario));

        assertEquals(
                "El nombre de usuario es obligatorio.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarUsuarioSinClave() {

        Usuario usuario = new Usuario();

        usuario.setUsuario(
                "test_sin_clave_" + System.currentTimeMillis());

        usuario.setClave("");
        usuario.setRol(Rol.CLIENTE);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> usuarioService
                                .registrarUsuario(usuario));

        assertEquals(
                "La contraseña es obligatoria.",
                excepcion.getMessage());
    }


    // RN-US02, RN-US03 y RN-US07
    @Test
    void iniciarSesionCorrectamente() {

        String nombreUsuario =
                "test_login_" + System.currentTimeMillis();

        Usuario usuario = new Usuario();

        usuario.setUsuario(nombreUsuario);
        usuario.setClave("Login123");
        usuario.setRol(Rol.CLIENTE);
        usuario.setEstado(true);

        usuarioService.registrarUsuario(usuario);

        Usuario autenticado =
                usuarioService.iniciarSesion(
                        nombreUsuario,
                        "Login123");

        assertNotNull(autenticado);

        assertEquals(
                nombreUsuario,
                autenticado.getUsuario());

        assertEquals(
                Rol.CLIENTE,
                autenticado.getRol());
    }


    // RN-US03
    @Test
    void noDebeIniciarSesionConClaveIncorrecta() {

        String nombreUsuario =
                "test_clave_incorrecta_"
                        + System.currentTimeMillis();

        Usuario usuario = new Usuario();

        usuario.setUsuario(nombreUsuario);
        usuario.setClave("ClaveCorrecta123");
        usuario.setRol(Rol.CLIENTE);
        usuario.setEstado(true);

        usuarioService.registrarUsuario(usuario);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> usuarioService.iniciarSesion(
                                nombreUsuario,
                                "ClaveIncorrecta"));

        assertEquals(
                "Usuario o contraseña incorrectos.",
                excepcion.getMessage());
    }


    // RN-US03
    @Test
    void noDebeIniciarSesionConUsuarioInexistente() {

        String nombreUsuario =
                "usuario_inexistente_"
                        + System.currentTimeMillis();

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> usuarioService.iniciarSesion(
                                nombreUsuario,
                                "Clave123"));

        assertEquals(
                "Usuario o contraseña incorrectos.",
                excepcion.getMessage());
    }


    // RN-US02 y RN-US06
    @Test
    void usuarioDesactivadoNoDebeIniciarSesion() {

        String nombreUsuario =
                "test_inactivo_" + System.currentTimeMillis();

        Usuario usuario = new Usuario();

        usuario.setUsuario(nombreUsuario);
        usuario.setClave("Clave123");
        usuario.setRol(Rol.CLIENTE);
        usuario.setEstado(true);

        usuarioService.registrarUsuario(usuario);

        usuarioService.desactivarUsuario(
                usuario.getIdUsuario());

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> usuarioService.iniciarSesion(
                                nombreUsuario,
                                "Clave123"));

        assertEquals(
                "La cuenta se encuentra desactivada.",
                excepcion.getMessage());

        // RN-US06: el registro continúa existiendo
        Usuario guardado =
                usuarioService.obtenerUsuarioPorId(
                        usuario.getIdUsuario());

        assertNotNull(guardado);
        assertFalse(guardado.getEstado());
    }


    // RN-US06
    @Test
    void activarUsuarioCorrectamente() {

        String nombreUsuario =
                "test_activar_" + System.currentTimeMillis();

        Usuario usuario = new Usuario();

        usuario.setUsuario(nombreUsuario);
        usuario.setClave("Clave123");
        usuario.setRol(Rol.CLIENTE);

        usuarioService.registrarUsuario(usuario);

        usuarioService.desactivarUsuario(
                usuario.getIdUsuario());

        Usuario desactivado =
                usuarioService.obtenerUsuarioPorId(
                        usuario.getIdUsuario());

        assertFalse(desactivado.getEstado());

        usuarioService.activarUsuario(
                usuario.getIdUsuario());

        Usuario activado =
                usuarioService.obtenerUsuarioPorId(
                        usuario.getIdUsuario());

        assertTrue(activado.getEstado());
    }


    // RN-US07
    @Test
    void cambiarClaveCorrectamente() {

        String nombreUsuario =
                "test_cambio_clave_"
                        + System.currentTimeMillis();

        Usuario usuario = new Usuario();

        usuario.setUsuario(nombreUsuario);
        usuario.setClave("ClaveAntigua123");
        usuario.setRol(Rol.CLIENTE);

        usuarioService.registrarUsuario(usuario);

        usuarioService.cambiarClave(
                usuario.getIdUsuario(),
                "ClaveNueva456");

        Usuario actualizado =
                usuarioService.obtenerUsuarioPorId(
                        usuario.getIdUsuario());

        assertTrue(
                PasswordUtil.verificar(
                        "ClaveNueva456",
                        actualizado.getClave()));

        assertFalse(
                PasswordUtil.verificar(
                        "ClaveAntigua123",
                        actualizado.getClave()));

        Usuario autenticado =
                usuarioService.iniciarSesion(
                        nombreUsuario,
                        "ClaveNueva456");

        assertNotNull(autenticado);
    }


    @Test
    void obtenerUsuarioPorIdCorrectamente() {

        String nombreUsuario =
                "test_buscar_" + System.currentTimeMillis();

        Usuario usuario = new Usuario();

        usuario.setUsuario(nombreUsuario);
        usuario.setClave("Clave123");
        usuario.setRol(Rol.CLIENTE);

        usuarioService.registrarUsuario(usuario);

        Usuario encontrado =
                usuarioService.obtenerUsuarioPorId(
                        usuario.getIdUsuario());

        assertNotNull(encontrado);

        assertEquals(
                usuario.getIdUsuario(),
                encontrado.getIdUsuario());

        assertEquals(
                nombreUsuario,
                encontrado.getUsuario());
    }


    @Test
    void obtenerTodosLosUsuariosCorrectamente() {

        List<Usuario> usuarios =
                usuarioService.obtenerTodosLosUsuarios();

        assertNotNull(usuarios);
    }
    
    @Test
    void actualizarUsuarioDebeConservarClave() {

        Usuario usuario = new Usuario();

        usuario.setUsuario(
                "usuario" + System.nanoTime());

        usuario.setClave(
                "Clave123");

        usuario.setRol(
                Rol.CLIENTE);

        usuario.setEstado(true);

        usuarioService.registrarUsuario(
                usuario);

        String hashOriginal =
                usuario.getClave();

        usuario.setUsuario(
                "actualizado" + System.nanoTime());

        // Simula una actualización que no envía contraseña
        usuario.setClave(null);

        usuarioService.actualizarUsuario(
                usuario);

        Usuario actualizado =
                usuarioService.obtenerUsuarioPorId(
                        usuario.getIdUsuario());

        assertEquals(
                hashOriginal,
                actualizado.getClave());

        assertTrue(
                PasswordUtil.verificar(
                        "Clave123",
                        actualizado.getClave()));
    }
}