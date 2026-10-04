package interfaces;

import java.util.List;
import model.Entrenador;

public interface EntrenadorDAO {

    void registrarEntrenador(Entrenador entrenador);

    void actualizarEntrenador(Entrenador entrenador);

    Entrenador obtenerEntrenadorPorId(Integer id);

    List<Entrenador> obtenerTodosLosEntrenadores();

    List<Entrenador> obtenerEntrenadoresPorEspecialidad(String especialidad);
    
    List<Entrenador> obtenerEntrenadoresActivos();
}