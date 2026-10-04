package interfaces;

import java.util.List;

import model.Rutina;

public interface RutinaDAO {

    void registrarRutina(Rutina rutina);

    void actualizarRutina(Rutina rutina);

    Rutina obtenerRutinaPorId(Integer id);

    List<Rutina> obtenerTodasLasRutinas();

    List<Rutina> obtenerRutinasPorEntrenador(Integer idEntrenador);
    
    List<Rutina> obtenerRutinasActivas();
}