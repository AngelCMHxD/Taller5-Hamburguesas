package uniandes.dpoo.hamburguesas.mundo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import uniandes.dpoo.hamburguesas.excepciones.HamburguesaException;
import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;

public class Restaurante
{
    private static final String CARPETA_FACTURAS = "./facturas/";

    private static final String PREFIJO_FACTURAS = "factura_";

    private ArrayList<Pedido> pedidos;

    private ArrayList<Ingrediente> ingredientes;

    private ArrayList<ProductoMenu> menuBase;

    private ArrayList<Combo> menuCombos;

    private Pedido pedidoEnCurso;

    public Restaurante( )
    {
        pedidos = new ArrayList<Pedido>( );
        ingredientes = new ArrayList<Ingrediente>( );
        menuBase = new ArrayList<ProductoMenu>( );
        menuCombos = new ArrayList<Combo>( );
    }

    public void iniciarPedido( String nombreCliente, String direccionCliente ) throws YaHayUnPedidoEnCursoException
    {
        if( pedidoEnCurso != null )
            throw new YaHayUnPedidoEnCursoException( pedidoEnCurso.getNombreCliente( ), nombreCliente );

        pedidoEnCurso = new Pedido( nombreCliente, direccionCliente );
    }

    public void cerrarYGuardarPedido( ) throws NoHayPedidoEnCursoException, IOException
    {
        if( pedidoEnCurso == null )
            throw new NoHayPedidoEnCursoException( );

        String nombreArchivo = PREFIJO_FACTURAS + pedidoEnCurso.getIdPedido( ) + ".txt";
        new File(CARPETA_FACTURAS).mkdirs();
        pedidoEnCurso.guardarFactura( new File( CARPETA_FACTURAS + nombreArchivo ) );
        pedidos.add(pedidoEnCurso);
        pedidoEnCurso = null;
    }

    public Pedido getPedidoEnCurso( )
    {
        return pedidoEnCurso;
    }

    public ArrayList<Pedido> getPedidos( )
    {
        return pedidos;
    }

    public Pedido getPedido(int id)
    {
        for (Pedido p : pedidos)
        {
            if (p.getIdPedido() == id)
            {
                return p;
            }
        }
        return null;
    }

    public ArrayList<ProductoMenu> getMenuBase( )
    {
        return menuBase;
    }

    public ArrayList<Combo> getMenuCombos( )
    {
        return menuCombos;
    }

    public ArrayList<Ingrediente> getIngredientes( )
    {
        return ingredientes;
    }

    public void cargarInformacionRestaurante( File archivoIngredientes, File archivoMenu, File archivoCombos ) throws HamburguesaException, NumberFormatException, IOException
    {
        cargarIngredientes( archivoIngredientes );
        cargarMenu( archivoMenu );
        cargarCombos( archivoCombos );
    }

    private void cargarIngredientes( File archivoIngredientes ) throws IngredienteRepetidoException, IOException
    {
        BufferedReader reader = new BufferedReader( new java.io.FileReader( archivoIngredientes ) );
        try
        {
            String linea = reader.readLine( );
            while( linea != null )
            {
                if( !linea.isEmpty( ) )
                {
                    String[] ingredientesStr = linea.split( ";" );
                    String nombreIngrediente = ingredientesStr[ 0 ];
                    int costoIngrediente = Integer.parseInt( ingredientesStr[ 1 ] );
                    Ingrediente ingrediente = new Ingrediente( nombreIngrediente, costoIngrediente );

                    for( Ingrediente i : this.ingredientes )
                    {
                        if( i.getNombre( ).equals( nombreIngrediente ) )
                        {
                            throw new IngredienteRepetidoException( nombreIngrediente );
                        }
                    }
                    this.ingredientes.add( ingrediente );
                }
                linea = reader.readLine( );
            }
        }
        catch( Exception e )
        {
            throw e;
        }
        finally
        {
            reader.close( );
        }
    }

    private void cargarMenu( File archivoMenu ) throws ProductoRepetidoException, IOException
    {
        BufferedReader reader = new BufferedReader( new java.io.FileReader( archivoMenu ) );
        try
        {
            String linea = reader.readLine( );
            while( linea != null )
            {
                if( !linea.isEmpty( ) )
                {
                    String[] productoStr = linea.split( ";" );
                    String nombreProducto = productoStr[ 0 ];
                    int costoProducto = Integer.parseInt( productoStr[ 1 ] );
                    ProductoMenu producto = new ProductoMenu( nombreProducto, costoProducto );

                    for( ProductoMenu prod : this.menuBase )
                    {
                        if( prod.getNombre( ).equals( nombreProducto ) )
                        {
                            throw new ProductoRepetidoException( nombreProducto );
                        }
                    }
                    this.menuBase.add( producto );
                }
                linea = reader.readLine( );
            }
        }
        catch( Exception e )
        {
            throw e;
        }
        finally
        {
            reader.close( );
        }
    }

    private void cargarCombos( File archivoCombos ) throws ProductoRepetidoException, ProductoFaltanteException, IOException
    {
        BufferedReader reader = new BufferedReader( new java.io.FileReader( archivoCombos ) );
        try
        {
            String linea = reader.readLine( );
            while( linea != null )
            {
                if( !linea.isEmpty( ) )
                {
                    String[] comboStr = linea.split( ";" );
                    String nombreCombo = comboStr[ 0 ];
                    double descuento = Double.parseDouble( comboStr[ 1 ].replace( "%", "" ) ) / 100;

                    for( Combo c : this.menuCombos )
                    {
                        if( c.getNombre( ).equals( nombreCombo ) )
                        {
                            throw new ProductoRepetidoException( nombreCombo );
                        }
                    }

                    ArrayList<ProductoMenu> itemsCombo = new ArrayList<>( );
                    for( int i = 2; i < comboStr.length; i++ )
                    {
                        String nombreProducto = comboStr[ i ];
                        ProductoMenu productoItem = null;

                        int index = 0;
                        boolean found = false;
                        while( index < this.menuBase.size( ) && !found )
                        {
                            if( this.menuBase.get( index ).getNombre( ).equals( nombreProducto ) )
                            {
                                productoItem = this.menuBase.get( index );
                                found = true;
                            }
                            index++;
                        }

                        if( productoItem == null )
                        {
                            throw new ProductoFaltanteException( nombreProducto );
                        }

                        itemsCombo.add( productoItem );
                    }

                    Combo combo = new Combo( nombreCombo, descuento, itemsCombo );
                    this.menuCombos.add( combo );
                }
                linea = reader.readLine( );
            }
        }
        catch( Exception e )
        {
            throw e;
        }
        finally
        {
            reader.close( );
        }
    }
}
