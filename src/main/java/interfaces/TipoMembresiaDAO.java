package interfaces;

import java.util.List;

import model.TipoMembresia;
import model.enums.TipoPlan;

public interface TipoMembresiaDAO {

    void registrarTipoMembresia(TipoMembresia tipoMembresia);

    void actualizarTipoMembresia(TipoMembresia tipoMembresia);

    TipoMembresia obtenerTipoMembresiaPorId(Integer id);

    TipoMembresia obtenerTipoMembresiaPorNombre(TipoPlan nombre);

    List<TipoMembresia> obtenerTodosLosTiposMembresia();
    
    List<TipoMembresia> obtenerTiposMembresiaActivos();
}