package redsocial.difusion;

import java.util.List;

/**
 * Resultado de buscar la cadena de difusión más rápida entre dos usuarios.
 *
 * <p>Es un {@code record} <b>inmutable</b>: el constructor compacto copia las dos listas con
 * {@link List#copyOf}, así quien recibe el resultado no puede modificarlo ni verlo cambiar.</p>
 *
 * @param alcanzable  {@code false} si no existe ninguna cadena de difusión entre los dos usuarios
 *                    (en ese caso {@code tiempoTotal} es 0 y {@code cadena} está vacía)
 * @param tiempoTotal suma de los minutos de los vínculos de la cadena; 0 si el origen es el destino
 * @param cadena      usuarios de la cadena en orden de transmisión, del origen al destino
 * @param traza       decisiones principales que tomó el cálculo, una por línea, para poder
 *                    mostrárselas al usuario (lo que la consigna pide "visualizar"); puede estar vacía
 */
public record ResultadoCamino(boolean alcanzable, int tiempoTotal, List<String> cadena, List<String> traza) {

    public ResultadoCamino {
        cadena = List.copyOf(cadena);
        traza = List.copyOf(traza);
    }

    /**
     * Resultado para el caso en que el destino no es alcanzable desde el origen: sin cadena y con
     * tiempo 0, conservando la traza de las decisiones que se alcanzaron a tomar.
     */
    public static ResultadoCamino noAlcanzable(List<String> traza) {
        return new ResultadoCamino(false, 0, List.of(), traza);
    }
}
