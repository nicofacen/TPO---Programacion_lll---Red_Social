package redsocial.financiamiento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redsocial.financiamiento.SeleccionIniciativas.Resultado;
import redsocial.modelo.Iniciativa;

class SeleccionIniciativasTest {

    private static List<Integer> ids(Resultado resultado) {
        List<Integer> ids = new ArrayList<>();
        for (Iniciativa iniciativa : resultado.seleccionadas()) {
            ids.add(iniciativa.id());
        }
        return ids;
    }

    @Test
    @DisplayName("Ejemplo clásico donde Greedy por razón impacto/costo falla (160) y la mochila obtiene el óptimo (220)")
    void ejemploClasicoDondeGreedyFalla() {
        List<Iniciativa> iniciativas = List.of(
                new Iniciativa(1, 10, 60),
                new Iniciativa(2, 20, 100),
                new Iniciativa(3, 30, 120)
        );
        Resultado resultado = SeleccionIniciativas.seleccionar(iniciativas, 50);

        assertEquals(List.of(2, 3), ids(resultado));
        assertEquals(50, resultado.costoTotal());
        assertEquals(220, resultado.impactoTotal());
        assertEquals(0, resultado.presupuestoRestante());
        assertEquals(220, SeleccionIniciativas.maximoImpacto(iniciativas, 50));
    }

    @Test
    @DisplayName("Presupuesto 0 y lista vacía de iniciativas devuelven selección vacía")
    void presupuestoCeroYSinIniciativas() {
        List<Iniciativa> iniciativas = List.of(
                new Iniciativa(1, 10, 60),
                new Iniciativa(2, 20, 100)
        );
        Resultado conPresupuestoCero = SeleccionIniciativas.seleccionar(iniciativas, 0);
        assertTrue(conPresupuestoCero.seleccionadas().isEmpty());
        assertEquals(0, conPresupuestoCero.costoTotal());
        assertEquals(0, conPresupuestoCero.impactoTotal());
        assertEquals(0, conPresupuestoCero.presupuestoRestante());

        Resultado sinIniciativas = SeleccionIniciativas.seleccionar(List.of(), 100);
        assertTrue(sinIniciativas.seleccionadas().isEmpty());
        assertEquals(0, sinIniciativas.costoTotal());
        assertEquals(0, sinIniciativas.impactoTotal());
        assertEquals(100, sinIniciativas.presupuestoRestante());
    }

    @Test
    @DisplayName("Si ninguna iniciativa entra en el presupuesto, no se selecciona ninguna y sobra todo")
    void ningunaEntraEnElPresupuesto() {
        List<Iniciativa> iniciativas = List.of(new Iniciativa(1, 50, 200));
        Resultado resultado = SeleccionIniciativas.seleccionar(iniciativas, 49);

        assertTrue(resultado.seleccionadas().isEmpty());
        assertEquals(0, resultado.costoTotal());
        assertEquals(0, resultado.impactoTotal());
        assertEquals(49, resultado.presupuestoRestante());
    }

    @Test
    @DisplayName("Una iniciativa con impacto 0 no altera el óptimo")
    void iniciativaConImpactoCero() {
        List<Iniciativa> iniciativas = List.of(
                new Iniciativa(1, 5, 0),
                new Iniciativa(2, 5, 7)
        );
        Resultado resultado = SeleccionIniciativas.seleccionar(iniciativas, 5);

        assertEquals(List.of(2), ids(resultado));
        assertEquals(5, resultado.costoTotal());
        assertEquals(7, resultado.impactoTotal());
        assertEquals(0, resultado.presupuestoRestante());
    }

    @Test
    @DisplayName("Costo no positivo, impacto negativo y presupuesto negativo lanzan IllegalArgumentException")
    void validaciones() {
        assertThrows(IllegalArgumentException.class, () -> new Iniciativa(1, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Iniciativa(1, -10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Iniciativa(1, 5, -1));
        assertThrows(IllegalArgumentException.class,
                () -> SeleccionIniciativas.seleccionar(List.of(new Iniciativa(1, 5, 10)), -1));
        assertThrows(IllegalArgumentException.class,
                () -> SeleccionIniciativas.maximoImpacto(null, 10));
    }

    @Test
    @DisplayName("Coincide con fuerza bruta en 300 casos al azar y la reconstrucción es coherente")
    void coincideConFuerzaBrutaEn300CasosAlAzar() {
        Random azar = new Random(42);
        for (int caso = 0; caso < 300; caso++) {
            int n = 1 + azar.nextInt(10);
            int presupuesto = azar.nextInt(60);
            List<Iniciativa> iniciativas = new ArrayList<>();
            for (int id = 1; id <= n; id++) {
                int costo = 1 + azar.nextInt(25);
                int impacto = azar.nextInt(100);
                iniciativas.add(new Iniciativa(id, costo, impacto));
            }

            int optimoFuerzaBruta = fuerzaBruta(iniciativas, 0, presupuesto);
            Resultado resultado = SeleccionIniciativas.seleccionar(iniciativas, presupuesto);

            assertEquals(optimoFuerzaBruta, resultado.impactoTotal());
            assertEquals(optimoFuerzaBruta, SeleccionIniciativas.maximoImpacto(iniciativas, presupuesto));

            int sumaCostos = 0;
            int sumaImpactos = 0;
            int idAnterior = 0;
            for (Iniciativa elegida : resultado.seleccionadas()) {
                assertTrue(elegida.id() > idAnterior, "las iniciativas deben conservar su orden y no repetirse");
                idAnterior = elegida.id();
                sumaCostos += elegida.costo();
                sumaImpactos += elegida.impacto();
            }
            assertTrue(sumaCostos <= presupuesto);
            assertEquals(sumaCostos, resultado.costoTotal());
            assertEquals(sumaImpactos, resultado.impactoTotal());
            assertEquals(presupuesto - sumaCostos, resultado.presupuestoRestante());
        }
    }

    /** Explora los 2^n subconjuntos posibles para obtener el impacto óptimo real como referencia. */
    private static int fuerzaBruta(List<Iniciativa> iniciativas, int indice, int presupuestoDisponible) {
        if (indice == iniciativas.size()) {
            return 0;
        }
        Iniciativa actual = iniciativas.get(indice);
        int sinActual = fuerzaBruta(iniciativas, indice + 1, presupuestoDisponible);
        if (actual.costo() > presupuestoDisponible) {
            return sinActual;
        }
        int conActual = actual.impacto() + fuerzaBruta(iniciativas, indice + 1, presupuestoDisponible - actual.costo());
        return Math.max(sinActual, conActual);
    }
}
