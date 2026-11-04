package redsocial.difusion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redsocial.difusion.CadenasAlternativas.CadenaAlternativa;
import redsocial.difusion.CadenasAlternativas.Resultado;
import redsocial.grafo.Grafo;
import redsocial.grafo.Grafo.Arista;

/** Pruebas de la búsqueda de cadenas alternativas con restricciones (B3: Backtracking). */
@DisplayName("CadenasAlternativas: cadenas simples de difusión con Backtracking")
class CadenasAlternativasTest {

    /**
     * Red de prueba con un ciclo entre B y C (ningún usuario puede repetirse en una cadena):
     * A->B 2, A->C 5, B->C 1, B->D 7, C->D 2, C->B 1.
     */
    private static Grafo redDePrueba() {
        Grafo g = new Grafo();
        g.agregarVinculo("A", "B", 2);
        g.agregarVinculo("A", "C", 5);
        g.agregarVinculo("B", "C", 1);
        g.agregarVinculo("B", "D", 7);
        g.agregarVinculo("C", "D", 2);
        g.agregarVinculo("C", "B", 1);
        return g;
    }

    private static Set<List<String>> conjuntoDeCadenas(Resultado resultado) {
        Set<List<String>> conjunto = new HashSet<>();
        for (CadenaAlternativa c : resultado.cadenas()) {
            conjunto.add(c.usuarios());
        }
        return conjunto;
    }

    @Test
    @DisplayName("Enumera las 4 cadenas simples sin repetir usuarios y destaca la más rápida")
    void enumeraTodasLasCadenasSimplesYMasRapida() {
        Resultado r = CadenasAlternativas.buscar(redDePrueba(), "A", "D", 100, 10, Set.of());

        assertEquals(4, r.cadenas().size());
        assertEquals(
                Set.of(
                        List.of("A", "B", "D"),
                        List.of("A", "B", "C", "D"),
                        List.of("A", "C", "D"),
                        List.of("A", "C", "B", "D")),
                conjuntoDeCadenas(r));

        Map<List<String>, Integer> tiempos = new HashMap<>();
        for (CadenaAlternativa c : r.cadenas()) {
            tiempos.put(c.usuarios(), c.minutos());
        }
        assertEquals(9, tiempos.get(List.of("A", "B", "D")));
        assertEquals(5, tiempos.get(List.of("A", "B", "C", "D")));
        assertEquals(7, tiempos.get(List.of("A", "C", "D")));
        assertEquals(13, tiempos.get(List.of("A", "C", "B", "D")));

        assertNotNull(r.masRapida());
        assertEquals(List.of("A", "B", "C", "D"), r.masRapida().usuarios());
        assertEquals(5, r.masRapida().minutos());
    }

    @Test
    @DisplayName("Filtra las cadenas que superan el tiempo máximo")
    void filtraPorTiempoMaximo() {
        Resultado r = CadenasAlternativas.buscar(redDePrueba(), "A", "D", 7, 10, Set.of());

        assertEquals(2, r.cadenas().size());
        assertEquals(
                Set.of(
                        List.of("A", "B", "C", "D"),
                        List.of("A", "C", "D")),
                conjuntoDeCadenas(r));
        assertNotNull(r.masRapida());
        assertEquals(List.of("A", "B", "C", "D"), r.masRapida().usuarios());
        assertEquals(5, r.masRapida().minutos());
    }

    @Test
    @DisplayName("Respeta la cantidad máxima de intermediarios")
    void filtraPorMaximoDeIntermediarios() {
        Resultado conUno = CadenasAlternativas.buscar(redDePrueba(), "A", "D", 100, 1, Set.of());
        assertEquals(2, conUno.cadenas().size());
        assertEquals(
                Set.of(
                        List.of("A", "B", "D"),
                        List.of("A", "C", "D")),
                conjuntoDeCadenas(conUno));
        assertNotNull(conUno.masRapida());
        assertEquals(List.of("A", "C", "D"), conUno.masRapida().usuarios());
        assertEquals(7, conUno.masRapida().minutos());

        Resultado conCero = CadenasAlternativas.buscar(redDePrueba(), "A", "D", 100, 0, Set.of());
        assertTrue(conCero.cadenas().isEmpty());
        assertNull(conCero.masRapida());
    }

    @Test
    @DisplayName("Respeta los usuarios excluidos, incluido el destino")
    void respetaUsuariosExcluidos() {
        Grafo g = redDePrueba();

        Resultado sinB = CadenasAlternativas.buscar(g, "A", "D", 100, 10, Set.of("B"));
        assertEquals(1, sinB.cadenas().size());
        assertEquals(Set.of(List.of("A", "C", "D")), conjuntoDeCadenas(sinB));
        assertNotNull(sinB.masRapida());
        assertEquals(List.of("A", "C", "D"), sinB.masRapida().usuarios());
        assertEquals(7, sinB.masRapida().minutos());

        Resultado sinBNiC = CadenasAlternativas.buscar(g, "A", "D", 100, 10, Set.of("B", "C"));
        assertTrue(sinBNiC.cadenas().isEmpty());
        assertNull(sinBNiC.masRapida());

        Resultado destinoExcluido = CadenasAlternativas.buscar(g, "A", "D", 100, 10, Set.of("D"));
        assertTrue(destinoExcluido.cadenas().isEmpty());
        assertNull(destinoExcluido.masRapida());
    }

    @Test
    @DisplayName("Valida parámetros inválidos lanzando IllegalArgumentException")
    void validacionesLanzanExcepcion() {
        Grafo g = redDePrueba();

        assertThrows(
                IllegalArgumentException.class,
                () -> CadenasAlternativas.buscar(g, "A", "A", 100, 10, Set.of()));
        assertThrows(
                IllegalArgumentException.class,
                () -> CadenasAlternativas.buscar(g, "A", "D", -1, 10, Set.of()));
        assertThrows(
                IllegalArgumentException.class,
                () -> CadenasAlternativas.buscar(g, "A", "D", 100, -1, Set.of()));
        assertThrows(
                IllegalArgumentException.class,
                () -> CadenasAlternativas.buscar(g, "A", "Inexistente", 100, 10, Set.of()));
        assertThrows(
                IllegalArgumentException.class,
                () -> CadenasAlternativas.buscar(g, "Inexistente", "D", 100, 10, Set.of()));
    }

    @Test
    @DisplayName("Coincide con una enumeración exhaustiva sin podas en 100 redes al azar")
    void coincideConEnumeracionExhaustivaEn100RedesAlAzar() {
        Random azar = new Random(99);

        for (int caso = 0; caso < 100; caso++) {
            int n = 3 + azar.nextInt(5); // 3 a 7 usuarios
            Grafo g = new Grafo();
            for (int i = 0; i < n; i++) {
                g.agregarUsuario("U" + i);
            }
            for (int e = 0; e < 2 * n; e++) {
                int u = azar.nextInt(n);
                int v = azar.nextInt(n - 1);
                if (v >= u) {
                    v++;
                }
                int minutos = 1 + azar.nextInt(9); // 1 a 9
                g.agregarVinculo("U" + u, "U" + v, minutos);
            }

            String origen = "U0";
            String destino = "U" + (n - 1);
            int tiempoMaximo = 5 + azar.nextInt(25); // 5 a 29
            int maxIntermediarios = azar.nextInt(n); // 0 a n-1
            Set<String> excluidos =
                    azar.nextBoolean() ? Set.of("U" + (1 + azar.nextInt(n - 1))) : Set.of();

            int esperadas = contarExhaustivoSinPoda(
                    g, origen, destino, tiempoMaximo, maxIntermediarios, excluidos);
            Resultado r = CadenasAlternativas.buscar(
                    g, origen, destino, tiempoMaximo, maxIntermediarios, excluidos);

            assertEquals(esperadas, r.cadenas().size(), "Cantidad de cadenas en el caso " + caso);

            Set<List<String>> vistas = new HashSet<>();
            for (CadenaAlternativa c : r.cadenas()) {
                List<String> usuarios = c.usuarios();
                assertEquals(origen, usuarios.getFirst());
                assertEquals(destino, usuarios.getLast());
                assertEquals(
                        usuarios.size(),
                        new HashSet<>(usuarios).size(),
                        "No puede repetir usuarios");
                assertTrue(c.minutos() <= tiempoMaximo, "No puede superar el tiempo máximo");
                assertTrue(
                        usuarios.size() - 2 <= maxIntermediarios,
                        "No puede superar el máximo de intermediarios");
                for (String ex : excluidos) {
                    assertFalse(usuarios.contains(ex), "No puede pasar por un usuario excluido");
                }
                assertTrue(vistas.add(usuarios), "No puede haber cadenas duplicadas");
            }
        }
    }

    /**
     * Recorre todos los caminos simples de {@code origen} a {@code destino} sin ninguna poda
     * y aplica todas las restricciones únicamente al llegar al destino.
     */
    private static int contarExhaustivoSinPoda(Grafo g, String origen, String destino,
            int tiempoMaximo, int maxIntermediarios, Set<String> excluidos) {
        boolean[] enCamino = new boolean[g.cantidadUsuarios()];
        List<String> camino = new ArrayList<>();
        int indiceOrigen = g.indiceDe(origen);
        int indiceDestino = g.indiceDe(destino);

        enCamino[indiceOrigen] = true;
        camino.add(origen);

        return dfsExhaustivo(
                g, indiceOrigen, indiceDestino, 0, tiempoMaximo, maxIntermediarios,
                excluidos, enCamino, camino);
    }

    private static int dfsExhaustivo(Grafo g, int actual, int destino, int tiempo,
            int tiempoMaximo, int maxIntermediarios, Set<String> excluidos,
            boolean[] enCamino, List<String> camino) {
        if (actual == destino) {
            if (tiempo > tiempoMaximo || camino.size() - 2 > maxIntermediarios) {
                return 0;
            }
            for (String u : camino) {
                if (excluidos.contains(u)) {
                    return 0;
                }
            }
            return 1;
        }

        int total = 0;
        for (Arista arista : g.vecinos(actual)) {
            int vecino = arista.destino();
            if (enCamino[vecino]) {
                continue;
            }
            enCamino[vecino] = true;
            camino.add(g.nombreDe(vecino));
            total += dfsExhaustivo(
                    g, vecino, destino, tiempo + arista.minutos(), tiempoMaximo,
                    maxIntermediarios, excluidos, enCamino, camino);
            camino.removeLast();
            enCamino[vecino] = false;
        }
        return total;
    }
}
