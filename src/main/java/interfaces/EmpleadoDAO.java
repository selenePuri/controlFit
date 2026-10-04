package interfaces;

import java.util.List;
import model.Empleado;

public interface EmpleadoDAO {
	
	void registrarEmpleado(Empleado empleado);
	
	void actualizarEmpleado(Empleado empleado);

    Empleado obtenerEmpleadoPorId(Integer id);

    Empleado obtenerEmpleadoPorDni(String dni);

    List<Empleado> obtenerTodosLosEmpleados();

}
