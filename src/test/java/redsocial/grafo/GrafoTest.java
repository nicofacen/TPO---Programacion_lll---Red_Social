package redsocial.grafo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas de la red de difusión: sentido de los vínculos, vínculos repetidos y entradas inválidas. */
@DisplayName("Grafo: red de difusión dirigida y ponderada")
class GrafoTest {

    @Test
    @DisplayName("Los vínculos son dirigidos: A -> B no habilita B -> A")
    void losVinculosSonDirigidos() {
        Grafo g = new Grafo();
        g.agregarVinculo("A", "B", 5);

        assertEquals(2, g.cantidadUsuarios(), "el vínculo da de alta a los dos usuarios");
        assertEquals(List.of("A", "B"), g.usuarios(), "los usuarios quedan en orden de alta");
        assertEquals(1, g.vecinos(g.indiceDe("A")).size(), "A transmite a B");
        assertEquals(new Grafo.Arista(g.indiceDe("B"), 5), g.vecinos(g.indiceDe("A")).get(0));
        assertTrue(g.vecinos(g.indiceDe("B")).isEmpty(), "B no transmite a nadie");
    }

    @Test
    @DisplayName("Un vínculo repetido conserva el de menor tiempo")
    void unVinculoRepetidoConservaElMenorTiempo() {
        Grafo g = new Grafo();
        g.agregarVinculo("A", "B", 9);
        g.agregarVinculo("A", "B", 3);
        g.agregarVinculo("A", "B", 6);

        List<Grafo.Arista> salientes = g.vecinos(g.indiceDe("A"));
        assertEquals(1, salientes.size(), "queda un solo vínculo por par ordenado");
        assertEquals(3, salientes.get(0).minutos(), "se conserva el más rápido");
        assertEquals(2, g.cantidadUsuarios());
    }

    @Test
    @DisplayName("agregarUsuario es idempotente y contiene informa si el usuario existe")
    void agregarUsuarioEsIdempotente() {
        Grafo g = new Grafo();
        int primero = g.agregarUsuario("Ana");
        int repetido = g.agregarUsuario("Ana");

        assertEquals(primero, repetido, "el mismo nombre devuelve el mismo índice");
        assertEquals(1, g.cantidadUsuarios(), "no se duplica el usuario");
        assertEquals("Ana", g.nombreDe(primero));
        assertTrue(g.contiene("Ana"));
        assertFalse(g.contiene("Z"), "un usuario que no fue dado de alta no está en la red");
    }

    @Test
    @DisplayName("Entradas inválidas: tiempo no positivo, autovínculo, nombre vacío y usuario inexistente")
    void entradasInvalidasLanzanExcepcion() {
        Grafo g = new Grafo();
        g.agregarVinculo("A", "B", 4);

        assertThrows(IllegalArgumentException.class, () -> g.agregarVinculo("A", "B", 0),
                "el tiempo tiene que ser positivo");
        assertThrows(IllegalArgumentException.class, () -> g.agregarVinculo("A", "B", -7),
                "no se admiten tiempos negativos");
        assertThrows(IllegalArgumentException.class, () -> g.agregarVinculo("A", "A", 5),
                "un usuario no puede vincularse consigo mismo");
        assertThrows(IllegalArgumentException.class, () -> g.agregarUsuario("   "),
                "el nombre no puede estar en blanco");
        assertThrows(IllegalArgumentException.class, () -> g.indiceDe("Nadie"),
                "no se puede pedir el índice de un usuario que no existe");
    }
}
