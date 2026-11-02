package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoMenuTest
{
    private ProductoMenu producto1;

    @BeforeEach
    void setUp() throws Exception
    {
        producto1 = new ProductoMenu("corral", 14000);
    }

    @Test
    void testGetNombre()
    {
        assertEquals("corral", producto1.getNombre(), "el nombre del producto no es el esperado");
    }

    @Test
    void testGetPrecio()
    {
        assertEquals(14000, producto1.getPrecio(), "el precio del producto no es el esperado");
    }

    @Test
    void testGenerarTextoFactura()
    {
        String esperado = "corral\n" + "            14000\n";
        assertEquals(esperado, producto1.generarTextoFactura(), "el texto de la factura no es el esperado");
    }
}
