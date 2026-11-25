package redsocial.actividades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redsocial.actividades.SeleccionActividades.Resultado;
import redsocial.modelo.Actividad;

class SeleccionActividadesTest {

    private static Actividad act(int id, int horaInicio, int horaFin, int interes) {
        return new Actividad(id, "Act " + id, LocalTime.of(horaInicio, 0), LocalTime.of(horaFin, 0), interes);
    }

    private static List<Integer> ids(Resultado resultado) {
        List<Integer> ids = new ArrayList<>();
        for (Actividad a : resultado.seleccionadas()) {
            ids.add(a.id());
        }
        return ids;
    }

    @Test
    @DisplayName("Supera las heurísticas Greedy (menor fin, mayor interés y mayor interés/hora)")
    void superaHeuristicasGreedy() {
        // Menor fin: #2(30) + #6(40) + #3(45) = 115
        // Mayor interés y mayor interés/hora: #1(95) + #5(40) + #2(30) = 165
        // Óptimo real (DP): #4(80) + #7(55) + #5(40) = 175
        List<Actividad> actividades = List.of(
                act(1, 12, 15, 95),
                act(2, 9, 11, 30),
                act(3, 14, 16, 45),
                act(4, 10, 13, 80),
                act(5, 15, 17, 40),
                act(6, 11, 14, 40),
                act(7, 13, 15, 55)
        );

        Resultado resultado = SeleccionActividades.seleccionar(actividades);

        assertEquals(List.of(4, 7, 5), ids(resultado));
        assertEquals(175, resultado.interesTotal());
        assertEquals(175, SeleccionActividades.maximoInteres(actividades));
    }

    @Test
    @DisplayName("Permite que una actividad comience exactamente cuando termina otra y rechaza solapamiento de 1 minuto")
    void compatibilidadEnElLimiteHorario() {
        Actividad a = new Actividad(1, "A", LocalTime.of(9, 0), LocalTime.of(11, 0), 20);
        Actividad b = new Actividad(2, "B", LocalTime.of(11, 0), LocalTime.of(13, 0), 25);
        Actividad c = new Actividad(3, "C", LocalTime.of(13, 0), LocalTime.of(14, 30), 15);
        Actividad solapada = new Actividad(4, "D", LocalTime.of(10, 59), LocalTime.of(12, 0), 30);

        assertTrue(a.esCompatibleCon(b));
        assertTrue(b.esCompatibleCon(c));
        assertFalse(a.esCompatibleCon(solapada));

        Resultado resultado = SeleccionActividades.seleccionar(List.of(c, solapada, a, b));
        assertEquals(List.of(1, 2, 3), ids(resultado));
        assertEquals(60, resultado.interesTotal());
    }

    @Test
    @DisplayName("Casos borde: lista vacía, una sola actividad y actividades con interés cero")
    void casosBorde() {
        Resultado vacia = SeleccionActividades.seleccionar(List.of());
        assertTrue(vacia.seleccionadas().isEmpty());
        assertEquals(0, vacia.interesTotal());
        assertEquals(0, SeleccionActividades.maximoInteres(List.of()));

        Actividad unica = act(10, 8, 10, 42);
        Resultado unaSola = SeleccionActividades.seleccionar(List.of(unica));
        assertEquals(List.of(unica), unaSola.seleccionadas());
        assertEquals(42, unaSola.interesTotal());

        List<Actividad> conCero = List.of(
                act(1, 9, 10, 0),
                act(2, 10, 12, 15),
                act(3, 12, 14, 0)
        );
        Resultado resultadoCero = SeleccionActividades.seleccionar(conCero);
        assertEquals(List.of(2), ids(resultadoCero));
        assertEquals(15, resultadoCero.interesTotal());
    }

    @Test
    @DisplayName("No modifica la lista original y devuelve la selección en orden cronológico")
    void noModificaListaOriginal() {
        List<Actividad> original = new ArrayList<>();
        original.add(act(3, 15, 17, 20));
        original.add(act(1, 9, 11, 30));
        original.add(act(2, 11, 14, 40));
        List<Actividad> copiaEsperada = List.copyOf(original);

        Resultado resultado = SeleccionActividades.seleccionar(original);

        assertEquals(copiaEsperada, original);
        assertEquals(List.of(1, 2, 3), ids(resultado));
        assertEquals(90, resultado.interesTotal());
    }

    @Test
    @DisplayName("Entradas inválidas lanzan IllegalArgumentException")
    void validaciones() {
        LocalTime nueve = LocalTime.of(9, 0);
        LocalTime diez = LocalTime.of(10, 0);

        assertThrows(IllegalArgumentException.class, () -> new Actividad(1, "  ", nueve, diez, 10));
        assertThrows(IllegalArgumentException.class, () -> new Actividad(1, "A", null, diez, 10));
        assertThrows(IllegalArgumentException.class, () -> new Actividad(1, "A", nueve, null, 10));
        assertThrows(IllegalArgumentException.class, () -> new Actividad(1, "A", diez, diez, 10));
        assertThrows(IllegalArgumentException.class, () -> new Actividad(1, "A", diez, nueve, 10));
        assertThrows(IllegalArgumentException.class, () -> new Actividad(1, "A", nueve, diez, -1));
        assertThrows(IllegalArgumentException.class, () -> SeleccionActividades.seleccionar(null));
        assertThrows(IllegalArgumentException.class, () -> SeleccionActividades.maximoInteres(null));

        List<Actividad> conNulo = new ArrayList<>();
        conNulo.add(act(1, 9, 10, 5));
        conNulo.add(null);
        assertThrows(IllegalArgumentException.class, () -> SeleccionActividades.seleccionar(conNulo));
    }

    @Test
    @DisplayName("Coincide con fuerza bruta (2^n) en 300 casos al azar y las elegidas no se superponen")
    void coincideConFuerzaBrutaEn300CasosAlAzar() {
        Random azar = new Random(77);
        for (int caso = 0; caso < 300; caso++) {
            int n = 1 + azar.nextInt(12);
            List<Actividad> actividades = new ArrayList<>();
            for (int id = 1; id <= n; id++) {
                int inicioMin = 8 * 60 + azar.nextInt(12 * 60); // entre 08:00 y 19:59
                int duracionMin = 15 + azar.nextInt(180);       // entre 15 min y 3h14m
                LocalTime inicio = LocalTime.of(inicioMin / 60, inicioMin % 60);
                int finMin = Math.min(inicioMin + duracionMin, 23 * 60 + 59);
                LocalTime fin = LocalTime.of(finMin / 60, finMin % 60);
                int interes = azar.nextInt(101);
                actividades.add(new Actividad(id, "Act " + id, inicio, fin, interes));
            }

            int optimoFuerzaBruta = fuerzaBruta(actividades);
            Resultado resultado = SeleccionActividades.seleccionar(actividades);

            assertEquals(optimoFuerzaBruta, resultado.interesTotal());
            assertEquals(optimoFuerzaBruta, SeleccionActividades.maximoInteres(actividades));

            int suma = 0;
            Actividad anterior = null;
            for (Actividad elegida : resultado.seleccionadas()) {
                if (anterior != null) {
                    assertFalse(anterior.fin().isAfter(elegida.inicio()),
                            "Las actividades elegidas deben ser compatibles y cronológicas");
                }
                anterior = elegida;
                suma += elegida.interes();
            }
            assertEquals(optimoFuerzaBruta, suma);
        }
    }

    /** Explora los 2^n subconjuntos de actividades y devuelve el mayor interés entre los compatibles. */
    private static int fuerzaBruta(List<Actividad> actividades) {
        int n = actividades.size();
        int mejor = 0;
        int totalMascaras = 1 << n;
        for (int mascara = 1; mascara < totalMascaras; mascara++) {
            int suma = 0;
            boolean valido = true;
            for (int i = 0; i < n && valido; i++) {
                if ((mascara & (1 << i)) == 0) {
                    continue;
                }
                Actividad a = actividades.get(i);
                suma += a.interes();
                for (int j = i + 1; j < n; j++) {
                    if ((mascara & (1 << j)) != 0 && !a.esCompatibleCon(actividades.get(j))) {
                        valido = false;
                        break;
                    }
                }
            }
            if (valido && suma > mejor) {
                mejor = suma;
            }
        }
        return mejor;
    }
}
