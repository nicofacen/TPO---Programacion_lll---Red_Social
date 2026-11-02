package redsocial.difusion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redsocial.grafo.Grafo;

/** Pruebas de las consultas entre todos los usuarios (Programación Dinámica: Floyd-Warshall). */
@DisplayName("ConsultasDifusion: tiempos mínimos entre todos los pares")
class ConsultasDifusionTest {

    @Test
    @DisplayName("Red de ejemplo: cadena A-C-B-D de 4 minutos, pares sin cadena y distancia 0 a uno mismo")
    void redDeEjemplo() {
        Grafo g = new Grafo();
        g.agregarVinculo("A", "B", 4);
        g.agregarVinculo("A", "C", 1);
        g.agregarVinculo("C", "B", 2);
        g.agregarVinculo("B", "D", 1);
        g.agregarUsuario("Aislado");
        ConsultasDifusion consultas = new ConsultasDifusion(g);

        ResultadoCamino r = consultas.consultar("A", "D");
        assertTrue(r.alcanzable());
        assertEquals(List.of("A", "C", "B", "D"), r.cadena());
        assertEquals(4, r.tiempoTotal());

        assertFalse(consultas.consultar("D", "A").alcanzable());
        assertFalse(consultas.consultar("A", "Aislado").alcanzable());

        ResultadoCamino consigoMismo = consultas.consultar("B", "B");
        assertTrue(consigoMismo.alcanzable());
        assertEquals(0, consigoMismo.tiempoTotal());

        assertEquals(OptionalInt.of(4), consultas.tiempoMinimo("A", "D"));
        assertEquals(OptionalInt.empty(), consultas.tiempoMinimo("D", "A"));
    }

    @Test
    @DisplayName("Vínculo repetido: A->B con 9 y luego con 3 deja el tiempo mínimo en 3")
    void vinculoRepetidoConservaElMenor() {
        Grafo g = new Grafo();
        g.agregarVinculo("A", "B", 9);
        g.agregarVinculo("A", "B", 3);

        assertEquals(OptionalInt.of(3), new ConsultasDifusion(g).tiempoMinimo("A", "B"));
    }

    @Test
    @DisplayName("Un usuario inexistente lanza IllegalArgumentException")
    void usuarioInexistente() {
        Grafo g = new Grafo();
        g.agregarVinculo("A", "B", 1);
        ConsultasDifusion consultas = new ConsultasDifusion(g);

        assertThrows(IllegalArgumentException.class, () -> consultas.tiempoMinimo("A", "Z"));
        assertThrows(IllegalArgumentException.class, () -> consultas.consultar("Z", "A"));
    }

    @Test
    @DisplayName("Contra Dijkstra: 100 redes al azar, todos los pares dan el mismo resultado")
    void coincideConDijkstraEnRedesAlAzar() {
        Random azar = new Random(2026);
        for (int caso = 0; caso < 100; caso++) {
            int n = 2 + azar.nextInt(9); // 2 a 10 usuarios
            Grafo g = new Grafo();
            for (int u = 0; u < n; u++) {
                g.agregarUsuario("U" + u);
            }
            int vinculos = azar.nextInt(3 * n + 1);
            for (int v = 0; v < vinculos; v++) {
                int origen = azar.nextInt(n);
                int destino = azar.nextInt(n);
                if (origen != destino) {
                    g.agregarVinculo("U" + origen, "U" + destino, 1 + azar.nextInt(20));
                }
            }

            ConsultasDifusion consultas = new ConsultasDifusion(g);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    String o = "U" + i;
                    String d = "U" + j;
                    ResultadoCamino esperado = CadenaMasRapida.calcular(g, o, d);
                    ResultadoCamino obtenido = consultas.consultar(o, d);
                    String par = "caso " + caso + ", " + o + " -> " + d;

                    assertEquals(esperado.alcanzable(), obtenido.alcanzable(), par);
                    assertEquals(esperado.tiempoTotal(), obtenido.tiempoTotal(), par);
                    assertEquals(esperado.alcanzable(), consultas.tiempoMinimo(o, d).isPresent(), par);
                    if (obtenido.alcanzable()) {
                        assertEquals(o, obtenido.cadena().get(0), par);
                        assertEquals(d, obtenido.cadena().get(obtenido.cadena().size() - 1), par);
                    }
                }
            }
        }
    }
}
