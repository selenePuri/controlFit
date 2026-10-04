package service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import model.Cliente;
import model.Usuario;
import model.enums.Rol;
import util.PasswordUtil;

public class ClienteServiceTest {

    private final ClienteService clienteService =
            new ClienteService();


    // RN-CL01 y RN-CL02
    @Test
    void registrarClienteSinCuentaCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        clienteService.registrarCliente(
                cliente);

        assertNotNull(
                cliente.getIdCliente());

        assertNull(
                cliente.getUsuario());

        assertTrue(
                Boolean.TRUE.equals(
                        cliente.getEstado()));
    }


    // RN-CL01
    @Test
    void noDebeRegistrarClienteConDniDuplicado() {

        String dni =
                generarDni();

        Cliente cliente1 =
                crearClientePrueba();

        cliente1.setDni(dni);

        clienteService.registrarCliente(
                cliente1);

        Cliente cliente2 =
                crearClientePrueba();

        cliente2.setDni(dni);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> clienteService
                                .registrarCliente(
                                        cliente2));

        assertEquals(
                "Ya existe un cliente registrado con ese DNI.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarClienteConDniVacio() {

        Cliente cliente =
                crearClientePrueba();

        cliente.setDni("");

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> clienteService
                                .registrarCliente(
                                        cliente));

        assertEquals(
                "El DNI es obligatorio.",
                excepcion.getMessage());
    }


    @Test
    void noDebeRegistrarClienteConDniInvalido() {

        Cliente cliente =
                crearClientePrueba();

        cliente.setDni(
                "1234");

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> clienteService
                                .registrarCliente(
                                        cliente));

        assertEquals(
                "El DNI debe contener exactamente 8 números.",
                excepcion.getMessage());
    }


    // RN-CL03 y RN-CL10
    @Test
    void crearCuentaParaClienteExistenteCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        clienteService.registrarCliente(
                cliente);

        Usuario usuario =
                crearUsuarioPrueba();

        String claveOriginal =
                usuario.getClave();

        clienteService.crearCuentaCliente(
                cliente.getDni(),
                usuario);

        Cliente actualizado =
                clienteService.obtenerClientePorDni(
                        cliente.getDni());

        assertNotNull(
                actualizado.getUsuario());

        assertNotNull(
                actualizado.getUsuario()
                        .getIdUsuario());

        assertEquals(
                Rol.CLIENTE,
                actualizado.getUsuario()
                        .getRol());

        assertTrue(
                Boolean.TRUE.equals(
                        actualizado.getUsuario()
                                .getEstado()));

        assertTrue(
                PasswordUtil.verificar(
                        claveOriginal,
                        actualizado.getUsuario()
                                .getClave()));
    }


    // RN-CL05
    @Test
    void noDebeCrearSegundaCuentaParaMismoCliente() {

        Cliente cliente =
                crearClientePrueba();

        clienteService.registrarCliente(
                cliente);

        Usuario usuario1 =
                crearUsuarioPrueba();

        clienteService.crearCuentaCliente(
                cliente.getDni(),
                usuario1);

        Usuario usuario2 =
                crearUsuarioPrueba();

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> clienteService
                                .crearCuentaCliente(
                                        cliente.getDni(),
                                        usuario2));

        assertEquals(
                "El cliente ya tiene una cuenta de usuario.",
                excepcion.getMessage());
    }


    @Test
    void noDebeCrearCuentaParaClienteInexistente() {

        Usuario usuario =
                crearUsuarioPrueba();

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> clienteService
                                .crearCuentaCliente(
                                        generarDni(),
                                        usuario));

        assertEquals(
                "No existe un cliente registrado con ese DNI.",
                excepcion.getMessage());
    }


    // RN-US01
    @Test
    void noDebeCrearCuentaConNombreUsuarioDuplicado() {

        Cliente cliente1 =
                crearClientePrueba();

        clienteService.registrarCliente(
                cliente1);

        Usuario usuario1 =
                crearUsuarioPrueba();

        clienteService.crearCuentaCliente(
                cliente1.getDni(),
                usuario1);

        Cliente cliente2 =
                crearClientePrueba();

        clienteService.registrarCliente(
                cliente2);

        Usuario usuario2 =
                crearUsuarioPrueba();

        usuario2.setUsuario(
                usuario1.getUsuario());

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> clienteService
                                .crearCuentaCliente(
                                        cliente2.getDni(),
                                        usuario2));

        assertEquals(
                "El nombre de usuario ya se encuentra registrado.",
                excepcion.getMessage());
    }


    // RN-CL04, RN-CL07 y RN-CL08
    @Test
    void registrarClienteConCuentaCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        Usuario usuario =
                crearUsuarioPrueba();

        String claveOriginal =
                usuario.getClave();

        clienteService.registrarClienteConCuenta(
                cliente,
                usuario);

        assertNotNull(
                cliente.getIdCliente());

        assertNotNull(
                usuario.getIdUsuario());

        assertNotNull(
                cliente.getUsuario());

        assertEquals(
                usuario.getIdUsuario(),
                cliente.getUsuario()
                        .getIdUsuario());

        assertEquals(
                Rol.CLIENTE,
                usuario.getRol());

        assertTrue(
                PasswordUtil.verificar(
                        claveOriginal,
                        usuario.getClave()));
    }


    // RN-CL06
    @Test
    void registrarClienteConCuentaDebeReutilizarClienteExistente() {

        Cliente existente =
                crearClientePrueba();

        clienteService.registrarCliente(
                existente);

        Integer idOriginal =
                existente.getIdCliente();

        Cliente clienteFormulario =
                crearClientePrueba();

        clienteFormulario.setDni(
                existente.getDni());

        Usuario usuario =
                crearUsuarioPrueba();

        clienteService.registrarClienteConCuenta(
                clienteFormulario,
                usuario);

        Cliente encontrado =
                clienteService.obtenerClientePorDni(
                        existente.getDni());

        // RN-CL06: debe conservar el mismo cliente
        assertEquals(
                idOriginal,
                encontrado.getIdCliente());

        assertNotNull(
                encontrado.getUsuario());

        assertEquals(
                usuario.getIdUsuario(),
                encontrado.getUsuario()
                        .getIdUsuario());

        assertEquals(
                Rol.CLIENTE,
                encontrado.getUsuario()
                        .getRol());

        // No se creó un segundo cliente
        assertEquals(
                idOriginal,
                clienteFormulario.getIdCliente());
    }


    @Test
    void noDebeRegistrarClienteConCuentaConUsuarioDuplicado() {

        Cliente cliente1 =
                crearClientePrueba();

        Usuario usuario1 =
                crearUsuarioPrueba();

        clienteService.registrarClienteConCuenta(
                cliente1,
                usuario1);

        Cliente cliente2 =
                crearClientePrueba();

        Usuario usuario2 =
                crearUsuarioPrueba();

        usuario2.setUsuario(
                usuario1.getUsuario());

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> clienteService
                                .registrarClienteConCuenta(
                                        cliente2,
                                        usuario2));

        assertEquals(
                "El nombre de usuario ya se encuentra registrado.",
                excepcion.getMessage());
    }


    @Test
    void obtenerClientePorIdCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        clienteService.registrarCliente(
                cliente);

        Cliente encontrado =
                clienteService.obtenerClientePorId(
                        cliente.getIdCliente());

        assertNotNull(encontrado);

        assertEquals(
                cliente.getIdCliente(),
                encontrado.getIdCliente());

        assertEquals(
                cliente.getDni(),
                encontrado.getDni());
    }


    @Test
    void obtenerClientePorDniCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        clienteService.registrarCliente(
                cliente);

        Cliente encontrado =
                clienteService.obtenerClientePorDni(
                        cliente.getDni());

        assertNotNull(encontrado);

        assertEquals(
                cliente.getIdCliente(),
                encontrado.getIdCliente());
    }


    @Test
    void obtenerTodosLosClientesCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        clienteService.registrarCliente(
                cliente);

        List<Cliente> clientes =
                clienteService.obtenerTodosLosClientes();

        assertNotNull(clientes);

        assertTrue(
                clientes.stream()
                        .anyMatch(c ->
                                c.getIdCliente()
                                        .equals(
                                                cliente.getIdCliente())));
    }


    @Test
    void desactivarYActivarClienteCorrectamente() {

        Cliente cliente =
                crearClientePrueba();

        clienteService.registrarCliente(
                cliente);

        clienteService.desactivarCliente(
                cliente.getIdCliente());

        Cliente desactivado =
                clienteService.obtenerClientePorId(
                        cliente.getIdCliente());

        assertFalse(
                Boolean.TRUE.equals(
                        desactivado.getEstado()));

        clienteService.activarCliente(
                cliente.getIdCliente());

        Cliente activado =
                clienteService.obtenerClientePorId(
                        cliente.getIdCliente());

        assertTrue(
                Boolean.TRUE.equals(
                        activado.getEstado()));
    }


    private Cliente crearClientePrueba() {

        Cliente cliente =
                new Cliente();

        cliente.setDni(
                generarDni());

        cliente.setNombres(
                "Cliente");

        cliente.setApellidos(
                "Prueba");

        cliente.setTelefono(
                "987654321");

        cliente.setEstado(true);

        return cliente;
    }


    private Usuario crearUsuarioPrueba() {

        Usuario usuario =
                new Usuario();

        usuario.setUsuario(
                "cliente"
                        + Math.abs(
                                System.nanoTime()));

        usuario.setClave(
                "Clave123");

        usuario.setRol(
                Rol.CLIENTE);

        usuario.setEstado(true);

        return usuario;
    }


    private String generarDni() {

        long numero =
                Math.abs(
                        System.nanoTime())
                        % 90_000_000L
                        + 10_000_000L;

        return String.valueOf(numero);
    }
}