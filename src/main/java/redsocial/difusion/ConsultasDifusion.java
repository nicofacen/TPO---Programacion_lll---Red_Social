package redsocial.difusion;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import redsocial.grafo.Grafo;

/**
 * Consultas de difusión entre <b>todos</b> los pares de usuarios (funcionalidad A1).
 *
 * <p><b>Estrategia: Programación Dinámica (Floyd-Warshall).</b> El subproblema es "mejor cadena de
 * {@code i} a {@code j} usando solo los usuarios {@code 0..k} como intermediarios". Se resuelve para
 * {@code k = -1} (sin intermediarios: solo los vínculos directos) y se amplía de a un usuario por vez:
 * la mejor cadena de {@code i} a {@code j} con intermediarios {@code 0..k} o bien no usa a {@code k}
 * (ya estaba calculada) o bien pasa por {@code k} y entonces es la mejor de {@code i} a {@code k} más
 * la mejor de {@code k} a {@code j}, ambas con intermediarios {@code 0..k-1}. Los resultados se
 * guardan en la matriz {@code dist}, que se reutiliza en cada paso.</p>
 *
 * <p><b>Por qué conviene aquí:</b> la consigna pide responder muchas consultas entre cualquier par de
 * usuarios. Repetir Dijkstra en cada consulta costaría O((V + E) log V) cada vez; con Floyd-Warshall
 * se paga O(V³) <b>una sola vez</b> (el procesamiento inicial, en el constructor) y cada consulta es
 * una lectura de la matriz, O(1). Con pocas consultas conviene Dijkstra; con muchas, esta técnica.</p>
 *
 * <p><b>Complejidad:</b> tiempo O(V³) para el procesamiento inicial (triple bucle) y espacio O(V²) para
 * las matrices. La instancia refleja la red <b>al momento de construirla</b>: si después se agregan
 * vínculos al {@link Grafo}, hay que crear una nueva instancia.</p>
 */
public final class ConsultasDifusion {

    /** "Infinito": la mitad del máximo para que sumar dos infinitos no desborde el {@code int}. */
    private static final int INF = Integer.MAX_VALUE / 2;

    private final Grafo grafo;
    private final int[][] dist;
    /** {@code siguiente[i][j]}: primer usuario después de {@code i} en la mejor cadena hacia {@code j} (-1 si no hay). */
    private final int[][] siguiente;

    /**
     * Procesamiento inicial: calcula el tiempo mínimo entre todos los pares de usuarios de la red.
     *
     * @param grafo red de difusión (se lee una sola vez, en este constructor)
     */
    public ConsultasDifusion(Grafo grafo) {
        this.grafo = grafo;
        int n = grafo.cantidadUsuarios();

        // CASO BASE: sin intermediarios, solo valen la distancia 0 a uno mismo y los vínculos directos
        dist = new int[n][n];
        siguiente = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = (i == j) ? 0 : INF;
                siguiente[i][j] = (i == j) ? i : -1;
            }
        }
        for (int u = 0; u < n; u++) {
            for (Grafo.Arista a : grafo.vecinos(u)) {
                dist[u][a.destino()] = a.minutos();
                siguiente[u][a.destino()] = a.destino();
            }
        }

        // AMPLIACIÓN: se habilita de a un usuario k como intermediario posible
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == INF) {
                    continue; // no se llega a k desde i: pasar por k no puede mejorar nada
                }
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == INF) {
                        continue; // no se llega a j desde k
                    }
                    int pasandoPorK = dist[i][k] + dist[k][j];
                    if (pasandoPorK < dist[i][j]) {
                        dist[i][j] = pasandoPorK; // RECURRENCIA: min(dist[i][j], dist[i][k] + dist[k][j])
                        siguiente[i][j] = siguiente[i][k]; // el primer paso hacia j ahora es el primero hacia k
                    }
                }
            }
        }
    }

    /**
     * Tiempo mínimo en minutos de la mejor cadena de {@code origen} a {@code destino}.
     * Es una lectura de la matriz precalculada: O(1), sin recalcular nada.
     *
     * @return el tiempo, o vacío si no existe ninguna cadena
     * @throws IllegalArgumentException si alguno de los usuarios no existe
     */
    public OptionalInt tiempoMinimo(String origen, String destino) {
        int d = dist[grafo.indiceDe(origen)][grafo.indiceDe(destino)];
        return d == INF ? OptionalInt.empty() : OptionalInt.of(d);
    }

    /**
     * Mejor cadena de {@code origen} a {@code destino}, reconstruida con la matriz {@code siguiente}:
     * se parte del origen y se avanza al "siguiente" usuario hasta llegar al destino. Costo O(L), con
     * L la cantidad de usuarios de la cadena.
     *
     * @return el resultado (con traza vacía) o {@code noAlcanzable} si no existe ninguna cadena
     * @throws IllegalArgumentException si alguno de los usuarios no existe
     */
    public ResultadoCamino consultar(String origen, String destino) {
        int i = grafo.indiceDe(origen);
        int j = grafo.indiceDe(destino);
        if (dist[i][j] == INF) {
            return ResultadoCamino.noAlcanzable(List.of());
        }
        List<String> cadena = new ArrayList<>();
        int actual = i;
        cadena.add(grafo.nombreDe(actual));
        while (actual != j) {
            actual = siguiente[actual][j];
            cadena.add(grafo.nombreDe(actual));
        }
        return new ResultadoCamino(true, dist[i][j], cadena, List.of());
    }

    /**
     * Matriz de tiempos mínimos como texto: una fila de encabezado y una fila por usuario, con cada
     * celda de 7 caracteres y {@code -} donde no existe cadena.
     */
    public String matrizComoTexto() {
        int n = dist.length;
        StringBuilder sb = new StringBuilder(String.format("%-7s", ""));
        for (int j = 0; j < n; j++) {
            sb.append(String.format("%-7s", grafo.nombreDe(j)));
        }
        sb.append('\n');
        for (int i = 0; i < n; i++) {
            sb.append(String.format("%-7s", grafo.nombreDe(i)));
            for (int j = 0; j < n; j++) {
                sb.append(String.format("%-7s", dist[i][j] == INF ? "-" : String.valueOf(dist[i][j])));
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
