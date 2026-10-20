package redsocial.muro;

import redsocial.modelo.Publicacion;

/**
 * Criterio de orden del muro. Es una interfaz propia: no se usa {@code java.util.Comparator}
 * ni nada del JDK que ordene, porque la consigna exige un ordenamiento propio.
 *
 * <p>Las reglas de desempate de la consigna se aplican en cada constante:</p>
 * <ul>
 *   <li>{@link #MAS_RECIENTES}: fecha y hora descendente; si empatan, {@code id} menor primero.</li>
 *   <li>{@link #MAS_RELEVANTES}: {@link Publicacion#relevancia()} descendente; si empatan, la más
 *       reciente primero; si vuelven a empatar, {@code id} menor primero.</li>
 * </ul>
 */
@FunctionalInterface
public interface CriterioMuro {

    /**
     * Compara dos publicaciones.
     *
     * @return negativo si {@code a} debe aparecer ANTES que {@code b}, positivo si debe aparecer
     *         después y 0 si son equivalentes
     */
    int comparar(Publicacion a, Publicacion b);

    /** Más nueva primero; desempate por id menor. */
    CriterioMuro MAS_RECIENTES = (a, b) -> {
        int porFecha = b.fechaHora().compareTo(a.fechaHora());
        if (porFecha != 0) {
            return porFecha;
        }
        return Integer.compare(a.id(), b.id());
    };

    /** Mayor relevancia primero; desempate por más reciente y luego por id menor. */
    CriterioMuro MAS_RELEVANTES = (a, b) -> {
        int porRelevancia = Integer.compare(b.relevancia(), a.relevancia());
        if (porRelevancia != 0) {
            return porRelevancia;
        }
        return MAS_RECIENTES.comparar(a, b);
    };
}
