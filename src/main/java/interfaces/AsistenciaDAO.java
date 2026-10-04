package interfaces;

import java.time.LocalDate;
import java.util.List;

import model.Asistencia;

public interface AsistenciaDAO {

    void registrarAsistencia(Asistencia asistencia);

    void actualizarAsistencia(Asistencia asistencia);

    Asistencia obtenerAsistenciaPorId(Integer id);

    List<Asistencia> obtenerTodasLasAsistencias();

    List<Asistencia> obtenerAsistenciasPorCliente(Integer idCliente);

    List<Asistencia> obtenerAsistenciasPorFecha(LocalDate fecha);
    
    Asistencia obtenerAsistenciaAbiertaPorCliente(Integer idCliente);
}