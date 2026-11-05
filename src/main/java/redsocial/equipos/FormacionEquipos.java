package redsocial.equipos;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redsocial.modelo.Estudiante;

/**
 * B1 · Formación de equipos con restricciones.
 *
 * <p><b>Estrategia: Backtracking.</b> Se recorren los candidatos en orden de posición y, para cada uno, se
 * toman dos decisiones: <i>incluirlo</i> en el equipo (ELEGIR, EXPLORAR y luego DESHACER) o <i>excluirlo</i>.
 * La rama "incluir" se explora antes que la rama "excluir". Cuando el equipo llega al tamaño pedido, es válido
 * solo si entre todos cubren todas las habilidades requeridas. Los integrantes obligatorios se incluyen desde
 * el inicio y quedan fuera de la búsqueda.</p>
 *
 * <p><b>Sin repetidos.</b> Como las decisiones se toman siempre hacia posiciones mayores, cada combinación de
 * integrantes se genera una sola vez: el mismo equipo nunca aparece con sus integrantes en otro orden.</p>
 *
 * <p><b>Justificación.</b> La consigna pide <i>todos</i> los equipos válidos y las restricciones
 * (incompatibilidades, obligatorios, habilidades) se verifican de a una a medida que se arma el equipo. Greedy o
 * Programación Dinámica construyen <i>una</i> solución (la mejor según un criterio), no la enumeración completa.
 * Backtracking recorre el espacio de combinaciones y permite abandonar temprano las ramas que no pueden
 * terminar en un equipo válido.</p>
 *
 * <p><b>Podas</b> (ninguna descarta un equipo válido):</p>
 * <ul>
 *   <li><b>Incompatibilidad:</b> no se incluye a un candidato incompatible con alguien ya elegido. Cualquier
 *       equipo que lo contenga junto a ese integrante viola una restricción, así que la rama no tiene soluciones.</li>
 *   <li><b>Cantidad:</b> si los candidatos que quedan por decidir no alcanzan para completar el equipo, la rama
 *       nunca llega al tamaño pedido.</li>
 *   <li><b>Habilidades alcanzables:</b> si una habilidad requerida todavía sin cubrir no la tiene ningún candidato
 *       restante, ningún equipo de esa rama podrá cubrirla. Para saberlo en O(h) se precalcula, de atrás hacia
 *       adelante, qué habilidades requeridas aportan los candidatos desde cada posición, y la cobertura del equipo
 *       se mantiene con contadores que se actualizan al elegir y al deshacer.</li>
 * </ul>
 *
 * <p><b>Complejidad temporal.</b> Sin podas, el árbol incluir/excluir tiene O(2^n) nodos. Con la poda por cantidad
 * solo se abren ramas que pueden llegar a k integrantes: en el peor caso (sin restricciones que poden) el árbol tiene
 * exactamente 2 · C(n + 1, k) - 1 nodos y cada uno cuesta O(h + k), es decir <b>O(C(n + 1, k) · (h + k))</b>.
 * Las podas por incompatibilidad y por habilidades reducen ese número en la práctica, pero no el peor caso.
 * Espacio: O(n · h + n²) para las tablas y la matriz de incompatibles (h = habilidades requeridas) y O(n) de pila.</p>
 */
public final class FormacionEquipos {

    /** Par de estudiantes que no pueden integrar un mismo equipo. Es simétrica: (a, b) equivale a (b, a). */
    public record Incompatibilidad(String a, String b) { }

    private FormacionEquipos() { }

    /**
     * Devuelve todos los equipos válidos, en orden de hallazgo.
     *
     * @param estudiantes           estudiantes disponibles (nombres sin repetir)
     * @param tamano                cantidad de integrantes de cada equipo, mayor que 0
     * @param habilidadesRequeridas habilidades que el equipo debe cubrir entre todos
     * @param obligatorios          nombres que deben estar en todos los equipos
     * @param incompatibilidades    pares que no pueden coincidir en un equipo
     * @return cada equipo es una lista de nombres en el mismo orden en que figuran en {@code estudiantes};
     *         lista vacía si no hay ninguno
     * @throws IllegalArgumentException si {@code tamano <= 0}, hay nombres repetidos o un nombre desconocido
     */
    public static List<List<String>> encontrarEquipos(List<Estudiante> estudiantes, int tamano,
            Set<String> habilidadesRequeridas, Set<String> obligatorios, List<Incompatibilidad> incompatibilidades) {
        if (estudiantes == null || habilidadesRequeridas == null || obligatorios == null || incompatibilidades == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser nulos");
        }
        if (tamano <= 0) {
            throw new IllegalArgumentException("El tamaño del equipo debe ser mayor que 0 (se recibió " + tamano + ")");
        }
        int n = estudiantes.size();

        // Nombre -> posición, detectando repetidos
        Map<String, Integer> posicion = new HashMap<>();
        for (int i = 0; i < n; i++) {
            Estudiante e = estudiantes.get(i);
            if (e == null) {
                throw new IllegalArgumentException("La lista de estudiantes no puede contener nulos");
            }
            if (posicion.put(e.nombre(), i) != null) {
                throw new IllegalArgumentException("Nombre de estudiante repetido: " + e.nombre());
            }
        }

        // Incompatibilidades como matriz simétrica
        boolean[][] incompatibles = new boolean[n][n];
        for (Incompatibilidad par : incompatibilidades) {
            if (par == null) {
                throw new IllegalArgumentException("La lista de incompatibilidades no puede contener nulos");
            }
            int a = buscarPosicion(posicion, par.a());
            int b = buscarPosicion(posicion, par.b());
            if (a == b) {
                throw new IllegalArgumentException("Un estudiante no puede ser incompatible consigo mismo: " + par.a());
            }
            incompatibles[a][b] = true;
            incompatibles[b][a] = true;
        }

        // Obligatorios, en el orden de la lista de estudiantes
        boolean[] esObligatorio = new boolean[n];
        for (String nombre : obligatorios) {
            esObligatorio[buscarPosicion(posicion, nombre)] = true;
        }

        if (tamano > n || obligatorios.size() > tamano) {
            return new ArrayList<>();
        }

        // Habilidades requeridas -> índice 0..h-1
        Map<String, Integer> indiceHabilidad = new HashMap<>();
        for (String habilidad : habilidadesRequeridas) {
            indiceHabilidad.put(habilidad, indiceHabilidad.size());
        }

        Busqueda busqueda = new Busqueda(estudiantes, tamano, indiceHabilidad, esObligatorio, incompatibles);
        if (!busqueda.incluirObligatorios()) {
            return new ArrayList<>(); // dos obligatorios son incompatibles entre sí
        }
        busqueda.buscar(0);
        return busqueda.equipos;
    }

    private static int buscarPosicion(Map<String, Integer> posicion, String nombre) {
        Integer i = posicion.get(nombre);
        if (i == null) {
            throw new IllegalArgumentException("Estudiante desconocido: " + nombre);
        }
        return i;
    }

    /** Estado de la búsqueda: equipo parcial, cobertura de habilidades y tablas precalculadas. */
    private static final class Busqueda {

        private final List<Estudiante> estudiantes;
        private final int tamano;
        private final int cantidadHabilidades;
        private final boolean[][] incompatibles;

        /** Habilidades requeridas que tiene cada estudiante (por posición original). */
        private final BitSet[] aporta;
        /** Posiciones originales de los obligatorios, en orden creciente. */
        private final int[] fijos;
        /** Posiciones originales de los candidatos (los no obligatorios), en orden creciente. */
        private final int[] candidatos;
        /** sufijo[p] = habilidades requeridas que aportan los candidatos p, p+1, ..., m-1. */
        private final BitSet[] sufijo;

        /** Cuántos integrantes elegidos tienen cada habilidad requerida. */
        private final int[] cobertura;
        private int sinCubrir;

        /** Integrantes elegidos (posiciones originales): primero los obligatorios, luego los candidatos. */
        private final int[] elegidos;
        private int cantidadElegidos;
        /** Cuántos de los elegidos son candidatos (los últimos, en orden creciente de posición). */
        private int candidatosElegidos;

        private final List<List<String>> equipos = new ArrayList<>();

        Busqueda(List<Estudiante> estudiantes, int tamano, Map<String, Integer> indiceHabilidad,
                 boolean[] esObligatorio, boolean[][] incompatibles) {
            this.estudiantes = estudiantes;
            this.tamano = tamano;
            this.cantidadHabilidades = indiceHabilidad.size();
            this.incompatibles = incompatibles;
            int n = estudiantes.size();

            aporta = new BitSet[n];
            int cantidadFijos = 0;
            for (int i = 0; i < n; i++) {
                aporta[i] = new BitSet(cantidadHabilidades);
                for (String habilidad : estudiantes.get(i).habilidades()) {
                    Integer h = indiceHabilidad.get(habilidad);
                    if (h != null) {
                        aporta[i].set(h);
                    }
                }
                if (esObligatorio[i]) {
                    cantidadFijos++;
                }
            }

            fijos = new int[cantidadFijos];
            candidatos = new int[n - cantidadFijos];
            int f = 0;
            int c = 0;
            for (int i = 0; i < n; i++) {
                if (esObligatorio[i]) {
                    fijos[f++] = i;
                } else {
                    candidatos[c++] = i;
                }
            }

            // Precálculo de atrás hacia adelante para la poda por habilidades
            sufijo = new BitSet[candidatos.length + 1];
            sufijo[candidatos.length] = new BitSet(cantidadHabilidades);
            for (int p = candidatos.length - 1; p >= 0; p--) {
                sufijo[p] = (BitSet) sufijo[p + 1].clone();
                sufijo[p].or(aporta[candidatos[p]]);
            }

            cobertura = new int[cantidadHabilidades];
            sinCubrir = cantidadHabilidades;
            elegidos = new int[tamano];
        }

        /** Agrega los obligatorios al equipo. Devuelve false si dos de ellos son incompatibles entre sí. */
        boolean incluirObligatorios() {
            for (int fijo : fijos) {
                if (esIncompatibleConElegidos(fijo)) {
                    return false;
                }
                agregar(fijo);
            }
            return true;
        }

        /** Decide, para el candidato de la posición {@code p} en adelante, si se incluye o se excluye. */
        void buscar(int p) {
            int faltan = tamano - cantidadElegidos;
            if (faltan == 0) {
                // Equipo completo: es válido solo si cubre todas las habilidades requeridas
                if (sinCubrir == 0) {
                    registrarEquipo();
                }
                return;
            }
            // PODA: cantidad (los candidatos que quedan no alcanzan para completar el equipo)
            if (candidatos.length - p < faltan) {
                return;
            }
            // PODA: habilidades (una habilidad requerida sin cubrir no la tiene ningún candidato restante)
            if (!habilidadesAlcanzables(p)) {
                return;
            }

            int candidato = candidatos[p];

            // Rama "incluir"
            // PODA: incompatibilidad (no se incluye a quien choca con alguien ya elegido)
            if (!esIncompatibleConElegidos(candidato)) {
                // ELEGIR
                agregar(candidato);
                candidatosElegidos++;
                // EXPLORAR
                buscar(p + 1);
                // DESHACER (backtrack)
                candidatosElegidos--;
                quitarUltimo();
            }

            // Rama "excluir": se sigue con el próximo candidato sin incluir a este
            buscar(p + 1);
        }

        private boolean habilidadesAlcanzables(int p) {
            for (int h = 0; h < cantidadHabilidades; h++) {
                if (cobertura[h] == 0 && !sufijo[p].get(h)) {
                    return false;
                }
            }
            return true;
        }

        private boolean esIncompatibleConElegidos(int estudiante) {
            for (int i = 0; i < cantidadElegidos; i++) {
                if (incompatibles[estudiante][elegidos[i]]) {
                    return true;
                }
            }
            return false;
        }

        private void agregar(int estudiante) {
            elegidos[cantidadElegidos++] = estudiante;
            BitSet habilidades = aporta[estudiante];
            for (int h = habilidades.nextSetBit(0); h >= 0; h = habilidades.nextSetBit(h + 1)) {
                if (cobertura[h]++ == 0) {
                    sinCubrir--;
                }
            }
        }

        private void quitarUltimo() {
            int estudiante = elegidos[--cantidadElegidos];
            BitSet habilidades = aporta[estudiante];
            for (int h = habilidades.nextSetBit(0); h >= 0; h = habilidades.nextSetBit(h + 1)) {
                if (--cobertura[h] == 0) {
                    sinCubrir++;
                }
            }
        }

        /**
         * Copia el equipo actual con los nombres en el orden de la lista de estudiantes. Obligatorios y candidatos
         * elegidos ya están cada uno en orden creciente de posición, así que alcanza con intercalarlos: O(k).
         */
        private void registrarEquipo() {
            List<String> equipo = new ArrayList<>(tamano);
            int f = 0;
            int c = fijos.length;
            int finCandidatos = fijos.length + candidatosElegidos;
            while (f < fijos.length || c < finCandidatos) {
                if (c >= finCandidatos || (f < fijos.length && elegidos[f] < elegidos[c])) {
                    equipo.add(estudiantes.get(elegidos[f++]).nombre());
                } else {
                    equipo.add(estudiantes.get(elegidos[c++]).nombre());
                }
            }
            equipos.add(equipo);
        }
    }
}
