package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

public class RestauranteTest
{
    private static final String CARPETA_PRUEBAS = "./data/pruebas/";

    private Restaurante restaurante;

    private File ingredientes;
    private File menu;
    private File combos;

    @BeforeEach
    void setUp() throws Exception
    {
        restaurante = new Restaurante();
        ingredientes = new File(CARPETA_PRUEBAS + "ingredientes.txt");
        menu = new File(CARPETA_PRUEBAS + "menu.txt");
        combos = new File(CARPETA_PRUEBAS + "combos.txt");
    }

    private File archivo(String nombre)
    {
        return new File(CARPETA_PRUEBAS + nombre);
    }

    @Test
    void testRestauranteVacio()
    {
        assertTrue(restaurante.getIngredientes().isEmpty());
        assertTrue(restaurante.getMenuBase().isEmpty());
        assertTrue(restaurante.getMenuCombos().isEmpty());
        assertTrue(restaurante.getPedidos().isEmpty());
        assertNull(restaurante.getPedidoEnCurso());
    }

    @Test
    void testCargarInformacionRestaurante() throws Exception
    {
        restaurante.cargarInformacionRestaurante(ingredientes, menu, combos);

        assertEquals(3, restaurante.getIngredientes().size(), "las líneas vacías no deben cargarse como ingredientes");
        assertEquals("tomate", restaurante.getIngredientes().get(1).getNombre());
        assertEquals(2500, restaurante.getIngredientes().get(2).getCostoAdicional());

        assertEquals(3, restaurante.getMenuBase().size(), "las líneas vacías no deben cargarse como productos");
        assertEquals("papas medianas", restaurante.getMenuBase().get(1).getNombre());
        assertEquals(5500, restaurante.getMenuBase().get(1).getPrecio());

        assertEquals(2, restaurante.getMenuCombos().size(), "las líneas vacías no deben cargarse como combos");
        Combo comboCorral = restaurante.getMenuCombos().get(0);
        assertEquals("combo corral", comboCorral.getNombre());
        assertEquals(22050, comboCorral.getPrecio());
        Combo comboEspecial = restaurante.getMenuCombos().get(1);
        assertEquals(17195, comboEspecial.getPrecio());
    }

    @Test
    void testCargarDatosReales() throws Exception
    {
        restaurante.cargarInformacionRestaurante(new File("./data/ingredientes.txt"), new File("./data/menu.txt"), new File("./data/combos.txt"));
        assertEquals(15, restaurante.getIngredientes().size());
        assertEquals(22, restaurante.getMenuBase().size());
        assertEquals(4, restaurante.getMenuCombos().size());
    }

    @Test
    void testIngredienteRepetido()
    {
        IngredienteRepetidoException e = assertThrows(IngredienteRepetidoException.class,
                () -> restaurante.cargarInformacionRestaurante(archivo("ingredientes_repetidos.txt"), menu, combos));
        assertEquals("El ingrediente lechuga está repetido", e.getMessage());
    }

    @Test
    void testIngredienteFormatoInvalido()
    {
        assertThrows(NumberFormatException.class, () -> restaurante.cargarInformacionRestaurante(archivo("ingredientes_formato_invalido.txt"), menu, combos));
    }

    @Test
    void testProductoRepetido()
    {
        ProductoRepetidoException e = assertThrows(ProductoRepetidoException.class,
                () -> restaurante.cargarInformacionRestaurante(ingredientes, archivo("menu_repetido.txt"), combos));
        assertEquals("El producto corral está repetido", e.getMessage());
    }

    @Test
    void testComboRepetido()
    {
        ProductoRepetidoException e = assertThrows(ProductoRepetidoException.class,
                () -> restaurante.cargarInformacionRestaurante(ingredientes, menu, archivo("combos_repetidos.txt")));
        assertEquals("El producto combo corral está repetido", e.getMessage());
    }

    @Test
    void testComboProductoFaltante()
    {
        ProductoFaltanteException e = assertThrows(ProductoFaltanteException.class,
                () -> restaurante.cargarInformacionRestaurante(ingredientes, menu, archivo("combos_producto_faltante.txt")));
        assertEquals("El producto malteada no aparece en la información del restaurante", e.getMessage());
    }

    @Test
    void testArchivoInexistente()
    {
        assertThrows(FileNotFoundException.class, () -> new Restaurante().cargarInformacionRestaurante(archivo("no_existe.txt"), menu, combos));
        assertThrows(FileNotFoundException.class, () -> new Restaurante().cargarInformacionRestaurante(ingredientes, archivo("no_existe.txt"), combos));
        assertThrows(FileNotFoundException.class, () -> new Restaurante().cargarInformacionRestaurante(ingredientes, menu, archivo("no_existe.txt")));
    }

    @Test
    void testIniciarPedido() throws Exception
    {
        restaurante.iniciarPedido("Ana", "Calle 1");
        assertNotNull(restaurante.getPedidoEnCurso());
        assertEquals("Ana", restaurante.getPedidoEnCurso().getNombreCliente());
    }

    @Test
    void testIniciarPedidoConPedidoEnCurso() throws Exception
    {
        restaurante.iniciarPedido("Ana", "Calle 1");
        YaHayUnPedidoEnCursoException e = assertThrows(YaHayUnPedidoEnCursoException.class, () -> restaurante.iniciarPedido("Beto", "Calle 2"));
        assertEquals("Ya existe un pedido en curso, para el cliente Ana así que no se puede crear un pedido para Beto", e.getMessage());
        assertEquals("Ana", restaurante.getPedidoEnCurso().getNombreCliente(), "el pedido en curso no debe cambiar");
    }

    @Test
    void testCerrarSinPedidoEnCurso()
    {
        NoHayPedidoEnCursoException e = assertThrows(NoHayPedidoEnCursoException.class, () -> restaurante.cerrarYGuardarPedido());
        assertEquals("Actualmente no hay un pedido en curso", e.getMessage());
    }

    @Test
    void testCerrarYGuardarPedido() throws Exception
    {
        restaurante.cargarInformacionRestaurante(ingredientes, menu, combos);
        restaurante.iniciarPedido("Ana", "Calle 1");
        Pedido pedido = restaurante.getPedidoEnCurso();
        pedido.agregarProducto(restaurante.getMenuBase().get(0));
        pedido.agregarProducto(restaurante.getMenuCombos().get(0));

        restaurante.cerrarYGuardarPedido();

        assertNull(restaurante.getPedidoEnCurso(), "después de cerrar no debe haber pedido en curso");
        assertEquals(1, restaurante.getPedidos().size(), "el pedido cerrado debe quedar en el histórico");
        assertSame(pedido, restaurante.getPedido(pedido.getIdPedido()), "el pedido debe poder consultarse por id");

        File factura = new File("./facturas/factura_" + pedido.getIdPedido() + ".txt");
        try
        {
            assertTrue(factura.exists(), "la factura debió guardarse en la carpeta de facturas");
            assertEquals(pedido.generarTextoFactura(), Files.readString(factura.toPath()));
        }
        finally
        {
            factura.delete();
        }

        restaurante.iniciarPedido("Beto", "Calle 2");
        Pedido segundo = restaurante.getPedidoEnCurso();
        assertEquals("Beto", segundo.getNombreCliente());
        restaurante.cerrarYGuardarPedido();
        new File("./facturas/factura_" + segundo.getIdPedido() + ".txt").delete();

        assertEquals(2, restaurante.getPedidos().size());
        assertSame(segundo, restaurante.getPedido(segundo.getIdPedido()), "el segundo pedido debe poder consultarse por id");
    }

    @Test
    void testGetPedidoInexistente()
    {
        assertNull(restaurante.getPedido(-1));
    }
}
