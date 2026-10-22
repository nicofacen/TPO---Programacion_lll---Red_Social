package redsocial.financiamiento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
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
}
