package interfaces;

import java.time.LocalDate;
import java.util.List;

import model.Membresia;
import model.enums.EstadoMembresia;

public interface MembresiaDAO {

    void registrarMembresia(Membresia membresia);

    void actualizarMembresia(Membresia membresia);

    Membresia obtenerMembresiaPorId(Integer id);

    List<Membresia> obtenerTodasLasMembresias();

    List<Membresia> obtenerMembresiasPorCliente(Integer idCliente);

    List<Membresia> obtenerMembresiasPorEstado(EstadoMembresia estado);
    
    Membresia obtenerMembresiaVigentePorCliente(Integer idCliente, LocalDate fecha);

    boolean existeMembresiaActivaEnPeriodo(Integer idCliente, LocalDate fechaInicio, LocalDate fechaFin);
}