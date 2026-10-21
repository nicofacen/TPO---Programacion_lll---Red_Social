package redsocial.financiamiento;

import redsocial.modelo.Iniciativa;

import java.util.List;

/**
 * Selección de iniciativas a financiar con un presupuesto limitado.
 *
 * <p><b>Estrategia: Programación Dinámica (problema de la mochila 0/1).</b> Cada iniciativa se financia
 * completa o no se financia (no se puede fraccionar ni repetir), y se busca el mayor impacto total sin
 * superar el presupuesto.</p>
 *
 * <p><b>Por qué NO sirve Greedy.</b> Elegir siempre la iniciativa con mejor razón impacto/costo falla
 * porque la iniciativa no se puede fraccionar. Ejemplo con presupuesto 50 y (costo, impacto) =
 * (10, 60), (20, 100), (30, 120): las razones son 6, 5 y 4, así que Greedy toma las dos primeras
 * (costo 30, impacto 160) y la tercera ya no entra. El óptimo es elegir la segunda y la tercera
 * (costo 50, impacto 220).</p>
 *
 * <p><b>Por qué sí Programación Dinámica.</b></p>
 * <ul>
 *   <li><b>Subestructura óptima:</b> la mejor selección con las primeras {@code i} iniciativas y
 *       presupuesto {@code w} se arma a partir de la mejor selección con las primeras {@code i-1}
 *       (con {@code w} o con {@code w - costo}).</li>
 *   <li><b>Subproblemas superpuestos:</b> el mismo par (iniciativas consideradas, presupuesto) se
 *       necesita muchas veces; la tabla lo calcula una sola vez y lo reutiliza.</li>
 * </ul>
 *
 * <p><b>Tabla.</b> {@code dp[i][w]} = máximo impacto usando solo las primeras {@code i} iniciativas con
 * presupuesto {@code w}. La fila 0 (ninguna iniciativa) es toda 0. Recurrencia:</p>
 * <pre>
 *   dp[i][w] = dp[i-1][w]                                              (no financiar la i)
 *   dp[i][w] = max(dp[i-1][w], dp[i-1][w - costo_i] + impacto_i)       (si costo_i &lt;= w)
 * </pre>
 *
 * <p><b>Complejidad.</b> Con {@code n} iniciativas y presupuesto {@code P}, la tabla tiene
 * {@code (n+1) x (P+1)} celdas y cada una se calcula en O(1): tiempo <b>O(n · P)</b> y espacio
 * <b>O(n · P)</b>. Es <i>pseudopolinomial</i>: depende del valor numérico de {@code P}, no de la
 * cantidad de bits necesarios para escribirlo.</p>
 */
public final class SeleccionIniciativas {

    /** Máximo de celdas permitido para la tabla, para no agotar la memoria con presupuestos enormes. */
    private static final long MAXIMO_CELDAS = 100_000_000L;

    private SeleccionIniciativas() { }

    /**
     * Calcula el mayor impacto total posible sin superar el presupuesto.
     *
     * @param iniciativas iniciativas candidatas (cada una se puede financiar una sola vez)
     * @param presupuesto presupuesto disponible, no negativo
     * @return el máximo impacto alcanzable
     * @throws IllegalArgumentException si la lista es nula, hay una iniciativa nula, el presupuesto es
     *                                  negativo o la tabla resultante sería demasiado grande
     */
    public static int maximoImpacto(List<Iniciativa> iniciativas, int presupuesto) {
        int[][] dp = construirTabla(iniciativas, presupuesto);
        return dp[iniciativas.size()][presupuesto];
    }

    /**
     * Llena la tabla de programación dinámica de la mochila 0/1.
     *
     * @return tabla {@code dp} de {@code (n+1) x (presupuesto+1)}; {@code dp[n][presupuesto]} es el óptimo
     */
    private static int[][] construirTabla(List<Iniciativa> iniciativas, int presupuesto) {
        // VALIDACIONES
        if (iniciativas == null) {
            throw new IllegalArgumentException("La lista de iniciativas no puede ser nula");
        }
        for (Iniciativa iniciativa : iniciativas) {
            if (iniciativa == null) {
                throw new IllegalArgumentException("La lista de iniciativas no puede contener elementos nulos");
            }
        }
        if (presupuesto < 0) {
            throw new IllegalArgumentException("El presupuesto no puede ser negativo (recibido: " + presupuesto + ")");
        }
        int n = iniciativas.size();
        long celdas = (long) (n + 1) * (presupuesto + 1L);
        if (celdas > MAXIMO_CELDAS) {
            throw new IllegalArgumentException("La tabla tendría " + celdas + " celdas y el máximo permitido es "
                    + MAXIMO_CELDAS + ": reducí el presupuesto o la cantidad de iniciativas");
        }

        // CASO BASE: la fila 0 (ninguna iniciativa considerada) es toda 0, que es el valor inicial del arreglo
        int[][] dp = new int[n + 1][presupuesto + 1];

        for (int i = 1; i <= n; i++) {
            Iniciativa iniciativa = iniciativas.get(i - 1);
            int costo = iniciativa.costo();
            int impacto = iniciativa.impacto();
            for (int w = 0; w <= presupuesto; w++) {
                // RECURRENCIA: no financiar la iniciativa i (copiar la celda de arriba)...
                dp[i][w] = dp[i - 1][w];
                // ...o financiarla (si entra): su impacto + lo mejor con el presupuesto que queda
                if (costo <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - costo] + impacto);
                }
            }
        }
        return dp;
    }
}
