package app;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DemoConexion {

    public static void main(String[] args) {

        EntityManagerFactory fabrica = null;
        EntityManager manager = null;

        try {

            fabrica = Persistence
                    .createEntityManagerFactory("mysqlconex");

            manager = fabrica.createEntityManager();

            System.out.println("==============================");
            System.out.println("CONEXION EXITOSA A CONTROLFIT");
            System.out.println("==============================");

        } catch (Exception e) {

            System.out.println("==============================");
            System.out.println("ERROR DE CONEXION");
            System.out.println("==============================");

            e.printStackTrace();

        } finally {

            if (manager != null) {
                manager.close();
            }

            if (fabrica != null) {
                fabrica.close();
            }
        }
    }
}