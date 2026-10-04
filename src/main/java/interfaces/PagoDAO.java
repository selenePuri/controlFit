package interfaces;

import java.util.List;

import model.Pago;
import model.enums.EstadoPago;

public interface PagoDAO {

    void registrarPago(Pago pago);

    void actualizarPago(Pago pago);

    Pago obtenerPagoPorId(Integer id);

    List<Pago> obtenerTodosLosPagos();

    List<Pago> obtenerPagosPorMembresia(Integer idMembresia);

    List<Pago> obtenerPagosPorEstado(EstadoPago estado);
}