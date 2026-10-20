package redsocial.grafo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Red de difusión de la plataforma: un grafo <b>dirigido y ponderado</b> donde cada usuario es un
 * vértice y cada vínculo habilitado es una arista con un tiempo de transmisión positivo en minutos.
 *
 * <p><b>Representación: lista de adyacencia.</b> Cada usuario guarda únicamente la lista de sus
 * vínculos salientes. Se eligió esta representación en lugar de una matriz de adyacencia porque las
 * redes sociales son <i>ralas</i>: la cantidad de vínculos reales (E) es muy chica frente a todos los
 * pares posibles (V²). Con lista de adyacencia el espacio es O(V + E) y recorrer los vecinos de un
 * usuario cuesta O(grado), mientras que una matriz ocuparía O(V²) y recorrer una fila costaría O(V)
 * aunque el usuario tuviera un solo vínculo. Esto es exactamente lo que necesitan los recorridos de
 * Dijkstra y de backtracking que trabajan sobre esta red.</p>
 *
 * <p>Los usuarios se identifican por nombre y se traducen internamente a índices consecutivos
 * (0, 1, 2…) en orden de alta, para que los algoritmos puedan trabajar con arreglos.</p>
 *
 * <p>Complejidades: {@code agregarUsuario} y {@code indiceDe} O(1) promedio (tabla de hash);
 * {@code agregarVinculo} O(grado del origen); {@code vecinos} y {@code usuarios} O(1) (devuelven vistas).</p>
 */
public final class Grafo {

    /** Vínculo saliente: usuario destino (por índice) y tiempo de transmisión en minutos. */
    public record Arista(int destino, int minutos) { }

    /** Nombre de usuario a índice. Es un {@link LinkedHashMap} para conservar el orden de alta. */
    private final Map<String, Integer> indice = new LinkedHashMap<>();

    /** Nombres de los usuarios: la posición i guarda el nombre del usuario de índice i. */
    private final List<String> nombres = new ArrayList<>();

    /** Lista de adyacencia: la posición i guarda los vínculos salientes del usuario de índice i. */
    private final List<List<Arista>> adyacencia = new ArrayList<>();

    /**
     * Da de alta un usuario y devuelve su índice. Es <b>idempotente</b>: si el usuario ya existe
     * devuelve el índice que ya tenía, sin duplicarlo ni perder sus vínculos.
     *
     * @param nombre nombre del usuario; no puede ser null ni estar en blanco
     * @return índice del usuario (0, 1, 2… en orden de alta)
     * @throws IllegalArgumentException si el nombre es null o está en blanco
     */
    public int agregarUsuario(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
        }
        Integer existente = indice.get(nombre);
        if (existente != null) {
            return existente;
        }
        int nuevo = nombres.size();
        indice.put(nombre, nuevo);
        nombres.add(nombre);
        adyacencia.add(new ArrayList<>());
        return nuevo;
    }

    /**
     * Agrega el vínculo dirigido {@code origen -> destino} con un tiempo de transmisión en minutos.
     * Los usuarios que todavía no existan se dan de alta automáticamente.
     *
     * <p>El vínculo tiene un solo sentido: agregar {@code A -> B} no habilita {@code B -> A}.</p>
     *
     * <p><b>Un solo vínculo por par ordenado.</b> Si el par {@code (origen, destino)} ya tenía un
     * vínculo, se conserva el de <b>menor</b> tiempo: uno más lento nunca puede mejorar una cadena de
     * difusión (los tiempos se suman a lo largo de la cadena), así que guardarlo solo generaría
     * cadenas duplicadas con los mismos usuarios y un tiempo peor. Si el tiempo nuevo es menor,
     * reemplaza al anterior; si es mayor o igual, se descarta.</p>
     *
     * @param origen  usuario que transmite
     * @param destino usuario que recibe
     * @param minutos tiempo estimado de transmisión; tiene que ser positivo
     * @throws IllegalArgumentException si {@code minutos <= 0}, si origen y destino son el mismo
     *                                  usuario, o si algún nombre es null o está en blanco
     */
    public void agregarVinculo(String origen, String destino, int minutos) {
        if (minutos <= 0) {
            throw new IllegalArgumentException("El tiempo del vínculo tiene que ser positivo: " + minutos);
        }
        if (origen != null && origen.equals(destino)) {
            throw new IllegalArgumentException("Un usuario no puede tener un vínculo consigo mismo: " + origen);
        }
        int desde = agregarUsuario(origen);
        int hasta = agregarUsuario(destino);
        List<Arista> salientes = adyacencia.get(desde);
        for (int i = 0; i < salientes.size(); i++) {
            if (salientes.get(i).destino() == hasta) {
                // El par ya tenía un vínculo: se queda el de menor tiempo.
                if (minutos < salientes.get(i).minutos()) {
                    salientes.set(i, new Arista(hasta, minutos));
                }
                return;
            }
        }
        salientes.add(new Arista(hasta, minutos));
    }

    /** Cantidad de usuarios dados de alta en la red. */
    public int cantidadUsuarios() {
        return nombres.size();
    }

    /** Indica si el usuario está en la red. Un nombre null o en blanco devuelve {@code false}. */
    public boolean contiene(String nombre) {
        return nombre != null && indice.containsKey(nombre);
    }

    /**
     * Índice interno del usuario.
     *
     * @throws IllegalArgumentException si el usuario no está en la red
     */
    public int indiceDe(String nombre) {
        Integer i = nombre == null ? null : indice.get(nombre);
        if (i == null) {
            throw new IllegalArgumentException("El usuario '" + nombre + "' no existe en la red");
        }
        return i;
    }

    /**
     * Nombre del usuario que ocupa ese índice.
     *
     * @throws IllegalArgumentException si el índice está fuera de rango
     */
    public String nombreDe(int indice) {
        if (indice < 0 || indice >= nombres.size()) {
            throw new IllegalArgumentException("No existe un usuario con índice " + indice);
        }
        return nombres.get(indice);
    }

    /**
     * Vínculos salientes del usuario, en orden de inserción, como lista de <b>solo lectura</b>
     * (los algoritmos recorren la red pero no la modifican).
     *
     * @throws IllegalArgumentException si el índice está fuera de rango
     */
    public List<Arista> vecinos(int indice) {
        if (indice < 0 || indice >= adyacencia.size()) {
            throw new IllegalArgumentException("No existe un usuario con índice " + indice);
        }
        return Collections.unmodifiableList(adyacencia.get(indice));
    }

    /** Nombres de todos los usuarios, en orden de índice, como lista de solo lectura. */
    public List<String> usuarios() {
        return Collections.unmodifiableList(nombres);
    }
}
