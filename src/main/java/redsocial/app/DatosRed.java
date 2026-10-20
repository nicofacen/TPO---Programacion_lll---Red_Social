package redsocial.app;

import redsocial.grafo.Grafo;

/**
 * Red de difusión de ejemplo compartida por las opciones 2, 4 y 6.
 */
public final class DatosRed {

    private DatosRed() { }

    /** Red dirigida de 8 usuarios. Hugo solo transmite (nadie le transmite a él). */
    public static Grafo red() {
        Grafo g = new Grafo();
        g.agregarVinculo("Ana", "Bruno", 10);
        g.agregarVinculo("Ana", "Carla", 5);
        g.agregarVinculo("Carla", "Bruno", 3);
        g.agregarVinculo("Bruno", "Diego", 7);
        g.agregarVinculo("Carla", "Diego", 15);
        g.agregarVinculo("Carla", "Eva", 20);
        g.agregarVinculo("Diego", "Eva", 4);
        g.agregarVinculo("Eva", "Facu", 6);
        g.agregarVinculo("Diego", "Facu", 12);
        g.agregarVinculo("Facu", "Gina", 2);
        g.agregarVinculo("Eva", "Gina", 9);
        g.agregarVinculo("Bruno", "Eva", 25);
        g.agregarVinculo("Gina", "Carla", 8);
        g.agregarVinculo("Hugo", "Ana", 1);
        return g;
    }
}
