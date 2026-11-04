package redsocial.difusion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import redsocial.grafo.Grafo;
import redsocial.grafo.Grafo.Arista;

/**
 * B3 · Búsqueda de cadenas alternativas de difusión con restricciones.
 *
 * <p><b>Estrategia: Backtracking.</b> Se explora la red en profundidad (DFS) a partir del usuario
 * de origen llevando el camino parcial de usuarios y un arreglo {@code visitado[]} para no repetir
 * ningún usuario en la misma cadena. En cada paso, para cada vecino no visitado se toma la decisión
 * de avanzar por ese vínculo (ELEGIR), continuar recursivamente desde el vecino (EXPLORAR) y, al
 * volver, restaurar el estado quitando al vecino del camino y desmarcándolo (DESHACER / backtrack)
 * para probar el siguiente vínculo.</p>
 *
 * <p><b>Restricciones.</b> Ninguna cadena repite usuarios ni pasa por un usuario excluido. Además,
 * una cadena solo es válida si su tiempo total no supera {@code tiempoMaximo} y si no tiene más de
 * {@code maxIntermediarios} intermediarios (usuarios de la cadena sin contar origen ni destino).
 * Estos dos límites se verifican al registrar cada cadena que llega al destino.</p>
 *
 * <p><b>Justificación (por qué Backtracking y no Dijkstra).</b> El algoritmo de Dijkstra (Greedy)
 * encuentra únicamente <i>una</i> cadena óptima (la de menor tiempo total) y descarta el resto de
 * los caminos. En cambio, esta funcionalidad requiere <i>enumerar todas las cadenas simples</i> que
 * conectan el origen con el destino para comparar alternativas y determinar cuál de las válidas es
 * la más rápida. Backtracking permite recorrer sistemáticamente todas las trayectorias posibles.</p>
 *
 * <p><b>Complejidad temporal.</b> En el peor caso (un grafo denso donde casi todos los usuarios se
 * conectan entre sí), la cantidad de caminos simples entre dos vértices es del orden de (V - 2)!,
 * por lo que la complejidad en el peor caso es <b>O(V!)</b>. Espacio auxiliar: O(V) para la pila de
 * recursión, el camino actual y el arreglo de visitados (sin contar la memoria de las cadenas
 * encontradas).</p>
 */
public final class CadenasAlternativas {

    /**
     * Una cadena simple de difusión desde el origen hasta el destino junto con su tiempo total.
     *
     * @param usuarios nombres de los usuarios de la cadena en orden desde el origen hasta el destino
     * @param minutos  tiempo total de transmisión acumulado en minutos
     */
    public record CadenaAlternativa(List<String> usuarios, int minutos) { }

    /**
     * Resultado de la búsqueda de cadenas alternativas.
     *
     * @param cadenas   todas las cadenas encontradas, en el orden de hallazgo del recorrido DFS
     * @param masRapida la cadena de menor tiempo total entre las encontradas (ante empate, la
     *                  primera hallada), o {@code null} si no se encontró ninguna cadena
     */
    public record Resultado(List<CadenaAlternativa> cadenas, CadenaAlternativa masRapida) { }

    private CadenasAlternativas() { }

    /**
     * Enumera las cadenas simples de difusión desde {@code origen} hasta {@code destino}.
     *
     * @param grafo             red de difusión dirigida y ponderada
     * @param origen            nombre del usuario que inicia la difusión
     * @param destino           nombre del usuario que debe recibir el mensaje
     * @param tiempoMaximo      tiempo máximo permitido en minutos para toda la cadena (mayor o igual a 0)
     * @param maxIntermediarios cantidad máxima de usuarios intermedios, sin contar origen ni destino (mayor o igual a 0)
     * @param excluidos         conjunto de nombres de usuarios por los que no puede pasar la cadena
     * @return {@link Resultado} con la lista de cadenas encontradas y la más rápida de ellas
     * @throws IllegalArgumentException si {@code grafo} o {@code excluidos} es nulo, si {@code origen}
     *                                  o {@code destino} no existen en la red, si {@code origen} es igual
     *                                  a {@code destino}, o si {@code tiempoMaximo < 0} o {@code maxIntermediarios < 0}
     */
    public static Resultado buscar(Grafo grafo, String origen, String destino, int tiempoMaximo,
            int maxIntermediarios, Set<String> excluidos) {
        if (grafo == null || excluidos == null) {
            throw new IllegalArgumentException("El grafo y el conjunto de excluidos no pueden ser nulos");
        }
        if (tiempoMaximo < 0) {
            throw new IllegalArgumentException("El tiempo máximo no puede ser negativo: " + tiempoMaximo);
        }
        if (maxIntermediarios < 0) {
            throw new IllegalArgumentException(
                    "El máximo de intermediarios no puede ser negativo: " + maxIntermediarios);
        }

        int indiceOrigen = grafo.indiceDe(origen);
        int indiceDestino = grafo.indiceDe(destino);
        if (indiceOrigen == indiceDestino) {
            throw new IllegalArgumentException(
                    "El usuario de origen y el de destino deben ser distintos: " + origen);
        }

        boolean[] visitado = new boolean[grafo.cantidadUsuarios()];
        for (String excluido : excluidos) {
            if (grafo.contiene(excluido)) {
                visitado[grafo.indiceDe(excluido)] = true;
            }
        }

        if (visitado[indiceOrigen] || visitado[indiceDestino]) {
            return new Resultado(List.of(), null);
        }

        List<Integer> camino = new ArrayList<>();
        List<CadenaAlternativa> cadenas = new ArrayList<>();
        CadenaAlternativa[] mejor = new CadenaAlternativa[1];

        visitado[indiceOrigen] = true;
        camino.add(indiceOrigen);

        explorar(grafo, indiceOrigen, indiceDestino, 0, tiempoMaximo, maxIntermediarios,
                visitado, camino, cadenas, mejor);

        return new Resultado(Collections.unmodifiableList(cadenas), mejor[0]);
    }

    private static void explorar(Grafo grafo, int actual, int destino, int tiempoAcumulado,
            int tiempoMaximo, int maxIntermediarios, boolean[] visitado, List<Integer> camino,
            List<CadenaAlternativa> cadenas, CadenaAlternativa[] mejor) {
        if (actual == destino) {
            registrar(grafo, camino, tiempoAcumulado, tiempoMaximo, maxIntermediarios, cadenas, mejor);
            return;
        }

        for (Arista arista : grafo.vecinos(actual)) {
            int vecino = arista.destino();
            if (visitado[vecino]) {
                continue;
            }

            // ELEGIR
            visitado[vecino] = true;
            camino.add(vecino);

            // EXPLORAR
            explorar(grafo, vecino, destino, tiempoAcumulado + arista.minutos(), tiempoMaximo,
                    maxIntermediarios, visitado, camino, cadenas, mejor);

            // DESHACER (backtrack)
            camino.remove(camino.size() - 1);
            visitado[vecino] = false;
        }
    }

    /**
     * Registra la cadena actual si cumple los límites de tiempo e intermediarios.
     * Intermediarios = usuarios de la cadena sin contar el origen ni el destino.
     */
    private static void registrar(Grafo grafo, List<Integer> camino, int minutos, int tiempoMaximo,
            int maxIntermediarios, List<CadenaAlternativa> cadenas, CadenaAlternativa[] mejor) {
        int intermediarios = camino.size() - 2;
        if (minutos > tiempoMaximo || intermediarios > maxIntermediarios) {
            return; // la cadena llega al destino pero no cumple los límites: se descarta
        }

        List<String> nombres = new ArrayList<>(camino.size());
        for (int indice : camino) {
            nombres.add(grafo.nombreDe(indice));
        }
        CadenaAlternativa nueva = new CadenaAlternativa(Collections.unmodifiableList(nombres), minutos);
        cadenas.add(nueva);
        if (mejor[0] == null || minutos < mejor[0].minutos()) {
            mejor[0] = nueva;
        }
    }
}
