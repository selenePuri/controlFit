package service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dao.PromocionDAOImpl;
import interfaces.PromocionDAO;
import model.Promocion;
import model.TipoMembresia;
import model.enums.TipoDescuento;

public class PromocionService {

    private final PromocionDAO promocionDAO;
    private final TipoMembresiaService tipoMembresiaService;

    public PromocionService() {
        this.promocionDAO = new PromocionDAOImpl();
        this.tipoMembresiaService = new TipoMembresiaService();
    }


    // RN-PR01, RN-PR02, RN-PR03 y RN-PR05
    public void registrarPromocion(
            Promocion promocion) {

        validarDatosPromocion(promocion);

        List<TipoMembresia> tiposValidos =
                obtenerTiposMembresiaValidos(
                        promocion.getTiposMembresia());

        promocion.setTiposMembresia(
                tiposValidos);

        if (promocion.getEstado() == null) {
            promocion.setEstado(true);
        }

        promocionDAO.registrarPromocion(
                promocion);
    }


    public void actualizarPromocion(
            Promocion promocion) {

        if (promocion == null
                || promocion.getIdPromocion() == null) {

            throw new IllegalArgumentException(
                    "La promoción que desea actualizar no es válida.");
        }

        Promocion existente =
                promocionDAO.obtenerPromocionPorId(
                        promocion.getIdPromocion());

        if (existente == null) {
            throw new IllegalArgumentException(
                    "La promoción no existe.");
        }

        validarDatosPromocion(promocion);

        List<TipoMembresia> tiposValidos =
                obtenerTiposMembresiaValidos(
                        promocion.getTiposMembresia());

        promocion.setTiposMembresia(
                tiposValidos);

        promocionDAO.actualizarPromocion(
                promocion);
    }


    public Promocion obtenerPromocionPorId(
            Integer idPromocion) {

        if (idPromocion == null
                || idPromocion <= 0) {

            throw new IllegalArgumentException(
                    "El código de la promoción no es válido.");
        }

        Promocion promocion =
                promocionDAO.obtenerPromocionPorId(
                        idPromocion);

        if (promocion == null) {
            throw new IllegalArgumentException(
                    "La promoción no existe.");
        }

        return promocion;
    }


    public List<Promocion> obtenerTodasLasPromociones() {

        return promocionDAO
                .obtenerTodasLasPromociones();
    }


    public List<Promocion> obtenerPromocionesVigentes(
            LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }

        return promocionDAO
                .obtenerPromocionesVigentes(
                        fecha);
    }


    public List<Promocion>
            obtenerPromocionesVigentesPorTipoMembresia(
                    Integer idTipo,
                    LocalDate fecha) {

        if (idTipo == null
                || idTipo <= 0) {

            throw new IllegalArgumentException(
                    "El tipo de membresía no es válido.");
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }

        tipoMembresiaService
                .obtenerTipoMembresiaPorId(
                        idTipo);

        return promocionDAO
                .obtenerPromocionesVigentesPorTipoMembresia(
                        idTipo,
                        fecha);
    }


    // RN-PR01, RN-PR02 y RN-PR03
    public Promocion obtenerPromocionValida(
            Integer idPromocion,
            TipoMembresia tipoMembresia,
            LocalDate fecha) {

        Promocion promocion =
                obtenerPromocionPorId(
                        idPromocion);

        // RN-PR01
        if (!Boolean.TRUE.equals(
                promocion.getEstado())) {

            throw new IllegalStateException(
                    "La promoción se encuentra inactiva.");
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }

        // RN-PR02
        if (fecha.isBefore(
                promocion.getFechaInicio())
                || fecha.isAfter(
                        promocion.getFechaFin())) {

            throw new IllegalStateException(
                    "La promoción no se encuentra vigente.");
        }

        if (tipoMembresia == null
                || tipoMembresia.getIdTipo() == null) {

            throw new IllegalArgumentException(
                    "El tipo de membresía no es válido.");
        }

        TipoMembresia tipoExistente =
                tipoMembresiaService
                        .obtenerTipoMembresiaPorId(
                                tipoMembresia.getIdTipo());

        // RN-PR03
        boolean compatible =
                promocion.getTiposMembresia()
                        .stream()
                        .anyMatch(tipo ->
                                tipo.getIdTipo()
                                        .equals(
                                                tipoExistente
                                                        .getIdTipo()));

        if (!compatible) {
            throw new IllegalArgumentException(
                    "La promoción no aplica al tipo de membresía seleccionado.");
        }

        return promocion;
    }


    // RN-PR04, RN-PR05 y RN-PR06
    public BigDecimal calcularDescuento(
            Promocion promocion,
            BigDecimal precioBase) {

        if (promocion == null) {
            return BigDecimal.ZERO;
        }

        if (precioBase == null
                || precioBase.compareTo(
                        BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El precio base debe ser mayor a 0.");
        }

        if (promocion.getTipoDescuento() == null) {
            throw new IllegalArgumentException(
                    "El tipo de descuento es obligatorio.");
        }

        if (promocion.getValorDescuento() == null
                || promocion.getValorDescuento()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El valor del descuento debe ser mayor a 0.");
        }

        BigDecimal descuento;

        // RN-PR04
        if (promocion.getTipoDescuento()
                == TipoDescuento.PORCENTAJE) {

            if (promocion.getValorDescuento()
                    .compareTo(
                            new BigDecimal("100")) > 0) {

                throw new IllegalArgumentException(
                        "El porcentaje de descuento no puede ser mayor a 100.");
            }

            descuento =
                    precioBase
                            .multiply(
                                    promocion.getValorDescuento())
                            .divide(
                                    new BigDecimal("100"),
                                    2,
                                    RoundingMode.HALF_UP);

        } else if (promocion.getTipoDescuento()
                == TipoDescuento.MONTO_FIJO) {

            descuento =
                    promocion.getValorDescuento();

        } else {

            throw new IllegalArgumentException(
                    "El tipo de descuento no es válido.");
        }

        // RN-PR05 y RN-PR06
        if (descuento.compareTo(
                precioBase) > 0) {

            throw new IllegalArgumentException(
                    "El descuento no puede ser mayor al precio de la membresía.");
        }

        return descuento;
    }


    public void desactivarPromocion(
            Integer idPromocion) {

        Promocion promocion =
                obtenerPromocionPorId(
                        idPromocion);

        promocion.setEstado(false);

        promocionDAO.actualizarPromocion(
                promocion);
    }


    public void activarPromocion(
            Integer idPromocion) {

        Promocion promocion =
                obtenerPromocionPorId(
                        idPromocion);

        promocion.setEstado(true);

        promocionDAO.actualizarPromocion(
                promocion);
    }


    // RN-PR02, RN-PR03 y RN-PR05
    private void validarDatosPromocion(
            Promocion promocion) {

        if (promocion == null) {
            throw new IllegalArgumentException(
                    "La promoción es obligatoria.");
        }

        if (promocion.getNombre() == null
                || promocion.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de la promoción es obligatorio.");
        }

        if (promocion.getTipoDescuento() == null) {
            throw new IllegalArgumentException(
                    "El tipo de descuento es obligatorio.");
        }

        if (promocion.getValorDescuento() == null
                || promocion.getValorDescuento()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El valor del descuento debe ser mayor a 0.");
        }

        if (promocion.getFechaInicio() == null
                || promocion.getFechaFin() == null) {

            throw new IllegalArgumentException(
                    "Las fechas de la promoción son obligatorias.");
        }

        if (promocion.getFechaFin()
                .isBefore(
                        promocion.getFechaInicio())) {

            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        if (promocion.getTipoDescuento()
                == TipoDescuento.PORCENTAJE
                && promocion.getValorDescuento()
                        .compareTo(
                                new BigDecimal("100")) > 0) {

            throw new IllegalArgumentException(
                    "El porcentaje de descuento no puede ser mayor a 100.");
        }

        // RN-PR03
        if (promocion.getTiposMembresia() == null
                || promocion.getTiposMembresia()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "La promoción debe estar asociada al menos a un tipo de membresía.");
        }
    }


    // RN-PR03
    private List<TipoMembresia>
            obtenerTiposMembresiaValidos(
                    List<TipoMembresia> tipos) {

        List<TipoMembresia> tiposValidos =
                new ArrayList<>();

        for (TipoMembresia tipo : tipos) {

            if (tipo == null
                    || tipo.getIdTipo() == null) {

                throw new IllegalArgumentException(
                        "La promoción contiene un tipo de membresía no válido.");
            }

            TipoMembresia existente =
                    tipoMembresiaService
                            .obtenerTipoMembresiaHabilitado(
                                    tipo.getIdTipo());

            boolean repetido =
                    tiposValidos.stream()
                            .anyMatch(t ->
                                    t.getIdTipo()
                                            .equals(
                                                    existente
                                                            .getIdTipo()));

            if (!repetido) {
                tiposValidos.add(
                        existente);
            }
        }

        return tiposValidos;
    }
}