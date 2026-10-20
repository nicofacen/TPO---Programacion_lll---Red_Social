package redsocial.difusion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redsocial.grafo.Grafo;

/** Pruebas de la cadena de difusión más rápida (Greedy: Dijkstra). */
@DisplayName("CadenaMasRapida: cadena de difusión de menor tiempo total")
class CadenaMasRapidaTest {

    /**
     * Red de prueba: A->B 4, A->C 1, C->B 2, B->D 1, C->D 7 y un usuario Aislado sin vínculos.
     * Está armada para que la cadena más rápida NO sea la de menos saltos.
     */
    private static Grafo redDePrueba() {
        Grafo g = new Grafo();
        g.agregarVinculo("A", "B", 4);
        g.agregarVinculo("A", "C", 1);
        g.agregarVinculo("C", "B", 2);
        g.agregarVinculo("B", "D", 1);
        g.agregarVinculo("C", "D", 7);
        g.agregarUsuario("Aislado");
        return g;
    }

    @Test
    @DisplayName("Gana el menor tiempo total, no la cadena con menos saltos")
    void ganaElMenorTiempoNoElMenorSalto() {
        ResultadoCamino r = CadenaMasRapida.calcular(redDePrueba(), "A", "D");

        assertTrue(r.alcanzable());
        // A->C->B->D suma 1 + 2 + 1 = 4; A->B->D tiene menos saltos pero suma 4 + 1 = 5.
        assertEquals(List.of("A", "C", "B", "D"), r.cadena());
        assertEquals(4, r.tiempoTotal());
    }

    @Test
    @DisplayName("Si el destino no es alcanzable se informa y la cadena queda vacía")
    void destinoNoAlcanzable() {
        ResultadoCamino r = CadenaMasRapida.calcular(redDePrueba(), "A", "Aislado");

        assertFalse(r.alcanzable());
        assertTrue(r.cadena().isEmpty());
        assertEquals(0, r.tiempoTotal());
        assertTrue(r.traza().contains("El destino Aislado no es alcanzable desde A"));
    }

    @Test
    @DisplayName("Respeta el sentido de los vínculos: D no puede transmitirle a A")
    void respetaElSentidoDeLosVinculos() {
        ResultadoCamino r = CadenaMasRapida.calcular(redDePrueba(), "D", "A");

        assertFalse(r.alcanzable(), "todos los vínculos hacia A no existen: solo salen de A");
    }

    @Test
    @DisplayName("Origen igual a destino: cadena de un usuario y 0 minutos")
    void origenIgualADestino() {
        ResultadoCamino r = CadenaMasRapida.calcular(redDePrueba(), "A", "A");

        assertTrue(r.alcanzable());
        assertEquals(List.of("A"), r.cadena());
        assertEquals(0, r.tiempoTotal());
    }

    @Test
    @DisplayName("La traza registra las decisiones, incluidas las relajaciones")
    void laTrazaRegistraLasDecisiones() {
        ResultadoCamino r = CadenaMasRapida.calcular(redDePrueba(), "A", "D");

        assertFalse(r.traza().isEmpty(), "la consigna pide visualizar las decisiones del cálculo");
        assertTrue(r.traza().get(0).startsWith("Inicio:"));
        boolean hayRelajacion = false;
        for (String linea : r.traza()) {
            if (linea.contains("Relajación")) {
                hayRelajacion = true;
            }
        }
        assertTrue(hayRelajacion, "tiene que haber al menos una relajación en la traza");
    }

    @Test
    @DisplayName("Un usuario que no está en la red lanza IllegalArgumentException")
    void usuarioInexistenteLanzaExcepcion() {
        Grafo g = redDePrueba();

        assertThrows(IllegalArgumentException.class, () -> CadenaMasRapida.calcular(g, "A", "Nadie"));
        assertThrows(IllegalArgumentException.class, () -> CadenaMasRapida.calcular(g, "Nadie", "A"));
    }
}
