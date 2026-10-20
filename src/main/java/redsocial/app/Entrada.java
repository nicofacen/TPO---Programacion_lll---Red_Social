package redsocial.app;

import java.util.Scanner;

/**
 * Lectura de datos por consola con valor por defecto.
 *
 * <p>Si el usuario presiona Enter sin escribir nada se usa el valor por defecto. Así el modo
 * {@code --demo} puede ejecutar todo el sistema alimentándolo con líneas vacías.</p>
 */
public final class Entrada {

    private Entrada() { }

    /** Muestra "mensaje [defecto]: " y devuelve la línea leída (sin espacios extremos) o el valor por defecto. */
    public static String leer(Scanner in, String mensaje, String defecto) {
        System.out.print(mensaje + (defecto.isEmpty() ? "" : " [" + defecto + "]") + ": ");
        String linea = in.nextLine();
        return linea.isBlank() ? defecto : linea.trim();
    }

    /** Igual que {@link #leer} pero devuelve un entero; si no es un número lanza {@link IllegalArgumentException}. */
    public static int leerEntero(Scanner in, String mensaje, int defecto) {
        String texto = leer(in, mensaje, String.valueOf(defecto));
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + texto + "' no es un número entero");
        }
    }
}
