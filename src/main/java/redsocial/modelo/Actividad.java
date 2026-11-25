package redsocial.modelo;

import java.time.LocalTime;

/**
 * Actividad universitaria con horario y puntaje de interés.
 *
 * <p>Regla de compatibilidad del problema: dos actividades no se superponen si una finaliza antes o en
 * el mismo momento en que comienza la otra ({@code fin <= inicio}).</p>
 *
 * @param id      identificador de la actividad
 * @param nombre  nombre descriptivo de la actividad, no vacío
 * @param inicio  hora de inicio, no nula
 * @param fin     hora de finalización, estrictamente posterior a {@code inicio}
 * @param interes puntaje de interés entero no negativo (0 o más)
 */
public record Actividad(int id, String nombre, LocalTime inicio, LocalTime fin, int interes) {

    public Actividad {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("La actividad #" + id + " debe tener un nombre no vacío");
        }
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException(
                    "La actividad #" + id + " debe tener horas de inicio y finalización no nulas");
        }
        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La actividad #" + id
                    + " debe finalizar después de comenzar (" + inicio + " -> " + fin + ")");
        }
        if (interes < 0) {
            throw new IllegalArgumentException(
                    "La actividad #" + id + " debe tener un interés entero no negativo (recibido: " + interes + ")");
        }
    }

    /**
     * Indica si esta actividad es compatible con {@code otra} (no se superponen).
     * Una actividad puede comenzar exactamente cuando termina la otra.
     */
    public boolean esCompatibleCon(Actividad otra) {
        if (otra == null) {
            throw new IllegalArgumentException("La actividad a comparar no puede ser nula");
        }
        return !this.fin.isAfter(otra.inicio) || !otra.fin.isAfter(this.inicio);
    }
}
