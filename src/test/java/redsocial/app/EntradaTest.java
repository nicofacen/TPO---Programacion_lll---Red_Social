package redsocial.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Scanner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntradaTest {

    @Test
    @DisplayName("Una línea vacía o con solo espacios devuelve el valor por defecto")
    void lineaVaciaDevuelveDefecto() {
        assertEquals("Ana", Entrada.leer(new Scanner("\n"), "Origen", "Ana"));
        assertEquals("Ana", Entrada.leer(new Scanner("   \n"), "Origen", "Ana"));
    }

    @Test
    @DisplayName("El texto se devuelve sin los espacios de los extremos")
    void textoSeRecorta() {
        assertEquals("Bruno", Entrada.leer(new Scanner("  Bruno  \n"), "Origen", "Ana"));
    }

    @Test
    @DisplayName("leerEntero usa el defecto, parsea números y rechaza texto")
    void leerEntero() {
        assertEquals(50, Entrada.leerEntero(new Scanner("\n"), "Presupuesto", 50));
        assertEquals(70, Entrada.leerEntero(new Scanner("70\n"), "Presupuesto", 50));
        assertThrows(IllegalArgumentException.class,
                () -> Entrada.leerEntero(new Scanner("abc\n"), "Presupuesto", 50));
    }
}
