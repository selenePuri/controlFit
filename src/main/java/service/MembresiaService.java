package service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import dao.ClienteDAOImpl;
import dao.MembresiaDAOImpl;
import interfaces.ClienteDAO;
import interfaces.MembresiaDAO;
import model.Cliente;
import model.Membresia;
import model.Pago;
import model.Promocion;
import model.TipoMembresia;
import model.enums.EstadoMembresia;
import model.enums.EstadoPago;
import model.enums.MetodoPago;
import util.JPAUtil;

public class MembresiaService {

    private final MembresiaDAO membresiaDAO;
    private final ClienteDAO clienteDAO;
    private final TipoMembresiaService tipoMembresiaService;
    private final PromocionService promocionService;

    public MembresiaService() {
        this.membresiaDAO = new MembresiaDAOImpl();
        this.clienteDAO = new ClienteDAOImpl();
        this.tipoMembresiaService = new TipoMembresiaService();
        this.promocionService = new PromocionService();
    }


    // RN-ME01 a RN-ME13, RN-PA05 y RN-PA06
    public void contratarMembresia(
            Membresia membresia,
            MetodoPago metodoPago) {

        if (membresia == null) {
            throw new IllegalArgumentException(
                    "La membresía es obligatoria.");
        }

        if (metodoPago == null) {
            throw new IllegalArgumentException(
                    "El método de pago es obligatorio.");
        }


        // RN-ME01
        Cliente cliente =
                obtenerClienteValido(
                        membresia.getCliente());


        // RN-ME02 y RN-TM01
        TipoMembresia tipo =
                obtenerTipoMembresiaValido(
                        membresia.getTipoMembresia());


        // RN-ME03
        if (tipo.getDuracionDias() == null
                || tipo.getDuracionDias() <= 0) {

            throw new IllegalStateException(
                    "La duración del tipo de membresía no es válida.");
        }


        // RN-ME04
        if (tipo.getPrecio() == null
                || tipo.getPrecio()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalStateException(
                    "El precio del tipo de membresía no es válido.");
        }


        // RN-ME03
        LocalDate fechaInicio =
                membresia.getFechaInicio();

        if (fechaInicio == null) {
            throw new IllegalArgumentException(
                    "La fecha de inicio es obligatoria.");
        }


        // RN-ME03
        LocalDate fechaFin =
                fechaInicio.plusDays(
                        tipo.getDuracionDias() - 1L);


        // RN-ME13
        boolean existeSolapamiento =
                membresiaDAO
                        .existeMembresiaActivaEnPeriodo(
                                cliente.getIdCliente(),
                                fechaInicio,
                                fechaFin);

        if (existeSolapamiento) {
            throw new IllegalStateException(
                    "El cliente ya tiene una membresía activa durante ese período.");
        }


        // RN-ME04
        BigDecimal precioBase =
                tipo.getPrecio();

        BigDecimal descuento =
                BigDecimal.ZERO;

        Promocion promocion = null;


        // RN-ME05 y RN-PR01 a RN-PR07
        if (membresia.getPromocion() != null
                && membresia.getPromocion()
                        .getIdPromocion() != null) {

            promocion =
                    promocionService
                            .obtenerPromocionValida(
                                    membresia
                                            .getPromocion()
                                            .getIdPromocion(),
                                    tipo,
                                    fechaInicio);

            descuento =
                    promocionService
                            .calcularDescuento(
                                    promocion,
                                    precioBase);
        }


        // RN-ME06
        BigDecimal precioFinal =
                precioBase.subtract(
                        descuento);


        // RN-ME07
        if (precioFinal.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new IllegalStateException(
                    "El precio final de la membresía no puede ser negativo.");
        }


        // RN-PA05
        if (precioFinal.compareTo(
                BigDecimal.ZERO) == 0) {

            throw new IllegalStateException(
                    "El precio final debe ser mayor a 0 para registrar el pago inicial.");
        }


        // Datos calculados por el sistema
        membresia.setCliente(
                cliente);

        membresia.setTipoMembresia(
                tipo);

        membresia.setPromocion(
                promocion);

        membresia.setFechaInicio(
                fechaInicio);

        membresia.setFechaFin(
                fechaFin);

        membresia.setPrecioBase(
                precioBase);

        membresia.setDescuento(
                descuento);

        membresia.setPrecioFinal(
                precioFinal);


        // RN-ME08
        membresia.setEstado(
                EstadoMembresia.ACTIVA);


        // RN-PA05
        Pago pago =
                new Pago();

        pago.setMembresia(
                membresia);

        pago.setFechaPago(
                LocalDateTime.now());

        pago.setMonto(
                precioFinal);

        pago.setMetodoPago(
                metodoPago);

        pago.setEstado(
                EstadoPago.PAGADO);


        // RN-ME12 y RN-PA06
        EntityManager manager =
                JPAUtil.getEntityManager();

        try {

            manager.getTransaction()
                    .begin();

            manager.persist(
                    membresia);

            manager.persist(
                    pago);

            manager.getTransaction()
                    .commit();

        } catch (Exception e) {

            if (manager.getTransaction()
                    .isActive()) {

                manager.getTransaction()
                        .rollback();
            }

            throw e;

        } finally {

            manager.close();
        }
    }


    public Membresia obtenerMembresiaPorId(
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

        return membresia;
    }


    // RN-ME10
    public List<Membresia>
            obtenerTodasLasMembresias() {

        return membresiaDAO
                .obtenerTodasLasMembresias();
    }


    // RN-ME10
    public List<Membresia>
            obtenerMembresiasPorCliente(
                    Integer idCliente) {

        if (idCliente == null
                || idCliente <= 0) {

            throw new IllegalArgumentException(
                    "El código del cliente no es válido.");
        }

        Cliente cliente =
                clienteDAO.obtenerClientePorId(
                        idCliente);

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no existe.");
        }

        return membresiaDAO
                .obtenerMembresiasPorCliente(
                        cliente.getIdCliente());
    }


    public List<Membresia>
            obtenerMembresiasPorEstado(
                    EstadoMembresia estado) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado de la membresía es obligatorio.");
        }

        return membresiaDAO
                .obtenerMembresiasPorEstado(
                        estado);
    }


    // RN-ME09 y RN-ME11
    public Membresia obtenerMembresiaVigente(
            Integer idCliente,
            LocalDate fecha) {

        if (idCliente == null
                || idCliente <= 0) {

            throw new IllegalArgumentException(
                    "El código del cliente no es válido.");
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }

        return membresiaDAO
                .obtenerMembresiaVigentePorCliente(
                        idCliente,
                        fecha);
    }


    // RN-ME09 y RN-ME11
    public boolean tieneMembresiaVigente(
            Integer idCliente,
            LocalDate fecha) {

        Membresia membresia =
                obtenerMembresiaVigente(
                        idCliente,
                        fecha);

        return membresia != null;
    }


    public void actualizarEstado(
            Integer idMembresia,
            EstadoMembresia nuevoEstado) {

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El nuevo estado de la membresía es obligatorio.");
        }

        Membresia membresia =
                obtenerMembresiaPorId(
                        idMembresia);

        membresia.setEstado(
                nuevoEstado);

        membresiaDAO
                .actualizarMembresia(
                        membresia);
    }


    // RN-ME01 y RN-CL09
    private Cliente obtenerClienteValido(
            Cliente clienteRecibido) {

        if (clienteRecibido == null
                || clienteRecibido.getIdCliente() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un cliente.");
        }

        Cliente cliente =
                clienteDAO
                        .obtenerClientePorId(
                                clienteRecibido
                                        .getIdCliente());

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no existe.");
        }

        if (!Boolean.TRUE.equals(
                cliente.getEstado())) {

            throw new IllegalStateException(
                    "El cliente se encuentra inactivo y no puede adquirir una membresía.");
        }

        return cliente;
    }


    // RN-ME02 y RN-TM01
    private TipoMembresia obtenerTipoMembresiaValido(
            TipoMembresia tipoRecibido) {

        if (tipoRecibido == null
                || tipoRecibido.getIdTipo() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un tipo de membresía.");
        }

        return tipoMembresiaService
                .obtenerTipoMembresiaHabilitado(
                        tipoRecibido.getIdTipo());
    }
}