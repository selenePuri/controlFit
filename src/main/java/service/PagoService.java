package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import dao.MembresiaDAOImpl;
import dao.PagoDAOImpl;
import interfaces.MembresiaDAO;
import interfaces.PagoDAO;
import model.Membresia;
import model.Pago;
import model.enums.EstadoPago;
import model.enums.MetodoPago;

public class PagoService {

    private final PagoDAO pagoDAO;
    private final MembresiaDAO membresiaDAO;

    public PagoService() {
        this.pagoDAO = new PagoDAOImpl();
        this.membresiaDAO = new MembresiaDAOImpl();
    }


    // RN-PA01, RN-PA02, RN-PA03 y RN-PA04
    public void registrarPago(Pago pago) {

        Membresia membresia =
                validarPago(pago);

        // Usamos la membresía real de la BD
        pago.setMembresia(
                membresia);

        if (pago.getFechaPago() == null) {
            pago.setFechaPago(
                    LocalDateTime.now());
        }

        if (pago.getEstado() == null) {
            pago.setEstado(
                    EstadoPago.PAGADO);
        }

        pagoDAO.registrarPago(
                pago);
    }


    public void actualizarPago(Pago pago) {

        if (pago == null
                || pago.getIdPago() == null) {

            throw new IllegalArgumentException(
                    "El pago que desea actualizar no es válido.");
        }

        Pago existente =
                pagoDAO.obtenerPagoPorId(
                        pago.getIdPago());

        if (existente == null) {
            throw new IllegalArgumentException(
                    "El pago no existe.");
        }

        Membresia membresia =
                validarPago(pago);

        // Usamos la membresía real de la BD
        pago.setMembresia(
                membresia);

        pagoDAO.actualizarPago(
                pago);
    }


    public Pago obtenerPagoPorId(
            Integer idPago) {

        if (idPago == null
                || idPago <= 0) {

            throw new IllegalArgumentException(
                    "El código del pago no es válido.");
        }

        Pago pago =
                pagoDAO.obtenerPagoPorId(
                        idPago);

        if (pago == null) {
            throw new IllegalArgumentException(
                    "El pago no existe.");
        }

        return pago;
    }


    public List<Pago> obtenerTodosLosPagos() {

        return pagoDAO
                .obtenerTodosLosPagos();
    }


    public List<Pago> obtenerPagosPorMembresia(
            Integer idMembresia) {

        if (idMembresia == null
                || idMembresia <= 0) {

            throw new IllegalArgumentException(
                    "El código de la membresía no es válido.");
        }

        Membresia membresia =
                membresiaDAO
                        .obtenerMembresiaPorId(
                                idMembresia);

        if (membresia == null) {
            throw new IllegalArgumentException(
                    "La membresía no existe.");
        }

        return pagoDAO
                .obtenerPagosPorMembresia(
                        membresia.getIdMembresia());
    }


    public List<Pago> obtenerPagosPorEstado(
            EstadoPago estado) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del pago es obligatorio.");
        }

        return pagoDAO
                .obtenerPagosPorEstado(
                        estado);
    }


    // RN-PA01 a RN-PA04
    private Membresia validarPago(
            Pago pago) {

        if (pago == null) {
            throw new IllegalArgumentException(
                    "El pago es obligatorio.");
        }

        // RN-PA01
        Membresia membresiaRecibida =
                pago.getMembresia();

        if (membresiaRecibida == null
                || membresiaRecibida
                        .getIdMembresia() == null) {

            throw new IllegalArgumentException(
                    "El pago debe estar asociado a una membresía.");
        }

        Membresia membresia =
                membresiaDAO
                        .obtenerMembresiaPorId(
                                membresiaRecibida
                                        .getIdMembresia());

        if (membresia == null) {
            throw new IllegalArgumentException(
                    "La membresía asociada al pago no existe.");
        }


        // RN-PA02
        if (pago.getMonto() == null
                || pago.getMonto()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto del pago debe ser mayor a 0.");
        }


        // RN-PA03
        MetodoPago metodo =
                pago.getMetodoPago();

        if (metodo == null) {
            throw new IllegalArgumentException(
                    "El método de pago es obligatorio.");
        }


        // RN-PA04
        if (pago.getEstado() == null) {
            pago.setEstado(
                    EstadoPago.PAGADO);
        }

        return membresia;
    }
}