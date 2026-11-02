package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest
{
    private ArrayList<ProductoMenu> items;
    private Combo combo1;

    @BeforeEach
    void setUp() throws Exception
    {
        items = new ArrayList<ProductoMenu>();
        items.add(new ProductoMenu("corral", 14000));
        items.add(new ProductoMenu("papas medianas", 5500));
        items.add(new ProductoMenu("gaseosa", 5000));
        combo1 = new Combo("combo corral", 0.1, items);
    }

    @Test
    void testGetNombre()
    {
        assertEquals("combo corral", combo1.getNombre(), "el nombre del combo no es el esperado");
    }

    @Test
    void testGetPrecio()
    {
        assertEquals(22050, combo1.getPrecio(), "el precio debe aplicar el descuento sobre la suma de los productos");
    }

    @Test
    void testGetPrecioSinDescuento()
    {
        Combo combo = new Combo("sin descuento", 0, items);
        assertEquals(24500, combo.getPrecio(), "sin descuento el precio debe ser la suma de los productos");
    }

    @Test
    void testGetPrecioSinItems()
    {
        Combo combo = new Combo("vacio", 0.1, new ArrayList<ProductoMenu>());
        assertEquals(0, combo.getPrecio(), "un combo sin productos debe costar 0");
    }

    @Test
    void testItemsCopiados()
    {
        items.add(new ProductoMenu("papas grandes", 6900));
        assertEquals(22050, combo1.getPrecio(), "modificar la lista no debe afectar al combo");
    }

    @Test
    void testGenerarTextoFactura()
    {
        String esperado = "Combo combo corral\n" + " Descuento: 0.1\n" + "            22050\n";
        assertEquals(esperado, combo1.generarTextoFactura(), "el texto de la factura no es el esperado");
    }
}
