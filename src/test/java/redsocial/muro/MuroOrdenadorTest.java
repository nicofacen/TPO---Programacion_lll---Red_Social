package redsocial.muro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redsocial.modelo.Publicacion;

class MuroOrdenadorTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 10, 1, 9, 0);

    private static Publicacion pub(int id, int horas, int reacciones, int comentarios) {
        return new Publicacion(id, "autor" + id, BASE.plusHours(horas), reacciones, comentarios);
    }

    private static List<Integer> ids(List<Publicacion> lista) {
        List<Integer> ids = new ArrayList<>();
        for (Publicacion p : lista) {
            ids.add(p.id());
        }
        return ids;
    }

    @Test
    @DisplayName("Una lista vacía y una de un elemento se devuelven tal cual")
    void casosBase() {
        assertEquals(List.of(), MuroOrdenador.masRecientesPrimero(List.of()));
        List<Publicacion> una = List.of(pub(1, 0, 1, 1));
        assertEquals(una, MuroOrdenador.masRelevantesPrimero(una));
    }

    @Test
    @DisplayName("Más recientes primero, con empate de fecha gana el id menor")
    void masRecientes() {
        List<Publicacion> entrada = List.of(pub(3, 5, 0, 0), pub(1, 0, 0, 0), pub(2, 5, 0, 0), pub(4, 9, 0, 0));
        assertEquals(List.of(4, 2, 3, 1), ids(MuroOrdenador.masRecientesPrimero(entrada)));
    }

    @Test
    @DisplayName("Más relevantes primero, con desempate por recencia y luego por id")
    void masRelevantes() {
        List<Publicacion> entrada = List.of(pub(1, 0, 10, 0), pub(2, 5, 4, 3), pub(3, 5, 4, 3), pub(4, 1, 50, 0));
        assertEquals(List.of(4, 2, 3, 1), ids(MuroOrdenador.masRelevantesPrimero(entrada)));
    }

    @Test
    @DisplayName("La lista original no se modifica")
    void noModificaOriginal() {
        List<Publicacion> entrada = new ArrayList<>(List.of(pub(3, 5, 0, 0), pub(1, 0, 0, 0), pub(2, 9, 0, 0)));
        List<Publicacion> copia = new ArrayList<>(entrada);
        MuroOrdenador.masRecientesPrimero(entrada);
        assertEquals(copia, entrada);
    }

    @Test
    @DisplayName("5000 publicaciones quedan ordenadas, completas y sin repetir")
    void volumen() {
        Random azar = new Random(42);
        List<Publicacion> entrada = new ArrayList<>();
        for (int id = 1; id <= 5000; id++) {
            entrada.add(pub(id, azar.nextInt(200), azar.nextInt(50), azar.nextInt(20)));
        }
        for (CriterioMuro criterio : List.of(CriterioMuro.MAS_RECIENTES, CriterioMuro.MAS_RELEVANTES)) {
            List<Publicacion> orden = MuroOrdenador.ordenar(entrada, criterio);
            assertEquals(5000, orden.size());
            Set<Integer> vistos = new HashSet<>();
            for (int i = 0; i < orden.size(); i++) {
                vistos.add(orden.get(i).id());
                if (i > 0) {
                    assertTrue(criterio.comparar(orden.get(i - 1), orden.get(i)) < 0);
                }
            }
            assertEquals(5000, vistos.size());
        }
    }
}
