package uniandes.dpoo.hamburguesas.mundo;

import java.util.ArrayList;

public class Combo implements Producto
{
    private ArrayList<ProductoMenu> itemsCombo;

    private double descuento;

    private String nombreCombo;

    public Combo( String nombre, double descuento, ArrayList<ProductoMenu> items )
    {
        this.itemsCombo = new ArrayList<>( items );
        this.nombreCombo = nombre;
        this.descuento = descuento;
    }

    @Override
    public String getNombre( )
    {
        return nombreCombo;
    }

    @Override
    public int getPrecio( )
    {
        double precio = 0;
        for( Producto i : itemsCombo )
        {
            precio += i.getPrecio( );
        }

        return (int) (precio * (1 - descuento));
    }

    @Override
    public String generarTextoFactura( )
    {
        StringBuffer sb = new StringBuffer( );
        sb.append( "Combo " + nombreCombo + "\n" );
        sb.append( " Descuento: " + descuento + "\n" );
        sb.append( "            " + getPrecio( ) + "\n" );

        return sb.toString( );
    }
}
