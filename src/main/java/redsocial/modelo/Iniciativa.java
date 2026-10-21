package redsocial.modelo;

/**
 * Iniciativa universitaria que puede recibir financiamiento.
 *
 * <p>Regla del problema: una iniciativa se financia <b>una sola vez</b> y por el <b>total</b> de su costo.
 * No se puede financiar una parte ni repetirla; o se elige completa o no se elige.</p>
 *
 * @param id      identificador de la iniciativa
 * @param costo   costo entero positivo (mayor que 0)
 * @param impacto puntaje de impacto entero no negativo (0 o más)
 */
public record Iniciativa(int id, int costo, int impacto) {

    public Iniciativa {
        if (costo <= 0) {
            throw new IllegalArgumentException(
                    "La iniciativa #" + id + " debe tener un costo entero positivo (recibido: " + costo + ")");
        }
        if (impacto < 0) {
            throw new IllegalArgumentException(
                    "La iniciativa #" + id + " debe tener un impacto entero no negativo (recibido: " + impacto + ")");
        }
    }
}
