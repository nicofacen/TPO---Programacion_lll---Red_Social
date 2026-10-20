package redsocial.difusion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import redsocial.grafo.Grafo;

/**
 * Funcionalidad 2 — Cadena de difusión más rápida entre dos usuarios.
 *
 * <p><b>Estrategia: Greedy (algoritmo de Dijkstra).</b> Se arranca con el origen en 0 minutos y el
 * resto en infinito. En cada vuelta se toma al usuario <i>no fijado</i> con el menor tiempo acumulado
 * y se lo <b>fija</b> (esa es la decisión voraz: ese tiempo ya es el definitivo); después se
 * <b>relajan</b> sus vínculos salientes, es decir, si llegar por él mejora el tiempo de un vecino se
 * actualiza ese tiempo y se anota por quién se llegó. Cuando se fija el destino, la cadena se
 * reconstruye hacia atrás con los predecesores.</p>
 *
 * <p><b>Por qué la decisión voraz es correcta.</b> Todos los tiempos de los vínculos son
 * <i>positivos</i> (lo exige la consigna). Entonces, si {@code u} es el usuario no fijado con menor
 * tiempo acumulado, ninguna otra cadena puede llegar a {@code u} más rápido: cualquier cadena
 * alternativa tendría que pasar por otro usuario no fijado, que ya arranca con un tiempo mayor o
 * igual, y sumarle minutos positivos. Por eso la elección local nunca hay que revisarla y no se
 * necesita explorar todas las cadenas. Con tiempos negativos esto dejaría de valer y habría que usar
 * Bellman-Ford.</p>
 *
 * <p><b>Alternativas descartadas:</b> un recorrido en amplitud encontraría la cadena con menos saltos,
 * no la más rápida (ignora los minutos); probar todas las cadenas posibles es factorial; Bellman-Ford
 * es O(V·E), más lento sin necesidad porque acá no hay tiempos negativos.</p>
 *
 * <p><b>Nota sobre la cola de prioridad.</b> El {@link Comparator} que recibe la
 * {@link PriorityQueue} no ordena ninguna colección: solo le dice al <i>heap</i> binario cuál de dos
 * candidatos es el más barato, para poder entregar el mínimo en O(log V). El ordenamiento que la
 * consigna exige escribir a mano es el del muro de publicaciones, que no usa nada del JDK.</p>
 *
 * <p><b>Complejidad temporal: O((V + E) log V).</b> Cada usuario se fija una sola vez y cada vínculo
 * se relaja a lo sumo una vez; cada relajación encola una entrada, así que la cola llega a tener
 * O(V + E) entradas y cada {@code poll} / {@code add} cuesta O(log(V + E)) = O(log V). Espacio
 * O(V + E).</p>
 */
public final class CadenaMasRapida {

    /** Tiempo "infinito": todavía no se encontró ninguna cadena hasta ese usuario. */
    private static final int INFINITO = Integer.MAX_VALUE;

    private CadenaMasRapida() { }

    /**
     * Calcula la cadena de difusión de menor tiempo total entre dos usuarios.
     *
     * @param grafo   red de difusión
     * @param origen  usuario que inicia la convocatoria
     * @param destino usuario al que hay que llegar
     * @return la cadena, sus usuarios y el tiempo total; o un resultado no alcanzable si no existe
     * @throws IllegalArgumentException si alguno de los dos usuarios no está en la red
     */
    public static ResultadoCamino calcular(Grafo grafo, String origen, String destino) {
        int inicio = grafo.indiceDe(origen);
        int fin = grafo.indiceDe(destino);
        int cantidad = grafo.cantidadUsuarios();

        // Tiempo acumulado mínimo conocido hasta cada usuario, por quién se llegó y si ya es definitivo.
        int[] dist = new int[cantidad];
        int[] predecesor = new int[cantidad];
        boolean[] fijado = new boolean[cantidad];
        for (int i = 0; i < cantidad; i++) {
            dist[i] = INFINITO;
            predecesor[i] = -1;
        }
        dist[inicio] = 0;

        // Cola de prioridad {tiempo acumulado, usuario}: siempre entrega el candidato más barato.
        PriorityQueue<int[]> candidatos = new PriorityQueue<>(Comparator.comparingInt(entrada -> entrada[0]));
        candidatos.add(new int[] {0, inicio});

        while (!candidatos.isEmpty()) {
            int[] actual = candidatos.poll();
            int u = actual[1];
            if (fijado[u]) {
                continue; // entrada obsoleta: ese usuario ya se fijó con un tiempo menor
            }

            // DECISIÓN GREEDY: de los usuarios no fijados, el de menor tiempo acumulado. Como todos los
            // vínculos tienen tiempo positivo, ese tiempo ya es el mínimo definitivo y no se revisa más.
            fijado[u] = true;

            if (u == fin) {
                break; // ya se fijó el destino: su tiempo es definitivo, no hace falta seguir
            }

            for (Grafo.Arista vinculo : grafo.vecinos(u)) {
                int v = vinculo.destino();
                if (fijado[v]) {
                    continue;
                }
                int tiempoPorU = dist[u] + vinculo.minutos();
                // RELAJACIÓN: si llegar a v pasando por u es más rápido que lo mejor conocido, se mejora.
                if (tiempoPorU < dist[v]) {
                    dist[v] = tiempoPorU;
                    predecesor[v] = u;
                    candidatos.add(new int[] {tiempoPorU, v});
                }
            }
        }

        if (dist[fin] == INFINITO) {
            return ResultadoCamino.noAlcanzable(List.of());
        }
        return new ResultadoCamino(true, dist[fin], reconstruirCadena(grafo, predecesor, fin), List.of());
    }

    /**
     * Arma la cadena siguiendo los predecesores desde el destino hacia el origen y la da vuelta.
     * Cuesta O(L), con L la cantidad de usuarios de la cadena.
     */
    private static List<String> reconstruirCadena(Grafo grafo, int[] predecesor, int fin) {
        List<String> alReves = new ArrayList<>();
        for (int actual = fin; actual != -1; actual = predecesor[actual]) {
            alReves.add(grafo.nombreDe(actual));
        }
        List<String> cadena = new ArrayList<>(alReves.size());
        for (int i = alReves.size() - 1; i >= 0; i--) {
            cadena.add(alReves.get(i));
        }
        return cadena;
    }
}
