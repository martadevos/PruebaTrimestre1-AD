import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase que contiene los métodos que realizan las acciones del/los menús
 *
 * @author MartaDevós
 */

public class Telefono {

    /**
     * Método para llamar a un usuario, si no está en la agenda, lo añade
     */
    public static void llamar() {
        Scanner s = new Scanner(System.in);
        String resp, id, tipo, condicion;
        System.out.println("Introduce el nombre o el número del contacto para llamar.");
        resp = s.nextLine();

        if (resp.matches("[0-9]{9}")) {
            condicion = "telf LIKE '" + resp + "'";
        } else {
            condicion = "nombre LIKE '" + resp + "'";
        }
        try {
            if (!CRUD.buscarFiltrando("Agenda", "*", condicion).next()) {
                contactoNoExiste(resp);
            }
            ResultSet rs = CRUD.buscarFiltrando("Agenda", "*", condicion);
            rs.next();
            String[] contactoElegido = listaContactosCoincidentes(rs, resp);
            id = contactoElegido[0];
            resp = contactoElegido[1];

            long datetime = System.currentTimeMillis();
            Timestamp fecha = new Timestamp(datetime);
            tipo = llamada(resp);

            CRUD.insertar("Registro", "(idAgenda, fechaHora, tipo)", new String[]{id, "'" + fecha + "'", tipo});

        } catch (SQLException e) {
            System.out.println("Error: Clase Telefono, método llamar");
            e.printStackTrace();
        }
    }

    public static String llamada(String contacto) {
        Scanner s = new Scanner(System.in);
        String tipo;
        final long[] contMilisegundos = {0};
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                contMilisegundos[0] += 1;
            }
        }, 1000, 1);
        if (Math.random() > 0.2) {
            System.out.println("En llamada con " + contacto + ", pulse intro para acabar.");
            String llamada;
            do {
                llamada = s.next();
            } while (llamada == null);
            timer.cancel();
            timer.purge();
            System.out.println("Ha finalizado la llamada.\n" +
                    "Ha durado " + ((double) (contMilisegundos[0] / 1000) / 60) + " minutos");
            tipo = "'S'";
        } else {
            System.out.println("El numero marcado no se encuentra disponible.");
            tipo = "'P'";
        }
        return tipo;
    }

    public static void contactoNoExiste(String resp) {
        Scanner s = new Scanner(System.in);
        Pattern patron = Pattern.compile("[0-9]{9}");
        Matcher mat = patron.matcher(resp);
        if (mat.matches()) {
            System.out.println("El número introducido no está en la agenda.\n" +
                    "Introduzca un nombre para añadirlo.");
            CRUD.insertar("Agenda", "(nombre, telf)", new String[]{"'" + s.nextLine() + "'", "'" + resp + "'"});

        } else {
            System.out.println("El nombre introducido no existe.");
        }
    }

    /**
     * Método que muestra una lista de contactos que se encuentra en un ResultSet y pide que se elija 1
     */
    public static String[] listaContactosCoincidentes(ResultSet rs, String contacto) {
        Scanner s = new Scanner(System.in);
        ArrayList<String> agenda = new ArrayList<>();
        String[] contactoElegido;

        try {
            ResultSetMetaData md = rs.getMetaData();
            do {
                agenda.add(rs.getInt(md.getColumnLabel(1)) + "     " + rs.getString(md.getColumnLabel(2)) + "     " + rs.getString(md.getColumnLabel(3)));
            } while (rs.next());
            System.out.println("Se han encontrado " + agenda.size() + " registros con el nombre " + contacto + ".\n" +
                    "Introduzca el número entre 1 y " + agenda.size() + " correspondiente al contacto que desea llamar.");
            for (int i = 0; i < agenda.size(); i++) {
                System.out.println((i + 1) + ".- " + agenda.get(i));
            }
            int resp = s.nextInt();
            while (resp < 1 || resp > agenda.size()) {
                System.out.println("El número introducido no es correcto, introduzca uno entre 1 y " + agenda.size());
                resp = s.nextInt();
            }
            contactoElegido = agenda.get(resp - 1).split(" {5}");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contactoElegido;
    }

    /**
     * Método que genera una llamada aleatoria, si no está en la agenda, lo añade
     */
    public static void recibir() {
        Scanner s = new Scanner(System.in);
        StringBuilder num = new StringBuilder(String.valueOf((int) (Math.random() * (8 - 6) + 6)));
        String id = null, tipo = "E";

        for (int i = 1; i < 9; i++) {
            num.append((int) (Math.random() * (10 - 1) + 1));
        }

        System.out.println("Llamada entrante de " + num);
        try {
            if (!CRUD.buscarFiltrando("Agenda", "*", "telf LIKE '" + num + "'").next()) {
                System.out.println("El número no está en la agenda.\n" +
                        "Introduzca un nombre para añadirlo.");
                CRUD.insertar("Agenda", "(nombre, telf)", new String[]{"'" + s.nextLine() + "'", "'"+num+"'"});
            }
            ResultSet rs = CRUD.buscarFiltrando("Agenda", "*", "telf LIKE '" + num + "'");
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                id = String.valueOf(rs.getInt(md.getColumnLabel(1)));
            }
            long datetime = System.currentTimeMillis();
            Timestamp fecha = new Timestamp(datetime);
            llamada(String.valueOf(num));

            CRUD.insertar("Registro", "(idAgenda, fechaHora, tipo)", new String[]{id, "'" + fecha + "'", "'" + tipo + "'"});
        } catch (SQLException e) {
            System.out.println("Error: Clase Telefono, método recibir");
            e.printStackTrace();
        }
    }

    /**
     * Método que comprueba si hay registros de llamadas; si no hay muestra un mensaje informándolo, si sí hay, las lista
     */
    public static void mostrarRegistro() {
        ResultSet rs;

        rs = CRUD.buscar("Registro AS R JOIN ad2223_mdevos.Agenda AS A ON R.idAgenda=A.idAgenda", "A.nombre, R.fechaHora, R.tipo");
        try {
            if (!rs.next()) {
                System.out.println("-----No hay registros-----");
            } else {
                System.out.println("-----HISTORIAL DE LLAMADAS-----");
                CRUD.mostrarDatos(rs);
                System.out.println("-------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error: Clase Telefono, método recibir");
            e.printStackTrace();
        }
    }

    /**
     * Método que comprueba si hay registros de llamadas de un tipo; si no hay muestra un mensaje informándolo, si sí hay, las lista
     *
     * @param tipo String con el tipo de llamada a buscar
     */
    public static void mostrarRegistroFiltrado(String tipo) {
        ResultSet rs;

        rs = CRUD.buscarFiltrando("Registro AS R JOIN ad2223_mdevos.Agenda AS A ON R.idAgenda=A.idAgenda", "A.nombre, R.fechaHora, R.tipo", "R.tipo LIKE " + tipo);
        try {
            if (!rs.next()) {
                System.out.println("-----No hay registros-----");
            } else {
                System.out.println("-----HISTORIAL DE LLAMADAS-----");
                CRUD.mostrarDatos(rs);
                System.out.println("-------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error: Clase Telefono, método recibir");
            e.printStackTrace();
        }
    }
}
