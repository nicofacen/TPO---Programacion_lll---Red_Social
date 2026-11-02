package redsocial.app;

import java.util.Scanner;
import redsocial.difusion.ConsultasDifusion;
import redsocial.difusion.ResultadoCamino;

/**
 * Opción 4 del menú: consultas de difusión entre todos los usuarios (Programación Dinámica: Floyd-Warshall).
 *
 * <p>Hace el procesamiento inicial una sola vez, muestra la matriz de tiempos mínimos y luego responde
 * dos consultas leyendo la matriz (por defecto Ana → Gina y Ana → Hugo, esta última sin cadena).</p>
 */
public final class ConsultasConsola implements Opcion {

    @Override
    public String titulo() {
        return "A1 · Consultas de difusión entre todos los usuarios (Programación Dinámica: Floyd-Warshall)";
    }

    @Override
    public void ejecutar(Scanner in) {
        ConsultasDifusion consultas = new ConsultasDifusion(DatosRed.red());
        System.out.println("Procesamiento inicial completo. Tiempos mínimos en minutos ('-' = sin cadena):");
        System.out.print(consultas.matrizComoTexto());

        consultar(in, consultas, "Ana", "Gina");
        consultar(in, consultas, "Ana", "Hugo");
    }

    private static void consultar(Scanner in, ConsultasDifusion consultas, String origenPorDefecto,
                                  String destinoPorDefecto) {
        String origen = Entrada.leer(in, "Origen", origenPorDefecto);
        String destino = Entrada.leer(in, "Destino", destinoPorDefecto);
        try {
            ResultadoCamino resultado = consultas.consultar(origen, destino);
            System.out.println(CadenaRapidaConsola.formatear(origen, destino, resultado));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
