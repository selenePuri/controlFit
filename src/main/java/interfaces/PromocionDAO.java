package interfaces;

import java.time.LocalDate;
import java.util.List;

import model.Promocion;

public interface PromocionDAO {

    void registrarPromocion(Promocion promocion);

    void actualizarPromocion(Promocion promocion);

    Promocion obtenerPromocionPorId(Integer id);

    List<Promocion> obtenerTodasLasPromociones();

    List<Promocion> obtenerPromocionesVigentes(LocalDate fecha);
    
    List<Promocion> obtenerPromocionesVigentesPorTipoMembresia(Integer idTipoMembresia, LocalDate fecha);
}