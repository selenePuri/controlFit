package interfaces;

import java.util.List;
import model.Usuario;

public interface UsuarioDAO {
	
	void registrarUsuario(Usuario usuario);
	
	void actualizarUsuario(Usuario usuario);
	
	Usuario obtenerUsuarioPorId(Integer id);
	
	Usuario obtenerUsuarioPorNombre(String nombreUsuario);
	
	List<Usuario> obtenerTodosLosUsuarios();
	
	Usuario validarLogin(String nombreUsuario, String clave);
	
	boolean existeNombreUsuario(String nombreUsuario);

}
