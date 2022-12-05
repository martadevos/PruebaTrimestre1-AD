import java.util.Scanner;

/**
 * Clase para mostrar el/los menús
 *
 * @author MartaDevós
 */

public class Menu {

    /**
     * Método que escribe las opciones del menú en pantalla
     */
    public static void mostrarMenu() {
        System.out.println("Escriba el número correspondiente a la opción elegida:" +
                "\n 1. Llamar" +
                "\n 2. Recibir" +
                "\n 3. Ver registro" +
                "\n 4. Ver historial lamadas entrantes" +
                "\n 5. Ver historial lamadas salientes" +
                "\n 6. Salir");
    }

    /**
     * Método que llama al método mostrarMenu para poner por pantalla las opciones,
     * recoge y verifica la elegida por el usuario
     *
     * @return boolean que indica si el usuario quiere seguir usando la app o si desea salir
     */
    public static boolean menu() {
        Scanner s = new Scanner(System.in);
        int opc;
        boolean seguir = true;
        do {
            mostrarMenu();
            opc = s.nextInt();
        }while (!verificarEleccionMenu(opc));
        switch (opc) {
            case 1 -> Telefono.llamar();
            case 2 -> Telefono.recibir();
            case 3 -> Telefono.mostrarRegistro();
            case 4 -> Telefono.mostrarRegistroFiltrado("'E'");
            case 5 -> Telefono.mostrarRegistroFiltrado("'S'");
            case 6 -> seguir = false;
        }
        return seguir;
    }

    /**
     * Método que comprueba la eleccion del menú
     *
     * @param eleccionUsuario int con la elección del usuario
     *
     * @return boolean true si es correcta, false si es incorrecta
     * */
    public static boolean verificarEleccionMenu(int eleccionUsuario) {
        return eleccionUsuario > 0 && eleccionUsuario < 7;
    }

}
