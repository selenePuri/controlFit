package service;

import java.math.BigDecimal;
import java.util.List;

import dao.TipoMembresiaDAOImpl;
import interfaces.TipoMembresiaDAO;
import model.TipoMembresia;
import model.enums.TipoPlan;

public class TipoMembresiaService {

    private final TipoMembresiaDAO tipoMembresiaDAO;

    public TipoMembresiaService() {
        this.tipoMembresiaDAO = new TipoMembresiaDAOImpl();
    }


    // RN-TM01, RN-TM02 y RN-TM03
    public void registrarTipoMembresia(
            TipoMembresia tipoMembresia) {

        validarTipoMembresia(tipoMembresia);

        TipoMembresia existente =
                tipoMembresiaDAO
                        .obtenerTipoMembresiaPorNombre(
                                tipoMembresia.getNombre());

        if (existente != null) {
            throw new IllegalArgumentException(
                    "Ya existe un tipo de membresía para ese plan.");
        }

        // RN-TM03
        tipoMembresia.setDuracionDias(
                obtenerDuracionPlan(
                        tipoMembresia.getNombre()));

        if (tipoMembresia.getEstado() == null) {
            tipoMembresia.setEstado(true);
        }

        tipoMembresiaDAO
                .registrarTipoMembresia(
                        tipoMembresia);
    }


    public void actualizarTipoMembresia(
            TipoMembresia tipoMembresia) {

        if (tipoMembresia == null
                || tipoMembresia.getIdTipo() == null) {

            throw new IllegalArgumentException(
                    "El tipo de membresía que desea actualizar no es válido.");
        }

        TipoMembresia existente =
                tipoMembresiaDAO
                        .obtenerTipoMembresiaPorId(
                                tipoMembresia.getIdTipo());

        if (existente == null) {
            throw new IllegalArgumentException(
                    "El tipo de membresía no existe.");
        }

        validarTipoMembresia(tipoMembresia);

        TipoMembresia mismoPlan =
                tipoMembresiaDAO
                        .obtenerTipoMembresiaPorNombre(
                                tipoMembresia.getNombre());

        if (mismoPlan != null
                && !mismoPlan.getIdTipo()
                        .equals(tipoMembresia.getIdTipo())) {

            throw new IllegalArgumentException(
                    "Ya existe un tipo de membresía para ese plan.");
        }

        // RN-TM03
        tipoMembresia.setDuracionDias(
                obtenerDuracionPlan(
                        tipoMembresia.getNombre()));

        tipoMembresiaDAO
                .actualizarTipoMembresia(
                        tipoMembresia);
    }


    public TipoMembresia obtenerTipoMembresiaPorId(
            Integer idTipoMembresia) {

        if (idTipoMembresia == null
                || idTipoMembresia <= 0) {

            throw new IllegalArgumentException(
                    "El código del tipo de membresía no es válido.");
        }

        TipoMembresia tipo =
                tipoMembresiaDAO
                        .obtenerTipoMembresiaPorId(
                                idTipoMembresia);

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de membresía no existe.");
        }

        return tipo;
    }


    public List<TipoMembresia>
            obtenerTodosLosTiposMembresia() {

        return tipoMembresiaDAO
                .obtenerTodosLosTiposMembresia();
    }


    // RN-TM01
    public List<TipoMembresia>
            obtenerTiposMembresiaActivos() {

        return tipoMembresiaDAO
                .obtenerTiposMembresiaActivos();
    }


    // RN-TM01
    public TipoMembresia obtenerTipoMembresiaHabilitado(
            Integer idTipoMembresia) {

        TipoMembresia tipo =
                obtenerTipoMembresiaPorId(
                        idTipoMembresia);

        if (!Boolean.TRUE.equals(
                tipo.getEstado())) {

            throw new IllegalStateException(
                    "El tipo de membresía se encuentra inactivo.");
        }

        return tipo;
    }


    public void desactivarTipoMembresia(
            Integer idTipoMembresia) {

        TipoMembresia tipo =
                obtenerTipoMembresiaPorId(
                        idTipoMembresia);

        tipo.setEstado(false);

        tipoMembresiaDAO
                .actualizarTipoMembresia(tipo);
    }


    public void activarTipoMembresia(
            Integer idTipoMembresia) {

        TipoMembresia tipo =
                obtenerTipoMembresiaPorId(
                        idTipoMembresia);

        tipo.setEstado(true);

        tipoMembresiaDAO
                .actualizarTipoMembresia(tipo);
    }


    // RN-TM02
    private void validarTipoMembresia(
            TipoMembresia tipoMembresia) {

        if (tipoMembresia == null) {
            throw new IllegalArgumentException(
                    "El tipo de membresía es obligatorio.");
        }

        if (tipoMembresia.getNombre() == null) {
            throw new IllegalArgumentException(
                    "El plan de membresía es obligatorio.");
        }

        if (tipoMembresia.getPrecio() == null) {
            throw new IllegalArgumentException(
                    "El precio de la membresía es obligatorio.");
        }

        if (tipoMembresia.getPrecio()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El precio de la membresía debe ser mayor a 0.");
        }
    }


    // RN-TM03
    private Integer obtenerDuracionPlan(
            TipoPlan plan) {

        switch (plan) {

            case PASE_DIARIO:
                return 1;

            case MENSUAL:
                return 30;

            case TRIMESTRAL:
                return 90;

            case SEMESTRAL:
                return 180;

            case ANUAL:
                return 365;

            default:
                throw new IllegalArgumentException(
                        "El plan de membresía no es válido.");
        }
    }
}