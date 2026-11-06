package redsocial.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test de integración: ejecuta el modo demo completo y verifica las líneas clave de la salida
 * esperada, una por funcionalidad.
 */
@DisplayName("Main.demo: las 6 funcionalidades de punta a punta")
class MainDemoTest {

    private static final List<String> LINEAS_CLAVE = List.of(
            // 1 · Muro (Divide y Conquista)
            "Orden por recencia (ids): 6, 5, 4, 2, 3, 7, 1",
            "Orden por relevancia (ids): 5, 2, 3, 7, 1, 4, 6",
            // 2 · Cadena más rápida (Greedy)
            "Decisiones tomadas:",
            "Cadena: Ana -> Carla -> Bruno -> Diego -> Eva -> Facu -> Gina | Tiempo total: 27 min",
            // 3 · Iniciativas (Programación Dinámica)
            "Iniciativas seleccionadas: #1, #2, #4",
            "Costo total: 45 | Impacto obtenido: 230 | Presupuesto restante: 5",
            // 4 · A1 Consultas entre todos los usuarios (Floyd-Warshall)
            "No existe cadena de difusión de Ana a Hugo",
            // 5 · B1 Equipos (Backtracking)
            "Equipos válidos: 7",
            // 6 · B3 Cadenas alternativas (Backtracking)
            "Cadenas válidas: 10",
            "Más rápida: Ana -> Carla -> Bruno -> Diego -> Eva -> Gina | 28 min");

    @Test
    @DisplayName("La salida del demo contiene las líneas clave de las 6 funcionalidades")
    void demoMuestraLasLineasClave() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            Main.demo();
        } finally {
            System.setOut(original);
        }
        String salida = buffer.toString(StandardCharsets.UTF_8);

        List<String> faltantes = new ArrayList<>();
        for (String linea : LINEAS_CLAVE) {
            if (!salida.contains(linea)) {
                faltantes.add(linea);
            }
        }
        assertTrue(faltantes.isEmpty(), "Faltan estas líneas en la salida del demo: " + faltantes);
    }
}
