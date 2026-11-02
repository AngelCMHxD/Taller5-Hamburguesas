package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest
{
    private Pedido pedido;
    private ProductoMenu corral;
    private ProductoMenu gaseosa;

    @TempDir
    Path carpetaTemporal;

    @BeforeEach
    void setUp() throws Exception
    {
        pedido = new Pedido("Ana", "Calle 1 # 2-3");
        corral = new ProductoMenu("corral", 14000);
        gaseosa = new ProductoMenu("gaseosa", 5000);
    }

    @Test
    void testGetNombreCliente()
    {
        assertEquals("Ana", pedido.getNombreCliente(), "el nombre del cliente no es el esperado");
    }

    @Test
    void testIdsConsecutivos()
    {
        Pedido otro = new Pedido("Beto", "Carrera 4");
        assertEquals(pedido.getIdPedido() + 1, otro.getIdPedido(), "los ids deben ser consecutivos");
    }

    @Test
    void testPrecioPedidoVacio()
    {
        assertEquals(0, pedido.getPrecioTotalPedido(), "un pedido vacío debe costar 0");
    }

    @Test
    void testGetPrecioTotalPedido()
    {
        pedido.agregarProducto(corral);
        pedido.agregarProducto(gaseosa);
        assertEquals(22610, pedido.getPrecioTotalPedido(), "el total debe ser el neto + iva");
    }

    @Test
    void testIVATruncado()
    {
        pedido.agregarProducto(new ProductoMenu("agua", 1001));
        assertEquals(1191, pedido.getPrecioTotalPedido(), "el iva debe ser un entero");
    }

    @Test
    void testGenerarTextoFacturaVacio()
    {
        String esperado = "Cliente: Ana\n" + "Dirección: Calle 1 # 2-3\n" + "----------------\n" + "----------------\n" + "Precio Neto:  0\n" + "IVA:          0\n"
                + "Precio Total: 0\n";
        assertEquals(esperado, pedido.generarTextoFactura(), "la factura de un pedido vacío no es la esperada");
    }

    @Test
    void testGenerarTextoFacturaConProductos()
    {
        ArrayList<ProductoMenu> items = new ArrayList<ProductoMenu>();
        items.add(corral);
        items.add(gaseosa);
        Combo combo = new Combo("combo corral", 0.1, items);

        ProductoAjustado ajustado = new ProductoAjustado(corral);
        ajustado.agregarIngrediente(new Ingrediente("queso", 2500));

        pedido.agregarProducto(gaseosa);
        pedido.agregarProducto(combo);
        pedido.agregarProducto(ajustado);

        String esperado = "Cliente: Ana\n" + "Dirección: Calle 1 # 2-3\n" + "----------------\n" + gaseosa.generarTextoFactura() + combo.generarTextoFactura()
                + ajustado.generarTextoFactura() + "----------------\n" + "Precio Neto:  38600\n" + "IVA:          7334\n" + "Precio Total: 45934\n";
        assertEquals(esperado, pedido.generarTextoFactura(), "la factura del pedido no es la esperada");
    }

    @Test
    void testGuardarFactura() throws Exception
    {
        pedido.agregarProducto(corral);
        File archivo = carpetaTemporal.resolve("factura.txt").toFile();
        pedido.guardarFactura(archivo);

        assertTrue(archivo.exists(), "el archivo de la factura debió crearse");
        String contenido = Files.readString(archivo.toPath());
        assertEquals(pedido.generarTextoFactura(), contenido, "el archivo debe contener el texto de la factura");
    }

    @Test
    void testGuardarFacturaCarpetaInexistente()
    {
        File archivo = carpetaTemporal.resolve("no/existe/factura.txt").toFile();
        assertThrows(FileNotFoundException.class, () -> pedido.guardarFactura(archivo), "debió lanzarse filenotfoundexception");
    }
}
