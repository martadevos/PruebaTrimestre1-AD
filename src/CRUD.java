import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Clase que almacena los métodos para interaccionar con la base de datos
 *
 * @author MartaDevós
 */

public class CRUD {

    private static Connection con = null;
    private static Statement st = null;

    private static String password = "1234";
    private static String usuario = "ad2223_mdevos";
    private static String servidor = "jdbc:mysql://dns11036.phdns11.es";

    /**
     * Método que conecta con la base de datos
     */
    public static void conectar() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(servidor, usuario, password);
            if (con != null) {
                st = con.createStatement();
                System.out.println("Se ha conectado correctamente");
            }
        } catch (SQLException e) {
            System.out.println("No se pudo conectar");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Clase CRUD, método conectar");
            e.printStackTrace();
        }
    }

    /**
     * Método que crea las tablas
     */
    public static void crearTablas() {
        String tablaAgenda = "CREATE TABLE ad2223_mdevos.Agenda(" +
                "idAgenda int AUTO_INCREMENT PRIMARY KEY, " +
                "nombre varchar(100), " +
                "telf varchar(9) );";
                             /*Pone que lo llamemos tfno, pero le pongo telf
                               para evitar equivocarme al escribirlo luego*/

        String tablaRegistro = "CREATE TABLE ad2223_mdevos.Registro(" +
                "idRegistro int AUTO_INCREMENT PRIMARY KEY, " +
                "idAgenda int, " +
                "fechaHora timestamp, " +
                "tipo char," +
                "FOREIGN KEY (idAgenda) REFERENCES Agenda(idAgenda) on delete cascade on update cascade);";

        try {
            st.executeUpdate(tablaAgenda);
            st.executeUpdate(tablaRegistro);
        } catch (SQLException e) {
            System.out.println("Error: Clase CRUD, método crearTablas");
            e.printStackTrace();
        }
    }

    /**
     * Método que lee e inserta los registros de un fichero txt
     */
    public static void leerInsertar() {
        String s;
        boolean seguir = true;
        String rutaFichero = "src/agenda.txt";
        ArrayList<String> nombres = new ArrayList<>();
        ArrayList<String> numeros = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(rutaFichero));
            while (seguir) {
                s = br.readLine();
                if (s != null) {
                    nombres.add("'"+s+"'");
                } else {
                    seguir = false;
                }
                s = br.readLine();
                if (s != null) {
                    numeros.add("'"+s+"'");
                } else {
                    seguir = false;
                }
            }
            br.close();
            for (int i = 0; i < nombres.size() && i < numeros.size(); i++) {
                insertar("Agenda", "(nombre, telf)",new String[]{nombres.get(i), numeros.get(i)});
            }
        } catch (IOException e) {
            System.out.println("Error: Clase CRUD, método leerInsertar");
            e.printStackTrace();
        }

    }

    /**
     * Método para eliminar las tablas
     *
     * @param tablaEliminar String del nombre de la tabla que se quiere eliminar
     */
    public static void eliminarTabla(String tablaEliminar) {
        try {
            st.executeUpdate("DROP TABLE IF EXISTS ad2223_mdevos." + tablaEliminar + ";");
        } catch (SQLException e) {
            System.out.println("Error: Clase CRUD, método eliminarTabla para la tabla: " + tablaEliminar);
            e.printStackTrace();
        }
    }

    /**
     * Método para insertar datos en la tabla introducida
     *
     * @param tablaInsertar String del nombre de la tabla en la que se desean insertar datos
     * @param datos         Array String con los datos a insertar en la tabla
     */
    public static void insertar(String tablaInsertar, String campos, String[] datos) {
        StringBuilder sql = new StringBuilder("INSERT INTO ad2223_mdevos." + tablaInsertar+ " " + campos +" values(");

        for (int i = 0; i < datos.length-1; i++) {
            sql.append(datos[i]).append(", ");
        }
        sql.append(datos[datos.length-1]).append(");");
        try {
            st.executeUpdate(sql.toString());
        } catch (SQLException e) {
            System.out.println("Error: Clase CRUD, método insertar para la tabla: " + tablaInsertar);
            e.printStackTrace();
        }
    }

    /**
     * Método para listar todos los registros de una tabla
     *
     * @param tablaListar     String del nombre de la tabla que se quiere listar
     * @param datosParaListar String con los datos que se desean listar
     * @return ResultSet con los registros coincidentes en la base de datos
     */
    public static ResultSet buscar(String tablaListar, String datosParaListar) {
        ResultSet rs = null;
        try {
            rs = st.executeQuery("SELECT " + datosParaListar + " FROM ad2223_mdevos." + tablaListar);
        } catch (SQLException e) {
            System.out.println("Error: Clase CRUD, método listar para la tabla: " + tablaListar);
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * Método que devuelve todos los datos de la tabla encontrados que cumplan
     * con la condición introducida
     *
     * @param tablaListar     String del nombre de la tabla que se quiere listar
     * @param datosParaListar String con los datos que se desean listar
     * @param condicion       String con la condición del filtrado
     * @return ResultSet con los registros coincidentes en la base de datos
     */
    public static ResultSet buscarFiltrando(String tablaListar, String datosParaListar, String condicion) {
        ResultSet rs = null;
        try {
            rs = st.executeQuery("SELECT " + datosParaListar + " FROM ad2223_mdevos." + tablaListar + " WHERE " + condicion);
        } catch (SQLException e) {
            System.out.println("Error: Clase CRUD, método buscarFiltrando para la tabla: " + tablaListar);
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * Método que recibe un ResultSet y lo mustra con formato
     *
     * @param rs ResultSet que se desea mostrar por pantalla
     */
    public static void mostrarDatos(ResultSet rs) {
        try {
            ResultSetMetaData md = rs.getMetaData();
            do{
                StringBuilder cadena = new StringBuilder();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    cadena.append(rs.getString(md.getColumnLabel(i))).append("    ");
                }
                System.out.println(cadena);
            }while (rs.next());
        } catch (SQLException e) {
            System.out.println("Error: Clase CRUD, método mostrarDatos");
            e.printStackTrace();
        }
    }
}
