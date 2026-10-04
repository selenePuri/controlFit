package interfaces;

import java.util.List;
import java.time.LocalDate;

import model.RutinaCliente;

public interface RutinaClienteDAO {

    void registrarRutinaCliente(RutinaCliente rutinaCliente);

    void actualizarRutinaCliente(RutinaCliente rutinaCliente);

    RutinaCliente obtenerRutinaClientePorId(Integer id);

    List<RutinaCliente> obtenerTodasLasRutinasCliente();

    List<RutinaCliente> obtenerRutinasPorCliente(Integer idCliente);

    List<RutinaCliente> obtenerClientesPorRutina(Integer idRutina);
    
    RutinaCliente obtenerAsignacionActiva(Integer idCliente, Integer idRutina, LocalDate fecha);
}