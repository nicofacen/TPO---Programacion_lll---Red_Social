package redsocial.app;

import java.util.Scanner;
import redsocial.difusion.CadenaMasRapida;
import redsocial.difusion.ResultadoCamino;
import redsocial.grafo.Grafo;

/**
 * Opción 2 del menú: cadena de difusión más rápida entre dos usuarios (Greedy: Dijkstra).
 *
 * <p>Pide origen y destino (con valores por defecto para que el modo {@code --demo} corra solo),
 * muestra las decisiones que tomó el cálculo y después la cadena con su tiempo total.</p>
 */
public final class CadenaRapidaConsola implements Opcion {

    @Override
    public String titulo() {
        return "Cadena de difusión más rápida (Greedy: Dijkstra)";
    }

    @Override
    public void ejecutar(Scanner in) {
        Grafo red = DatosRed.red();
        System.out.println("Usuarios: " + red.usuarios());

        String origen = Entrada.leer(in, "Origen", "Ana");
        String destino = Entrada.leer(in, "Destino", "Gina");
        try {
            ResultadoCamino resultado = CadenaMasRapida.calcular(red, origen, destino);
            System.out.println("Decisiones tomadas:");
            for (String linea : resultado.traza()) {
                System.out.println("  " + linea);
            }
            System.out.println(formatear(origen, destino, resultado));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Línea de resultado de una cadena de difusión. La reutiliza la opción 4 (A1) para informar igual.
     *
     * @return la cadena unida con " -> " y el tiempo total, o el aviso de que no existe cadena
     */
    static String formatear(String origen, String destino, ResultadoCamino r) {
        if (!r.alcanzable()) {
            return "No existe cadena de difusión de " + origen + " a " + destino;
        }
        return "Cadena: " + String.join(" -> ", r.cadena()) + " | Tiempo total: " + r.tiempoTotal() + " min";
    }
}
