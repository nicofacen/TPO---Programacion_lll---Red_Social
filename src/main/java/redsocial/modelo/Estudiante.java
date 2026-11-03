package redsocial.modelo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Estudiante de la red social con sus habilidades declaradas.
 *
 * <p>Las habilidades se guardan en un conjunto inmutable que conserva el orden de inserción, para que al
 * imprimirlas el orden sea siempre el mismo.</p>
 *
 * @param nombre      nombre del estudiante, no vacío (lo identifica dentro de un grupo)
 * @param habilidades habilidades declaradas, no nulas (puede estar vacío)
 */
public record Estudiante(String nombre, Set<String> habilidades) {

    public Estudiante {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del estudiante no puede estar vacío");
        }
        if (habilidades == null) {
            throw new IllegalArgumentException("Las habilidades de " + nombre + " no pueden ser nulas");
        }
        habilidades = Collections.unmodifiableSet(new LinkedHashSet<>(habilidades));
    }
}
