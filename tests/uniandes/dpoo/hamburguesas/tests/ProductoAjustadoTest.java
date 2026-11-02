package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest
{
    private ProductoMenu base;
    private ProductoAjustado ajustado;
    private Ingrediente queso;
    private Ingrediente tomate;

    @BeforeEach
    void setUp() throws Exception
    {
        base = new ProductoMenu("corral", 14000);
        ajustado = new ProductoAjustado(base);
        queso = new Ingrediente("queso", 2500);
        tomate = new Ingrediente("tomate", 1000);
    }

    @Test
    void testGetNombre()
    {
        assertEquals("corral", ajustado.getNombre(), "el nombre debe ser el del producto");
    }

    @Test
    void testGetPrecioSinAjustes()
    {
        assertEquals(14000, ajustado.getPrecio(), "sin ajustes el precio debe ser el del producto");
    }

    @Test
    void testGetPrecioConAgregados()
    {
        ajustado.agregarIngrediente(queso);
        ajustado.agregarIngrediente(queso);
        ajustado.agregarIngrediente(tomate);
        assertEquals(14000 + 2500 + 2500 + 1000, ajustado.getPrecio(), "el precio debe ser la sumatoria de cada ingrediente");
    }

    @Test
    void testGetPrecioConEliminados()
    {
        ajustado.eliminarIngrediente(tomate);
        assertEquals(14000, ajustado.getPrecio(), "eliminar ingredientes no debe cambiar el precio");
    }

    @Test
    void testGenerarTextoFacturaSinAjustes()
    {
        String esperado = "corral\n" + "            14000\n";
        assertEquals(esperado, ajustado.generarTextoFactura(), "el texto de la factura no es el esperado");
    }

    @Test
    void testGenerarTextoFacturaConAjustes()
    {
        ajustado.agregarIngrediente(queso);
        ajustado.eliminarIngrediente(tomate);
        String esperado = "corral\n" + "    +queso                2500\n" + "    -tomate\n" + "            16500\n";
        assertEquals(esperado, ajustado.generarTextoFactura(), "el texto de la factura no es el esperado");
    }
}
