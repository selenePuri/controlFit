package interfaces;

import java.util.List;
import model.Cliente;

public interface ClienteDAO {

    void registrarCliente(Cliente cliente);

    void actualizarCliente(Cliente cliente);

    Cliente obtenerClientePorId(Integer id);

    Cliente obtenerClientePorDni(String dni);

    List<Cliente> obtenerTodosLosClientes();
    
    boolean existeClientePorDni(String dni); 
}