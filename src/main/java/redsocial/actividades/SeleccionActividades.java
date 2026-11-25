package redsocial.actividades;

import java.util.ArrayList;
import java.util.List;
import redsocial.modelo.Actividad;

/**
 * Selección de actividades compatibles de máximo interés total (A3).
 *
 * <p><b>Estrategia: Programación Dinámica (planificación de intervalos ponderados / Weighted Interval
 * Scheduling).</b> Cada actividad tiene un horario {@code [inicio, fin]} y un puntaje de interés. Una
 * actividad puede comenzar exactamente cuando finaliza otra ({@code fin <= inicio}).</p>
 *
 * <p><b>Por qué NO sirve Greedy.</b> A diferencia de la selección de actividades sin pesos (donde elegir
 * siempre la que termina antes es óptimo), cuando cada actividad tiene un puntaje de interés distinto
 * ninguna heurística voraz garantiza el óptimo: elegir la que termina primero, la de mayor interés o la
 * de mayor razón interés/duración pueden bloquear combinaciones de mayor suma total.</p>
 *
 * <p><b>Por qué sí Programación Dinámica.</b> Una vez ordenadas las actividades por hora de finalización
 * creciente ({@code 1..n}), para cada actividad {@code i} se calcula {@code p[i]}: el índice de la última
 * actividad anterior compatible con {@code i} ({@code fin[j] <= inicio[i]}), o {@code 0} si no existe.</p>
 * <ul>
 *   <li><b>Subestructura óptima:</b> en la selección óptima entre las primeras {@code i} actividades, o bien
 *       se excluye la actividad {@code i} (reduciendo el problema a {@code 1..i-1}) o bien se la incluye
 *       junto con la selección óptima entre las actividades compatibles {@code 1..p[i]}.</li>
 *   <li><b>Subproblemas superpuestos:</b> distintos prefijos consultan repetidamente los mismos óptimos
 *       {@code dp[j]}; guardar cada resultado en un arreglo 1D evita el recálculo exponencial.</li>
 * </ul>
 *
 * <p><b>Recurrencia.</b> {@code dp[i]} = máximo interés alcanzable considerando las primeras {@code i}
 * actividades ordenadas por fin:</p>
 * <pre>
 *   dp[0] = 0
 *   dp[i] = max(dp[i - 1], interes[i] + dp[p[i]])    para 1 &lt;= i &lt;= n
 * </pre>
 *
 * <p><b>Reconstrucción.</b> Se recorre {@code dp} hacia atrás desde {@code i = n}: si
 * {@code dp[i] != dp[i - 1]}, la actividad {@code i} mejoró el óptimo y forma parte de la solución, por lo
 * que se la agrega y se salta a {@code p[i]}; en caso contrario se continúa en {@code i - 1}.</p>
 *
 * <p><b>Ordenamiento propio.</b> Para respetar la regla del proyecto de no usar métodos de ordenamiento del
 * JDK, las actividades se ordenan con un Merge Sort propio ({@code O(n log n)}).</p>
 *
 * <p><b>Complejidad.</b> Ordenar cuesta {@code O(n log n)}, calcular {@code p[i]} con búsqueda binaria
 * cuesta {@code O(n log n)}, llenar {@code dp} cuesta {@code O(n)} y reconstruir cuesta {@code O(n)}:
 * tiempo total <b>O(n log n)</b> y espacio auxiliar <b>O(n)</b>.</p>
 */
public final class SeleccionActividades {

    private SeleccionActividades() { }

    /**
     * Resultado de la selección óptima de actividades compatibles.
     *
     * @param seleccionadas actividades elegidas, ordenadas cronológicamente y sin superposiciones
     * @param interesTotal  suma de los puntajes de interés de las actividades elegidas (el óptimo)
     */
    public record Resultado(List<Actividad> seleccionadas, int interesTotal) { }

    /**
     * Calcula únicamente el máximo interés total alcanzable sin superposiciones.
     *
     * @param actividades actividades candidatas (no se modifica la lista recibida)
     * @return máximo interés total posible
     * @throws IllegalArgumentException si la lista es nula o contiene elementos nulos
     */
    public static int maximoInteres(List<Actividad> actividades) {
        validar(actividades);
        if (actividades.isEmpty()) {
            return 0;
        }
        Actividad[] ordenadas = ordenarPorFin(actividades);
        int[] p = calcularPredecesoresCompatibles(ordenadas);
        int[] dp = construirTabla(ordenadas, p);
        return dp[ordenadas.length];
    }

    /**
     * Selecciona un subconjunto de actividades compatibles que maximiza el interés total.
     *
     * @param actividades actividades candidatas (no se modifica la lista recibida)
     * @return actividades seleccionadas en orden cronológico y el interés total obtenido
     * @throws IllegalArgumentException si la lista es nula o contiene elementos nulos
     */
    public static Resultado seleccionar(List<Actividad> actividades) {
        validar(actividades);
        if (actividades.isEmpty()) {
            return new Resultado(List.of(), 0);
        }

        Actividad[] ordenadas = ordenarPorFin(actividades);
        int[] p = calcularPredecesoresCompatibles(ordenadas);
        int[] dp = construirTabla(ordenadas, p);

        // RECONSTRUCCIÓN: se recorre dp hacia atrás desde n hasta 1
        List<Actividad> enReversa = new ArrayList<>();
        int i = ordenadas.length;
        while (i > 0) {
            if (dp[i] != dp[i - 1]) {
                // la actividad i mejoró estrictamente el óptimo: se incluye y se salta a su predecesor compatible
                enReversa.add(ordenadas[i - 1]);
                i = p[i];
            } else {
                // sin la actividad i ya se alcanzaba el mismo interés: se la omite
                i--;
            }
        }

        // se invierte para devolver las actividades en orden cronológico
        List<Actividad> seleccionadas = new ArrayList<>(enReversa.size());
        for (int k = enReversa.size() - 1; k >= 0; k--) {
            seleccionadas.add(enReversa.get(k));
        }
        return new Resultado(List.copyOf(seleccionadas), dp[ordenadas.length]);
    }

    private static void validar(List<Actividad> actividades) {
        if (actividades == null) {
            throw new IllegalArgumentException("La lista de actividades no puede ser nula");
        }
        for (Actividad actividad : actividades) {
            if (actividad == null) {
                throw new IllegalArgumentException("La lista de actividades no puede contener elementos nulos");
            }
        }
    }

    /**
     * Llena el arreglo 1D de Programación Dinámica: {@code dp[i]} es el máximo interés con las primeras
     * {@code i} actividades (1-indexado).
     */
    private static int[] construirTabla(Actividad[] ordenadas, int[] p) {
        int n = ordenadas.length;
        // CASO BASE: dp[0] = 0 (sin actividades consideradas, el interés es 0)
        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            int sinElegir = dp[i - 1];
            int eligiendo = ordenadas[i - 1].interes() + dp[p[i]];
            // RECURRENCIA: quedarse con la mejor entre excluir o incluir la actividad i
            dp[i] = Math.max(sinElegir, eligiendo);
        }
        return dp;
    }

    /**
     * Para cada actividad {@code i} (1-indexada), calcula {@code p[i]}: el mayor índice {@code j < i} cuya
     * actividad termina antes o en el mismo momento en que comienza {@code i}, o {@code 0} si no existe.
     */
    private static int[] calcularPredecesoresCompatibles(Actividad[] ordenadas) {
        int n = ordenadas.length;
        int[] p = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            p[i] = ultimaCompatible(ordenadas, i - 1);
        }
        return p;
    }

    /**
     * Búsqueda binaria sobre {@code ordenadas[0..indice - 1]} (ordenadas por {@code fin} creciente) para
     * hallar la última actividad cuyo {@code fin <= ordenadas[indice].inicio()}. Devuelve su índice
     * 1-based, o {@code 0} si ninguna es compatible.
     */
    private static int ultimaCompatible(Actividad[] ordenadas, int indice) {
        // BÚSQUEDA BINARIA
        int izquierda = 0;
        int derecha = indice - 1;
        int respuesta = -1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            if (!ordenadas[medio].fin().isAfter(ordenadas[indice].inicio())) {
                respuesta = medio;
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }
        return respuesta + 1;
    }

    /** Devuelve una copia en arreglo ordenada por hora de fin creciente usando Merge Sort propio. */
    private static Actividad[] ordenarPorFin(List<Actividad> actividades) {
        Actividad[] datos = actividades.toArray(new Actividad[0]);
        Actividad[] auxiliar = new Actividad[datos.length];
        mergeSort(datos, auxiliar, 0, datos.length - 1);
        return datos;
    }

    private static void mergeSort(Actividad[] datos, Actividad[] auxiliar, int inicio, int fin) {
        if (inicio >= fin) {
            return;
        }
        int medio = inicio + (fin - inicio) / 2;
        mergeSort(datos, auxiliar, inicio, medio);
        mergeSort(datos, auxiliar, medio + 1, fin);
        if (comparar(datos[medio], datos[medio + 1]) <= 0) {
            return;
        }
        combinar(datos, auxiliar, inicio, medio, fin);
    }

    private static void combinar(Actividad[] datos, Actividad[] auxiliar, int inicio, int medio, int fin) {
        for (int k = inicio; k <= fin; k++) {
            auxiliar[k] = datos[k];
        }
        int izquierda = inicio;
        int derecha = medio + 1;
        for (int k = inicio; k <= fin; k++) {
            if (izquierda > medio) {
                datos[k] = auxiliar[derecha++];
            } else if (derecha > fin) {
                datos[k] = auxiliar[izquierda++];
            } else if (comparar(auxiliar[derecha], auxiliar[izquierda]) < 0) {
                datos[k] = auxiliar[derecha++];
            } else {
                datos[k] = auxiliar[izquierda++];
            }
        }
    }

    /** Orden determinista: fin creciente, luego inicio creciente, luego id creciente. */
    private static int comparar(Actividad a, Actividad b) {
        int porFin = a.fin().compareTo(b.fin());
        if (porFin != 0) {
            return porFin;
        }
        int porInicio = a.inicio().compareTo(b.inicio());
        if (porInicio != 0) {
            return porInicio;
        }
        return Integer.compare(a.id(), b.id());
    }
}
