package redsocial.equipos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redsocial.equipos.FormacionEquipos.Incompatibilidad;
import redsocial.modelo.Estudiante;

@DisplayName("FormacionEquipos: backtracking con obligatorios, incompatibles y habilidades")
class FormacionEquiposTest {

    private static final Set<String> TODAS = Set.of("Java", "SQL", "UI");

    /** Grupo base: Ana{Java,SQL}, Beto{Java,UI}, Cami{SQL}, Dani{UI}. */
    private static List<Estudiante> grupoBase() {
        return List.of(
                new Estudiante("Ana", Set.of("Java", "SQL")),
                new Estudiante("Beto", Set.of("Java", "UI")),
                new Estudiante("Cami", Set.of("SQL")),
                new Estudiante("Dani", Set.of("UI")));
    }

    private static List<List<String>> equipos(int tamano, Set<String> requeridas, Set<String> obligatorios,
                                              List<Incompatibilidad> incompatibles) {
        return FormacionEquipos.encontrarEquipos(grupoBase(), tamano, requeridas, obligatorios, incompatibles);
    }

    @Test
    @DisplayName("Tamaño 2 cubriendo Java, SQL y UI: tres equipos")
    void casoBase() {
        List<List<String>> resultado = equipos(2, TODAS, Set.of(), List.of());
        assertEquals(Set.of(List.of("Ana", "Beto"), List.of("Ana", "Dani"), List.of("Beto", "Cami")),
                new HashSet<>(resultado));
        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("Con Cami obligatoria solo queda [Beto, Cami]")
    void conObligatorio() {
        assertEquals(List.of(List.of("Beto", "Cami")), equipos(2, TODAS, Set.of("Cami"), List.of()));
    }

    @Test
    @DisplayName("Con Ana y Beto incompatibles se descarta [Ana, Beto]")
    void conIncompatibilidad() {
        List<List<String>> resultado = equipos(2, TODAS, Set.of(), List.of(new Incompatibilidad("Ana", "Beto")));
        assertEquals(Set.of(List.of("Ana", "Dani"), List.of("Beto", "Cami")), new HashSet<>(resultado));
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Casos sin solución devuelven una lista vacía")
    void sinSolucion() {
        // Un solo integrante no cubre las tres habilidades
        assertTrue(equipos(1, TODAS, Set.of(), List.of()).isEmpty());
        // Obligatorios incompatibles entre sí
        assertTrue(equipos(3, TODAS, Set.of("Ana", "Beto"), List.of(new Incompatibilidad("Beto", "Ana"))).isEmpty());
        // Más obligatorios que el tamaño del equipo
        assertTrue(equipos(1, Set.of(), Set.of("Ana", "Beto"), List.of()).isEmpty());
        // Una habilidad que nadie tiene
        assertTrue(equipos(2, Set.of("Java", "Python"), Set.of(), List.of()).isEmpty());
    }

    @Test
    @DisplayName("Sin habilidades requeridas salen las C(4,2) = 6 combinaciones")
    void sinRequeridas() {
        List<List<String>> resultado = equipos(2, Set.of(), Set.of(), List.of());
        assertEquals(6, resultado.size());
        assertEquals(6, new HashSet<>(resultado).size());
    }

    @Test
    @DisplayName("Tamaño 0, obligatorio inexistente o incompatibilidad con un nombre inexistente son inválidos")
    void validaciones() {
        assertThrows(IllegalArgumentException.class, () -> equipos(0, TODAS, Set.of(), List.of()));
        assertThrows(IllegalArgumentException.class, () -> equipos(2, TODAS, Set.of("Zoe"), List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> equipos(2, TODAS, Set.of(), List.of(new Incompatibilidad("Ana", "Zoe"))));
    }

    @Test
    @DisplayName("300 casos al azar coinciden con la fuerza bruta y no tienen equipos repetidos")
    void contraFuerzaBruta() {
        String[] catalogo = {"Java", "SQL", "UI", "Testing", "Redes"};
        Random azar = new Random(11);
        for (int caso = 0; caso < 300; caso++) {
            int n = 1 + azar.nextInt(8);
            List<Estudiante> estudiantes = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                Set<String> habilidades = new HashSet<>();
                for (String h : catalogo) {
                    if (azar.nextInt(3) == 0) {
                        habilidades.add(h);
                    }
                }
                estudiantes.add(new Estudiante("E" + i, habilidades));
            }
            int k = 1 + azar.nextInt(n);
            Set<String> requeridas = new HashSet<>();
            for (String h : catalogo) {
                if (azar.nextInt(4) == 0) {
                    requeridas.add(h);
                }
            }
            Set<String> obligatorios = new HashSet<>();
            if (azar.nextInt(3) == 0) {
                obligatorios.add("E" + azar.nextInt(n));
            }
            List<Incompatibilidad> incompatibles = new ArrayList<>();
            int cantidad = n < 2 ? 0 : azar.nextInt(4);
            for (int j = 0; j < cantidad; j++) {
                int a = azar.nextInt(n);
                int b = azar.nextInt(n - 1);
                if (b >= a) {
                    b++; // b distinto de a
                }
                incompatibles.add(new Incompatibilidad("E" + a, "E" + b));
            }

            List<List<String>> obtenido =
                    FormacionEquipos.encontrarEquipos(estudiantes, k, requeridas, obligatorios, incompatibles);
            Set<List<String>> esperado = fuerzaBruta(estudiantes, k, requeridas, obligatorios, incompatibles);

            assertEquals(esperado, new HashSet<>(obtenido), "caso " + caso);
            assertEquals(obtenido.size(), new HashSet<>(obtenido).size(), "equipos repetidos en el caso " + caso);
        }
    }

    /** Recorre las 2^n máscaras con k bits encendidos y filtra por habilidades, obligatorios e incompatibles. */
    private static Set<List<String>> fuerzaBruta(List<Estudiante> estudiantes, int k, Set<String> requeridas,
                                                 Set<String> obligatorios, List<Incompatibilidad> incompatibles) {
        int n = estudiantes.size();
        Set<List<String>> validos = new HashSet<>();
        for (int mascara = 0; mascara < (1 << n); mascara++) {
            if (Integer.bitCount(mascara) != k) {
                continue;
            }
            List<String> equipo = new ArrayList<>();
            Set<String> cubiertas = new HashSet<>();
            for (int i = 0; i < n; i++) {
                if ((mascara & (1 << i)) != 0) {
                    equipo.add(estudiantes.get(i).nombre());
                    cubiertas.addAll(estudiantes.get(i).habilidades());
                }
            }
            boolean valido = cubiertas.containsAll(requeridas) && equipo.containsAll(obligatorios);
            for (Incompatibilidad par : incompatibles) {
                if (equipo.contains(par.a()) && equipo.contains(par.b())) {
                    valido = false;
                }
            }
            if (valido) {
                validos.add(equipo);
            }
        }
        return validos;
    }
}
