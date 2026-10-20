package redsocial.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PublicacionTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 10, 1, 9, 0);

    @Test
    @DisplayName("La relevancia es reacciones + 2 x comentarios")
    void relevancia() {
        assertEquals(18, new Publicacion(1, "Ana", BASE, 12, 3).relevancia());
    }

    @Test
    @DisplayName("Autor vacío, fecha nula y valores negativos son inválidos")
    void validaciones() {
        assertThrows(IllegalArgumentException.class, () -> new Publicacion(1, "  ", BASE, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Publicacion(1, null, BASE, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Publicacion(1, "Ana", null, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Publicacion(1, "Ana", BASE, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Publicacion(1, "Ana", BASE, 0, -1));
    }
}
