package redsocial.app;

import java.util.Scanner;

/**
 * Una opción del menú principal (una funcionalidad del sistema).
 *
 * <p>Cada funcionalidad tiene su propia clase {@code *Consola} que implementa esta interfaz
 * y se registra en {@link Main}.</p>
 */
public interface Opcion {

    /** Texto de una línea para el menú. Incluye la estrategia, por ejemplo "(Greedy: Dijkstra)". */
    String titulo();

    /**
     * Ejecuta la funcionalidad con datos de ejemplo. Toda lectura de datos del usuario debe hacerse con
     * {@link Entrada}, ofreciendo siempre un valor por defecto, para que el modo {@code --demo} funcione solo.
     */
    void ejecutar(Scanner in);
}
