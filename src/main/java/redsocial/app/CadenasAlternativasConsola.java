package redsocial.app;

import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import redsocial.difusion.CadenasAlternativas;
import redsocial.difusion.CadenasAlternativas.CadenaAlternativa;
import redsocial.difusion.CadenasAlternativas.Resultado;
import redsocial.grafo.Grafo;

/**
 * Opción 6 del menú: B3, cadenas alternativas de difusión con restricciones (Backtracking).
 */
public final class CadenasAlternativasConsola implements Opcion {

    @Override
    public String titulo() {
        return "B3 · Cadenas alternativas de difusión (Backtracking)";
    }

    @Override
    public void ejecutar(Scanner in) {
        Grafo red = DatosRed.red();
        System.out.println("Usuarios: " + red.usuarios());

        try {
            String origen = Entrada.leer(in, "Origen", "Ana");
            String destino = Entrada.leer(in, "Destino", "Gina");
            int tiempoMaximo = Entrada.leerEntero(in, "Tiempo máximo (min)", 40);
            int maxIntermediarios = Entrada.leerEntero(in, "Máximo de intermediarios", 4);
            String textoExcluidos = Entrada.leer(in, "Usuarios excluidos (separados por coma)", "");
            Set<String> excluidos = parsearExcluidos(textoExcluidos);

            Resultado resultado = CadenasAlternativas.buscar(
                    red, origen, destino, tiempoMaximo, maxIntermediarios, excluidos);

            System.out.println("Cadenas válidas: " + resultado.cadenas().size());
            for (CadenaAlternativa cadena : resultado.cadenas()) {
                System.out.println("  " + formatearCadena(cadena));
            }
            if (resultado.masRapida() != null) {
                System.out.println("Más rápida: " + formatearCadena(resultado.masRapida()));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static Set<String> parsearExcluidos(String texto) {
        if (texto.isBlank()) {
            return Set.of();
        }
        Set<String> excluidos = new LinkedHashSet<>();
        for (String parte : texto.split(",")) {
            String nombre = parte.trim();
            if (!nombre.isEmpty()) {
                excluidos.add(nombre);
            }
        }
        return excluidos;
    }

    private static String formatearCadena(CadenaAlternativa cadena) {
        return String.join(" -> ", cadena.usuarios()) + " | " + cadena.minutos() + " min";
    }
}
