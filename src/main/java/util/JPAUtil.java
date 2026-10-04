package util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {
	
	 private static final EntityManagerFactory fabrica =
	            Persistence.createEntityManagerFactory("mysqlconex");

	    private JPAUtil() {
	    }

	    public static EntityManager getEntityManager() {
	        return fabrica.createEntityManager();
	    }

	    public static void cerrar() {
	        if (fabrica.isOpen()) {
	            fabrica.close();
	        }
	    }
}
