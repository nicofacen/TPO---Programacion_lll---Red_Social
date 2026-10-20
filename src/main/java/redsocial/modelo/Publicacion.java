package redsocial.modelo;

import java.time.LocalDateTime;

/**
 * Publicación del muro de la red social.
 *
 * <p>La consigna NO define qué es la "relevancia" de una publicación. El equipo adoptó la fórmula
 * {@code reacciones + 2 x comentarios}, porque un comentario implica más esfuerzo que una reacción.
 * Para cambiar el criterio alcanza con tocar esta clase ({@link #PESO_COMENTARIO} o {@link #relevancia()}).</p>
 *
 * @param id         identificador único (se usa como último desempate)
 * @param autor      nombre del autor, no vacío
 * @param fechaHora  momento de publicación
 * @param reacciones cantidad de reacciones, no negativa
 * @param comentarios cantidad de comentarios, no negativa
 */
public record Publicacion(int id, String autor, LocalDateTime fechaHora, int reacciones, int comentarios) {

    /** Cuántas reacciones vale un comentario en el puntaje de relevancia. */
    public static final int PESO_COMENTARIO = 2;

    public Publicacion {
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El autor no puede estar vacío");
        }
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora no pueden ser nulas");
        }
        if (reacciones < 0 || comentarios < 0) {
            throw new IllegalArgumentException("Las reacciones y los comentarios no pueden ser negativos");
        }
    }

    /** Puntaje de relevancia: {@code reacciones + PESO_COMENTARIO x comentarios}. */
    public int relevancia() {
        return reacciones + PESO_COMENTARIO * comentarios;
    }
}
