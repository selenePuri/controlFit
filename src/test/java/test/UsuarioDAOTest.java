package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import dao.UsuarioDAOImpl;
import interfaces.UsuarioDAO;
import model.Usuario;
import model.enums.Rol;

public class UsuarioDAOTest {

    private final UsuarioDAO dao = new UsuarioDAOImpl();

    private Usuario crearUsuario() {

        Usuario usuario = new Usuario();

        usuario.setUsuario("usuario_" + System.nanoTime());
        usuario.setClave("12345");
        usuario.setRol(Rol.ADMINISTRADOR);
        usuario.setEstado(true);

        dao.registrarUsuario(usuario);

        return usuario;
    }

    @Test
    void testRegistrarUsuario() {

        Usuario usuario = crearUsuario();

        assertNotNull(usuario.getIdUsuario());
    }

    @Test
    void testObtenerUsuarioPorId() {

        Usuario usuario = crearUsuario();

        Usuario encontrado =
                dao.obtenerUsuarioPorId(usuario.getIdUsuario());

        assertNotNull(encontrado);
        assertEquals(usuario.getUsuario(), encontrado.getUsuario());
    }

    @Test
    void testActualizarUsuario() {

        Usuario usuario = crearUsuario();

        usuario.setRol(Rol.RECEPCIONISTA);

        dao.actualizarUsuario(usuario);

        Usuario actualizado =
                dao.obtenerUsuarioPorId(usuario.getIdUsuario());

        assertEquals(Rol.RECEPCIONISTA, actualizado.getRol());
    }

    @Test
    void testObtenerUsuarioPorNombre() {

        Usuario usuario = crearUsuario();

        Usuario encontrado =
                dao.obtenerUsuarioPorNombre(usuario.getUsuario());

        assertNotNull(encontrado);
        assertEquals(usuario.getUsuario(), encontrado.getUsuario());
    }

    @Test
    void testListarUsuarios() {

        crearUsuario();

        List<Usuario> usuarios =
                dao.obtenerTodosLosUsuarios();

        assertNotNull(usuarios);
        assertFalse(usuarios.isEmpty());
    }

    @Test
    void testValidarLogin() {

        Usuario usuario = crearUsuario();

        Usuario encontrado =
                dao.validarLogin(
                        usuario.getUsuario(),
                        "12345"
                );

        assertNotNull(encontrado);
        assertEquals(usuario.getUsuario(), encontrado.getUsuario());
    }

    @Test
    void testLoginIncorrecto() {

        Usuario resultado =
                dao.validarLogin(
                        "usuario_inexistente",
                        "incorrecta"
                );

        assertNull(resultado);
    }
}