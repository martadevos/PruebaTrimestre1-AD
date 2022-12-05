/**
 * Clase que ejecuta el main
 *
 * @author MartaDevós
 *
 */

public class Main {
    public static void main(String[] args) {
        CRUD.conectar();
        //CRUD.crearTablas();
        //CRUD.leerInsertar();
        boolean seguir;
        do {
            seguir = Menu.menu();
        }while (seguir);

        System.out.println("Gracias por usar la app Phone.\n¡Hasta la proxima! :)");
    }
}