package service;

import java.util.List;

import dao.UsuarioDAOImpl;
import interfaces.UsuarioDAO;
import model.Usuario;
import model.enums.Rol;
import util.PasswordUtil;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }


    // RN-US01, RN-US04 y RN-US07
    public void registrarUsuario(Usuario usuario) {

        validarDatosUsuario(usuario);

        // RN-US01
        if (usuarioDAO.existeNombreUsuario(usuario.getUsuario())) {
            throw new IllegalArgumentException(
                    "El nombre de usuario ya se encuentra registrado.");
        }

        // RN-US07
        usuario.setClave(
                PasswordUtil.generarHash(
                        usuario.getClave()));

        usuarioDAO.registrarUsuario(usuario);
    }


    // RN-US02, RN-US03 y RN-US07
    public Usuario iniciarSesion(
            String nombreUsuario,
            String clave) {

        if (nombreUsuario == null
                || nombreUsuario.isBlank()
                || clave == null
                || clave.isBlank()) {

            throw new IllegalArgumentException(
                    "Debe ingresar usuario y contraseña.");
        }

        Usuario usuario =
                usuarioDAO.obtenerUsuarioPorNombre(
                        nombreUsuario);

        // RN-US03
        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Usuario o contraseña incorrectos.");
        }

        // RN-US02
        if (!Boolean.TRUE.equals(
                usuario.getEstado())) {

            throw new IllegalStateException(
                    "La cuenta se encuentra desactivada.");
        }

        // RN-US07
        if (!PasswordUtil.verificar(
                clave,
                usuario.getClave())) {

            throw new IllegalArgumentException(
                    "Usuario o contraseña incorrectos.");
        }

        return usuario;
    }


    // RN-US01 y RN-US04
    public void actualizarUsuario(Usuario usuario) {

        if (usuario == null
                || usuario.getIdUsuario() == null) {

            throw new IllegalArgumentException(
                    "El usuario que desea actualizar no es válido.");
        }

        Usuario existente =
                usuarioDAO.obtenerUsuarioPorId(
                        usuario.getIdUsuario());

        if (existente == null) {
            throw new IllegalArgumentException(
                    "El usuario no existe.");
        }

        validarDatosActualizacion(usuario);

        Usuario mismoNombre =
                usuarioDAO.obtenerUsuarioPorNombre(
                        usuario.getUsuario());

        // RN-US01
        if (mismoNombre != null
                && !mismoNombre.getIdUsuario()
                        .equals(usuario.getIdUsuario())) {

            throw new IllegalArgumentException(
                    "El nombre de usuario ya pertenece a otra cuenta.");
        }

        // RN-US07: actualizar datos no cambia la contraseña
        usuario.setClave(
                existente.getClave());

        usuarioDAO.actualizarUsuario(usuario);
    }


    // RN-US07
    public void cambiarClave(
            Integer idUsuario,
            String nuevaClave) {

        Usuario usuario =
                obtenerUsuarioExistente(idUsuario);

        if (nuevaClave == null
                || nuevaClave.isBlank()) {

            throw new IllegalArgumentException(
                    "La nueva contraseña es obligatoria.");
        }

        usuario.setClave(
                PasswordUtil.generarHash(
                        nuevaClave));

        usuarioDAO.actualizarUsuario(usuario);
    }


    public Usuario obtenerUsuarioPorId(
            Integer idUsuario) {

        return obtenerUsuarioExistente(
                idUsuario);
    }


    public List<Usuario> obtenerTodosLosUsuarios() {

        return usuarioDAO
                .obtenerTodosLosUsuarios();
    }


    // RN-US06
    public void desactivarUsuario(
            Integer idUsuario) {

        Usuario usuario =
                obtenerUsuarioExistente(
                        idUsuario);

        usuario.setEstado(false);

        usuarioDAO.actualizarUsuario(
                usuario);
    }


    public void activarUsuario(
            Integer idUsuario) {

        Usuario usuario =
                obtenerUsuarioExistente(
                        idUsuario);

        usuario.setEstado(true);

        usuarioDAO.actualizarUsuario(
                usuario);
    }


    private Usuario obtenerUsuarioExistente(
            Integer idUsuario) {

        if (idUsuario == null
                || idUsuario <= 0) {

            throw new IllegalArgumentException(
                    "El código del usuario no es válido.");
        }

        Usuario usuario =
                usuarioDAO.obtenerUsuarioPorId(
                        idUsuario);

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no existe.");
        }

        return usuario;
    }


    // RN-US04
    private void validarDatosUsuario(
            Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario es obligatorio.");
        }

        if (usuario.getUsuario() == null
                || usuario.getUsuario().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio.");
        }

        if (usuario.getClave() == null
                || usuario.getClave().isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        if (usuario.getRol() == null) {
            throw new IllegalArgumentException(
                    "El usuario debe tener un rol.");
        }

        validarRol(
                usuario.getRol());

        if (usuario.getEstado() == null) {
            usuario.setEstado(true);
        }
    }


    // RN-US04
    private void validarDatosActualizacion(
            Usuario usuario) {

        if (usuario.getUsuario() == null
                || usuario.getUsuario().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio.");
        }

        if (usuario.getRol() == null) {
            throw new IllegalArgumentException(
                    "El usuario debe tener un rol.");
        }

        validarRol(
                usuario.getRol());

        if (usuario.getEstado() == null) {
            usuario.setEstado(true);
        }
    }


    // RN-US04
    private void validarRol(Rol rol) {

        if (rol != Rol.ADMINISTRADOR
                && rol != Rol.RECEPCIONISTA
                && rol != Rol.ENTRENADOR
                && rol != Rol.CLIENTE) {

            throw new IllegalArgumentException(
                    "El rol del usuario no es válido.");
        }
    }
}