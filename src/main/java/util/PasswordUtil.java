package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private PasswordUtil() {
    }

    public static String generarHash(String clave) {

        if (clave == null || clave.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña no puede estar vacía.");
        }

        return BCrypt.hashpw(clave, BCrypt.gensalt());
    }

    public static boolean verificar(String clave, String hash) {

        if (clave == null || clave.isBlank()
                || hash == null || hash.isBlank()) {
            return false;
        }

        try {
            return BCrypt.checkpw(clave, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}