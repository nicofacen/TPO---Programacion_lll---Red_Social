package redsocial.muro;

import java.util.ArrayList;
import java.util.List;
import redsocial.modelo.Publicacion;

/**
 * Ordena el muro de publicaciones con un Merge Sort propio.
 *
 * <p><b>Estrategia: Divide y Conquista.</b> La lista se parte por la mitad (dividir), cada mitad se
 * ordena recursivamente (conquistar) y las dos mitades ordenadas se mezclan en un único segmento
 * ordenado (combinar). Un segmento de 0 o 1 elemento ya está ordenado: es el caso base.</p>
 *
 * <p><b>Justificación.</b> El muro puede crecer mucho, y Merge Sort garantiza O(n log n) aun en el peor
 * caso; Quick Sort puede degradar a O(n²) y no es estable. Además es estable: dos publicaciones
 * equivalentes conservan su orden relativo. No se usa ningún ordenamiento del JDK.</p>
 *
 * <p><b>Complejidad.</b> Recurrencia {@code T(n) = 2·T(n/2) + O(n)}: dos subproblemas de tamaño n/2 y
 * una mezcla lineal. Por el Teorema Maestro (caso 2) resulta <b>O(n log n)</b> en mejor, promedio y
 * peor caso. Espacio auxiliar: O(n) (un único arreglo auxiliar reutilizado) más O(log n) de pila.</p>
 */
public final class MuroOrdenador {

    private MuroOrdenador() { }

    /** Devuelve una lista NUEVA con las publicaciones ordenadas según el criterio; la original no se modifica. */
    public static List<Publicacion> ordenar(List<Publicacion> publicaciones, CriterioMuro criterio) {
        Publicacion[] datos = publicaciones.toArray(new Publicacion[0]);
        Publicacion[] auxiliar = new Publicacion[datos.length];
        mergeSort(datos, auxiliar, 0, datos.length - 1, criterio);
        List<Publicacion> resultado = new ArrayList<>(datos.length);
        for (Publicacion p : datos) {
            resultado.add(p);
        }
        return resultado;
    }

    /** Orden por fecha descendente (la más nueva primero). */
    public static List<Publicacion> masRecientesPrimero(List<Publicacion> publicaciones) {
        return ordenar(publicaciones, CriterioMuro.MAS_RECIENTES);
    }

    /** Orden por relevancia descendente. */
    public static List<Publicacion> masRelevantesPrimero(List<Publicacion> publicaciones) {
        return ordenar(publicaciones, CriterioMuro.MAS_RELEVANTES);
    }

    /** Ordena el segmento {@code datos[inicio..fin]} (ambos inclusive). */
    private static void mergeSort(Publicacion[] datos, Publicacion[] auxiliar, int inicio, int fin,
                                  CriterioMuro criterio) {
        // CASO BASE: un segmento de 0 o 1 elemento ya está ordenado
        if (inicio >= fin) {
            return;
        }
        // DIVIDIR: punto medio del segmento
        int medio = inicio + (fin - inicio) / 2;
        // CONQUISTAR: ordenar recursivamente cada mitad
        mergeSort(datos, auxiliar, inicio, medio, criterio);
        mergeSort(datos, auxiliar, medio + 1, fin, criterio);
        // OPTIMIZACIÓN: si el último de la izquierda ya va antes que el primero de la derecha, no hay nada que mezclar
        if (criterio.comparar(datos[medio], datos[medio + 1]) <= 0) {
            return;
        }
        // COMBINAR: mezclar las dos mitades ordenadas
        combinar(datos, auxiliar, inicio, medio, fin, criterio);
    }

    /** Mezcla {@code datos[inicio..medio]} y {@code datos[medio+1..fin]}, ya ordenadas, con dos punteros. */
    private static void combinar(Publicacion[] datos, Publicacion[] auxiliar, int inicio, int medio, int fin,
                                 CriterioMuro criterio) {
        for (int k = inicio; k <= fin; k++) {
            auxiliar[k] = datos[k];
        }
        int izquierda = inicio;
        int derecha = medio + 1;
        for (int k = inicio; k <= fin; k++) {
            if (izquierda > medio) {
                datos[k] = auxiliar[derecha++];
            } else if (derecha > fin) {
                datos[k] = auxiliar[izquierda++];
            } else if (criterio.comparar(auxiliar[derecha], auxiliar[izquierda]) < 0) {
                // solo se toma de la derecha si es estrictamente anterior: así el orden es estable
                datos[k] = auxiliar[derecha++];
            } else {
                datos[k] = auxiliar[izquierda++];
            }
        }
    }
}
