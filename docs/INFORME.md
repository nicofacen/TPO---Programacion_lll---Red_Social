# Informe técnico — Red Social Universitaria para la Colaboración

**Materia:** Programación III (Diseño y Análisis de Algoritmos) — UADE
**Propuesta elegida:** 1 — Red social universitaria para la colaboración
**Integrantes:** Nicolas, Lucas y Fadi Abdala
**Lenguaje:** Java 21 (Maven, JUnit 5)

---

## 1. Resumen de funcionalidades y estrategias

| Etapa | Funcionalidad | Estrategia | Algoritmo | Clase principal | Complejidad temporal | Entrega |
|---|---|---|---|---|---|---|
| 1 | Muro de publicaciones | Divide y Conquista | Merge Sort propio | `muro/MuroOrdenador` | O(n log n) | 1 |
| 1 | Cadena de difusión más rápida | Greedy | Dijkstra con cola de prioridad | `difusion/CadenaMasRapida` | O((V+E) log V) | 1 |
| 1 | Selección de iniciativas | Programación Dinámica | Mochila 0/1 con tabla | `financiamiento/SeleccionIniciativas` | O(n·P) | 1 |
| 2 – A1 | Consultas entre todos los usuarios | Programación Dinámica | Floyd-Warshall | `difusion/ConsultasDifusion` | O(V³) + O(1) por consulta | 2 |
| 2 – B1 | Formación de equipos con restricciones | Backtracking | Incluir/excluir con podas | `equipos/FormacionEquipos` | O(C(n,k)·k) peor caso | 2 |
| 2 – B3 | Cadenas alternativas de difusión | Backtracking | DFS de caminos simples con podas | `difusion/CadenasAlternativas` | O(V!) peor caso | 2 |

Las cuatro estrategias que exige la consigna (Divide y Conquista, Greedy, Programación Dinámica y Backtracking) están aplicadas y se reconocen en el código de cada clase.

## 2. Decisiones y supuestos del equipo

1. **Relevancia de una publicación.** La consigna no la define. Se adopta `relevancia = reacciones + 2 × comentarios`, porque un comentario implica más esfuerzo que una reacción. El peso es la constante `Publicacion.PESO_COMENTARIO`.
2. **Un vínculo por par ordenado de usuarios.** Si se carga más de uno, `Grafo` conserva el de menor tiempo: uno más lento nunca mejora una cadena.
3. **Usuarios identificados por nombre**, mapeados internamente a índices enteros para trabajar con arreglos.
4. **Origen igual a destino.** En la cadena más rápida devuelve la cadena de un solo usuario con 0 minutos; en B3 se rechaza.
5. **Intermediarios (B3):** los usuarios de la cadena sin contar origen ni destino.
6. **Datos de ejemplo fijos** dentro de cada consola; el menú pide solo parámetros simples, siempre con valor por defecto.
7. **Salida de consola** con caracteres ASCII para símbolos (`->`, `>=`, `infinito`).
8. **Sin ordenamientos del JDK** en todo el código fuente.

## 3. Funcionalidad 1 — Organización del muro de publicaciones (Divide y Conquista)

**1. Análisis.** Hay que mostrar el muro de una red social con publicaciones (`id`, autor, fecha y hora, reacciones, comentarios) en dos
órdenes: *más recientes primero* y *más relevantes primero*. Los empates se resuelven así: por recencia, gana el `id` menor; por
relevancia, gana la más reciente y, si vuelven a empatar, el `id` menor. La consigna no define "relevancia": el equipo adoptó
`relevancia = reacciones + 2 x comentarios` (un comentario cuesta más esfuerzo que una reacción). El volumen puede crecer, y no se puede
usar ningún ordenamiento del JDK. Objetivo: ordenar `n` publicaciones de forma eficiente, estable y sin modificar la lista original.

**2. Estrategia elegida: Divide y Conquista (Merge Sort).** Se parte la lista a la mitad hasta llegar a segmentos de 0 o 1 elemento
(que ya están ordenados); luego se mezclan de a pares, tomando siempre el primero de cada lado según el criterio.
Ejemplo con 4 elementos y ordenar de menor a mayor `[8, 3, 5, 1]`:

- **Dividir:** `[8, 3]` y `[5, 1]`, y cada uno en `[8] [3]` y `[5] [1]`.
- **Conquistar:** los segmentos de un elemento ya están ordenados.
- **Combinar:** `[8] + [3] -> [3, 8]`; `[5] + [1] -> [1, 5]`; `[3, 8] + [1, 5] -> [1, 3, 5, 8]`.

**3. Justificación.** El muro crece con el tiempo, así que se necesita O(n log n) *garantizado*. Alternativas descartadas:
inserción, selección y burbuja son O(n²), inviables con miles de publicaciones; Quick Sort es O(n²) en el peor caso y no es estable;
Heap Sort es O(n log n) pero no es estable. Merge Sort es estable (los equivalentes mantienen su orden) y su costo no depende de los
datos. Además, `Arrays.sort`, `Collections.sort`, `List.sort`, `sorted()`, `TreeMap`, `TreeSet` y `Comparator` están prohibidos para ordenar.

**4. Dónde se ve en el código.** Clase `redsocial.muro.MuroOrdenador`:

| Fase | Método / lugar |
|---|---|
| Caso base (0 o 1 elemento) | `mergeSort`, `if (inicio >= fin) return;` |
| `// DIVIDIR` | `mergeSort`: `medio = inicio + (fin - inicio) / 2` |
| `// CONQUISTAR` | `mergeSort`: dos llamadas recursivas, una por mitad |
| `// COMBINAR` | `combinar`: copia al auxiliar y mezcla con dos punteros; toma de la derecha solo si `comparar(derecha, izquierda) < 0` (estable) |

El criterio es la interfaz propia `CriterioMuro` (`MAS_RECIENTES`, `MAS_RELEVANTES`), y la fórmula de relevancia está en
`Publicacion.relevancia()`. Si las dos mitades ya están en orden (`comparar(datos[medio], datos[medio + 1]) <= 0`) no se combinan.

**5. Casos de prueba** (`MuroOrdenadorTest` y `PublicacionTest`):

| Entrada | Resultado esperado |
|---|---|
| Lista vacía / un elemento | Se devuelve igual |
| Recientes: `pub(3,5h), pub(1,0h), pub(2,5h), pub(4,9h)` | ids `[4, 2, 3, 1]` |
| Relevantes: `pub(1,0,10,0)` (rel. 10), `pub(2,5,4,3)` (rel. 10), `pub(3,5,4,3)` (igual a la 2), `pub(4,1,50,0)` (rel. 50) | ids `[4, 2, 3, 1]` |
| Ordenar una lista | La original no se modifica |
| 5000 publicaciones al azar (semilla 42), ambos criterios | Cada par adyacente cumple `comparar < 0`; están los 5000 ids sin repetir |
| `Publicacion(12 reacciones, 3 comentarios).relevancia()` | 18 |
| Autor vacío, fecha nula, valores negativos | `IllegalArgumentException` |

**6. Complejidad temporal.** Cada llamada resuelve dos subproblemas de tamaño `n/2` y mezcla en tiempo lineal:
`T(n) = 2·T(n/2) + O(n)`. Por el Teorema Maestro (`a = 2`, `b = 2`, `f(n) = n = n^(log_2 2)`, caso 2) resulta **O(n log n)**.
Con el árbol de recursión: hay `log n` niveles y cada nivel cuesta `O(n)`. Vale en mejor, promedio y peor caso. Espacio: O(n) por el
arreglo auxiliar, más O(log n) de pila.

**Ejemplo de ejecución** (opción 1 del `--demo`):

```
Relevancia = reacciones + 2 x comentarios. Desempate: más reciente y luego id menor.

Más recientes primero:
ID   Autor    Fecha             Reacciones Comentarios  Relevancia
6    Facu     03/10/2026 09:00           3           0           3
5    Eva      02/10/2026 12:00          30          15          60
4    Diego    02/10/2026 09:00           8           1          10
2    Bruno    01/10/2026 14:00          40          10          60
3    Carla    01/10/2026 14:00          25           5          35
7    Gina     01/10/2026 14:00          25           5          35
1    Ana      01/10/2026 09:00          12           3          18
Orden por recencia (ids): 6, 5, 4, 2, 3, 7, 1

Más relevantes primero:
ID   Autor    Fecha             Reacciones Comentarios  Relevancia
5    Eva      02/10/2026 12:00          30          15          60
2    Bruno    01/10/2026 14:00          40          10          60
3    Carla    01/10/2026 14:00          25           5          35
7    Gina     01/10/2026 14:00          25           5          35
1    Ana      01/10/2026 09:00          12           3          18
4    Diego    02/10/2026 09:00           8           1          10
6    Facu     03/10/2026 09:00           3           0           3
Orden por relevancia (ids): 5, 2, 3, 7, 1, 4, 6
```

La publicación 5 queda antes que la 2: ambas tienen relevancia 60, pero la 5 es más reciente. La 3 queda antes que la 7: son idénticas
(misma fecha y relevancia 35) y gana el `id` menor.

## 4. Funcionalidad 2 — Cadena de difusión más rápida (Greedy)

_Pendiente — Entrega 1 — responsable: Lucas_

## 5. Funcionalidad 3 — Selección de iniciativas para financiar (Programación Dinámica)

_Pendiente — Entrega 1 — responsable: Fadi_

## 6. A1 — Consultas de difusión entre todos los usuarios (Programación Dinámica)

_Pendiente — Entrega 2 — responsable: Lucas_

## 7. B1 — Formación de equipos con restricciones (Backtracking)

_Pendiente — Entrega 2 — responsable: Nicolas_

## 8. B3 — Cadenas alternativas de difusión (Backtracking)

_Pendiente — Entrega 2 — responsable: Fadi_

## 9. Verificación y pruebas

_Pendiente — Entrega 2 — responsable: Fadi_
