# Informe técnico — Red Social Universitaria para la Colaboración

**Materia:** Programación III (Diseño y Análisis de Algoritmos) — UADE
**Propuesta elegida:** 1 — Red social universitaria para la colaboración
**Integrantes:** Nicolas Facendini, Lucas Felice y Fadi Abdala
**Lenguaje:** Java 21 (Maven, JUnit 5)

---

## 1. Resumen de funcionalidades y estrategias

| Etapa | Funcionalidad | Estrategia | Algoritmo | Clase principal | Complejidad temporal | Entrega |
|---|---|---|---|---|---|---|
| 1 | Muro de publicaciones | Divide y Conquista | Merge Sort propio | `muro/MuroOrdenador` | O(n log n) | 1 |
| 1 | Cadena de difusión más rápida | Greedy | Dijkstra con cola de prioridad | `difusion/CadenaMasRapida` | O((V+E) log V) | 1 |
| 1 | Selección de iniciativas | Programación Dinámica | Mochila 0/1 con tabla | `financiamiento/SeleccionIniciativas` | O(n·P) | 1 |
| 2 – A1 | Consultas entre todos los usuarios | Programación Dinámica | Floyd-Warshall | `difusion/ConsultasDifusion` | O(V³) + O(1) por consulta | 2 |
| 2 – B1 | Formación de equipos con restricciones | Backtracking | Incluir/excluir con podas | `equipos/FormacionEquipos` | O(C(n+1,k)·(h+k)) peor caso | 2 |
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

### 4.1 Análisis del problema

**Datos.** Una red de usuarios donde cada vínculo habilitado va de un usuario a otro (`origen -> destino`) y tiene un tiempo estimado de transmisión en minutos. Se reciben además un usuario de origen y uno de destino.

**Restricciones.**

- Los vínculos son **dirigidos**: que Ana le pueda transmitir a Bruno no implica que Bruno le pueda transmitir a Ana.
- Los tiempos son **enteros positivos** y **fijos**, y se **suman** a lo largo de la cadena.
- Puede no existir ninguna cadena entre los dos usuarios, y hay que informarlo.
- Entre un mismo par ordenado de usuarios se conserva un solo vínculo: el de menor tiempo (ver sección 2, supuesto 2).

**Objetivo.** Encontrar la cadena de **menor tiempo total** y mostrar tres cosas: la cadena, los usuarios que la integran y el tiempo total. Además, la consigna pide "visualizar las principales decisiones tomadas durante el cálculo".

### 4.2 Estrategia elegida: Greedy (algoritmo de Dijkstra)

La estrategia es **Greedy**: en cada paso se toma la mejor decisión disponible en ese momento y **no se vuelve atrás**.

Funciona así, en palabras simples:

1. El origen arranca con tiempo acumulado 0 y todos los demás usuarios en "infinito" (todavía no se sabe cómo llegar a ellos).
2. De todos los usuarios **no fijados**, se elige el de **menor tiempo acumulado** y se lo **fija**: ese tiempo se declara definitivo. Ésa es la decisión voraz.
3. Se miran los vínculos salientes del usuario que se acaba de fijar. Para cada vecino se pregunta: *¿llegar pasando por acá es más rápido que lo mejor que tenía?* Si la respuesta es sí, se le mejora el tiempo y se anota **por quién** se llegó (el predecesor). Eso es una **relajación**.
4. Se repite hasta fijar el destino. La cadena se reconstruye siguiendo los predecesores desde el destino hacia el origen y dando vuelta la lista.

**Una relajación real de la red de ejemplo.** Bruno recibe primero un tiempo de **10 minutos** (vínculo directo `Ana -> Bruno`). Después se fija Carla con 5 minutos y se evalúa `Carla -> Bruno (3 min)`: `5 + 3 = 8 < 10`, así que el tiempo de Bruno **se mejora a 8** y su predecesor pasa a ser Carla. Por eso la cadena final entra a Bruno por Carla y no directo desde Ana.

También se ve el caso contrario, una relajación **descartada**: con Bruno fijado en 8, se evalúa `Bruno -> Eva (25 min)`; `8 + 25 = 33`, pero Eva ya tenía 25, y como `33 >= 25` el vínculo se descarta.

### 4.3 Justificación de la estrategia

**Por qué la elección voraz es correcta.** Supongamos que `u` es el usuario no fijado con el menor tiempo acumulado conocido. Ninguna otra cadena puede llegar a `u` más rápido: cualquier cadena alternativa tendría que pasar por algún otro usuario no fijado, que ya arranca con un tiempo **mayor o igual** al de `u`, y encima sumarle los minutos positivos del resto del camino. Entonces el tiempo de `u` ya es el mínimo y no hace falta revisarlo nunca más. Ésta es la propiedad que permite no explorar todas las cadenas posibles.

**Dónde está la condición clave.** Todo el razonamiento se apoya en que los tiempos son **positivos** (lo exige la consigna). Con un tiempo negativo dejaría de valer: un desvío más largo podría terminar sumando menos, y un usuario ya fijado tendría que reabrirse. En ese escenario habría que usar Bellman-Ford.

**Alternativas descartadas.**

| Alternativa | Por qué se descartó |
|---|---|
| Recorrido en amplitud (BFS) | Encuentra la cadena con **menos saltos**, no la más rápida: ignora los minutos. En la red de prueba de los tests devolvería `A -> B -> D` (2 saltos, 5 min) en lugar de `A -> C -> B -> D` (3 saltos, **4 min**). |
| Probar todas las cadenas (backtracking) | Correcto pero de costo factorial: inviable al crecer la red. Es lo que hace B3, pero ahí el objetivo es distinto (enumerar **todas** las cadenas bajo restricciones, no encontrar una sola óptima). |
| Bellman-Ford | También resuelve el problema, pero cuesta O(V·E). Su ventaja —soportar pesos negativos— no se necesita acá, así que sería pagar de más. |
| Floyd-Warshall | Calcula **todos** los pares: O(V³). Para una sola consulta es un desperdicio. Se usa en A1 (sección 6), donde sí hay muchas consultas. |

### 4.4 Dónde se ve en el código

Clase `redsocial.difusion.CadenaMasRapida`, método estático `calcular(Grafo grafo, String origen, String destino)`, que devuelve un `redsocial.difusion.ResultadoCamino`.

| Fase de la estrategia | Dónde está en el código |
|---|---|
| Estado del algoritmo | Arreglos `dist[]` (tiempo acumulado conocido, inicializado en `INFINITO`), `predecesor[]` (en `-1`) y `fijado[]`; `dist[inicio] = 0`. |
| Candidatos ordenados por costo | `PriorityQueue<int[]>` con entradas `{tiempo, usuario}`: el heap siempre entrega el candidato más barato en O(log V). |
| **Decisión greedy** | El bloque marcado `// DECISIÓN GREEDY`: `fijado[u] = true`. Las entradas obsoletas de la cola (usuarios ya fijados) se saltean con el `continue` previo. |
| **Relajación** | El bloque marcado `// RELAJACIÓN`: `if (dist[u] + vinculo.minutos() < dist[v])` actualiza `dist[v]`, `predecesor[v] = u` y encola el nuevo candidato. |
| Corte temprano | Al fijar el destino se hace `break`: su tiempo ya es definitivo. |
| Reconstrucción de la cadena | `reconstruirCadena`: recorre `predecesor` desde el destino hasta el origen y da vuelta la lista. |
| Caso sin cadena | Si `dist[fin]` quedó en `INFINITO` se devuelve `ResultadoCamino.noAlcanzable(traza)`. |
| Decisiones visibles | La lista `traza`: una línea de `Inicio`, una por cada usuario fijado, una por cada relajación (aceptada o descartada) y una de cierre. |

La estructura de datos es `redsocial.grafo.Grafo`, con **lista de adyacencia** (espacio O(V + E), recorrer los vecinos de un usuario cuesta O(grado)), elegida porque una red social es rala: hay muchos menos vínculos reales que pares posibles de usuarios.

> **Nota sobre el `Comparator` de la cola de prioridad.** No ordena ninguna colección: solo le indica al *heap* binario cuál de dos candidatos es más barato, para poder extraer el mínimo. El ordenamiento que la consigna exige escribir a mano es el del muro de publicaciones (sección 3), que no usa nada del JDK.

### 4.5 Casos de prueba

`CadenaMasRapidaTest` usa la red `A->B 4`, `A->C 1`, `C->B 2`, `B->D 1`, `C->D 7` más un usuario `Aislado` sin vínculos.

| Entrada | Resultado esperado | Qué verifica |
|---|---|---|
| `A -> D` | `[A, C, B, D]`, 4 min | Gana el menor tiempo, no la cadena con menos saltos (`A -> B -> D` cuesta 5). |
| `A -> Aislado` | `alcanzable = false`, cadena vacía, 0 min | Se informa cuando no existe cadena. |
| `D -> A` | `alcanzable = false` | Se respeta el sentido de los vínculos. |
| `A -> A` | `[A]`, 0 min | Origen igual a destino. |
| `A -> D` (traza) | Primera línea `Inicio:` y al menos una línea `Relajación` | Las decisiones del cálculo quedan registradas. |
| `A -> Nadie` / `Nadie -> A` | `IllegalArgumentException` | Usuario inexistente. |

`GrafoTest` cubre la estructura sobre la que corre el algoritmo:

| Entrada | Resultado esperado | Qué verifica |
|---|---|---|
| `agregarVinculo("A","B",5)` | 2 usuarios; A con 1 vecino, B con 0 | Los vínculos son dirigidos. |
| `A->B` con 9, luego 3, luego 6 | Un solo vecino, de 3 min | Un vínculo por par ordenado: queda el más rápido. |
| `agregarUsuario("Ana")` dos veces | Mismo índice, 1 usuario; `contiene("Z")` falso | Idempotencia del alta. |
| minutos `0` y `-7`, `A->A`, nombre en blanco, `indiceDe("Nadie")` | `IllegalArgumentException` | Validación de entradas. |

Estas dos clases suman 10 pruebas (6 de `CadenaMasRapidaTest` y 4 de `GrafoTest`). La corrida completa del proyecto (`mvn test`) ejecuta **44 pruebas, 0 fallas** (detalle por clase en la sección 9.2).

### 4.6 Complejidad temporal

Sea **V** la cantidad de usuarios y **E** la cantidad de vínculos.

- Inicializar los tres arreglos: **O(V)**.
- Cada usuario se fija **una sola vez** (`fijado[u]` lo garantiza), y al fijarlo se recorren sus vínculos salientes. Sumando sobre todos los usuarios, cada vínculo se examina a lo sumo una vez: **O(E)** relajaciones evaluadas.
- Cada relajación aceptada encola una entrada. Como hay a lo sumo una por vínculo, la cola llega a tener **O(V + E)** entradas; cada `poll` y cada `add` cuesta **O(log(V + E))**, que es **O(log V)** porque `E <= V²` y entonces `log(V + E) = O(log V)`.
- Reconstruir la cadena: **O(L)**, con L la cantidad de usuarios de la cadena (L <= V).

**Total: O((V + E) · log V).** Espacio **O(V + E)**: la lista de adyacencia más los tres arreglos y la cola.

Para comparar: probar todas las cadenas sería factorial y Bellman-Ford O(V·E). Con la red de ejemplo (V = 8, E = 14) la diferencia no se nota, pero es lo que permite que la funcionalidad aguante una red universitaria real.

### 4.7 Ejemplo de ejecución

Salida real de `java -jar target/red-social-universitaria-1.0.0.jar --demo` (opción 2, `Ana -> Gina`):

```
=== 2. Cadena de difusión más rápida (Greedy: Dijkstra) ===
Usuarios: [Ana, Bruno, Carla, Diego, Eva, Facu, Gina, Hugo]
Origen [Por defecto: Ana]: Destino [Por defecto: Gina]: Decisiones tomadas:
  Inicio: tiempo[Ana] = 0 y el resto de los usuarios = infinito
  Se fija a Ana con tiempo mínimo 0 min (es el menor entre los usuarios no fijados)
    Relajación Ana -> Bruno (10 min): 0 + 10 = 10 < infinito -> se actualiza tiempo[Bruno] = 10
    Relajación Ana -> Carla (5 min): 0 + 5 = 5 < infinito -> se actualiza tiempo[Carla] = 5
  Se fija a Carla con tiempo mínimo 5 min (es el menor entre los usuarios no fijados)
    Relajación Carla -> Bruno (3 min): 5 + 3 = 8 < 10 -> se actualiza tiempo[Bruno] = 8
    Relajación Carla -> Diego (15 min): 5 + 15 = 20 < infinito -> se actualiza tiempo[Diego] = 20
    Relajación Carla -> Eva (20 min): 5 + 20 = 25 < infinito -> se actualiza tiempo[Eva] = 25
  Se fija a Bruno con tiempo mínimo 8 min (es el menor entre los usuarios no fijados)
    Relajación Bruno -> Diego (7 min): 8 + 7 = 15 < 20 -> se actualiza tiempo[Diego] = 15
    Relajación Bruno -> Eva (25 min): 33 >= 25 -> se descarta
  Se fija a Diego con tiempo mínimo 15 min (es el menor entre los usuarios no fijados)
    Relajación Diego -> Eva (4 min): 15 + 4 = 19 < 25 -> se actualiza tiempo[Eva] = 19
    Relajación Diego -> Facu (12 min): 15 + 12 = 27 < infinito -> se actualiza tiempo[Facu] = 27
  Se fija a Eva con tiempo mínimo 19 min (es el menor entre los usuarios no fijados)
    Relajación Eva -> Facu (6 min): 19 + 6 = 25 < 27 -> se actualiza tiempo[Facu] = 25
    Relajación Eva -> Gina (9 min): 19 + 9 = 28 < infinito -> se actualiza tiempo[Gina] = 28
  Se fija a Facu con tiempo mínimo 25 min (es el menor entre los usuarios no fijados)
    Relajación Facu -> Gina (2 min): 25 + 2 = 27 < 28 -> se actualiza tiempo[Gina] = 27
  Se fija a Gina con tiempo mínimo 27 min (es el menor entre los usuarios no fijados)
  Se alcanzó el destino Gina: se corta la búsqueda
Cadena: Ana -> Carla -> Bruno -> Diego -> Eva -> Facu -> Gina | Tiempo total: 27 min
```

Y el caso sin cadena, eligiendo la opción 2 del menú con destino `Hugo` (nadie le transmite a Hugo):

```
El destino Hugo no es alcanzable desde Ana
No existe cadena de difusión de Ana a Hugo
```

## 5. Funcionalidad 3 — Selección de iniciativas para financiar (Programación Dinámica)

### 5.1 Análisis del problema

**Datos.** Una lista de `n` iniciativas candidatas, donde cada una tiene un identificador (`id`), un `costo` entero positivo (`> 0`) y un puntaje de `impacto` entero no negativo (`>= 0`), más un `presupuesto` total disponible `P >= 0`.

**Restricciones.**

- Cada iniciativa se puede financiar **una sola vez** y debe recibir la **totalidad** del dinero solicitado (decisión 0/1: no se puede fraccionar ni repetir).
- La suma de los costos de las iniciativas seleccionadas no puede superar el presupuesto `P`.

**Objetivo.** Seleccionar el subconjunto de iniciativas que maximice el **impacto total** sin exceder `P`, informando las iniciativas seleccionadas, el costo total, el impacto obtenido y el presupuesto restante (`P - costoTotal`).

### 5.2 Estrategia elegida: Programación Dinámica (Mochila 0/1)

Se modela como el problema clásico de la **mochila 0/1** resuelto con una tabla `dp` de `(n + 1) x (P + 1)` celdas, donde:

- `dp[i][w]` representa el **máximo impacto alcanzable** considerando únicamente las primeras `i` iniciativas (`0 <= i <= n`) con un presupuesto disponible `w` (`0 <= w <= P`).
- **Caso base:** `dp[0][w] = 0` para todo `w` (sin iniciativas consideradas, el impacto máximo es 0).
- **Recurrencia:** para cada iniciativa `i` (con `costo_i` e `impacto_i`) y cada presupuesto `w`:
  - Si `costo_i > w` (no entra en el presupuesto): `dp[i][w] = dp[i - 1][w]`.
  - Si `costo_i <= w` (puede financiarse o no):
    `dp[i][w] = max(dp[i - 1][w], dp[i - 1][w - costo_i] + impacto_i)`.
- **Reconstrucción de la solución:** una vez completada la tabla, el impacto óptimo está en `dp[n][P]`. Para saber **cuáles** iniciativas fueron elegidas, se recorre la tabla hacia atrás desde `i = n` hasta `1` comenzando en `w = P`: si `dp[i][w] != dp[i - 1][w]`, significa que la iniciativa `i` aportó a la mejora de esa celda, por lo que se marca como seleccionada y se descuenta su costo (`w -= costo_i`).

### 5.3 Justificación de la estrategia

El problema cumple las dos propiedades que justifican el uso de **Programación Dinámica**:

1. **Subestructura óptima:** la mejor selección entre las primeras `i` iniciativas con presupuesto `w` se construye a partir de la mejor selección entre las primeras `i - 1` iniciativas (ya sea con presupuesto `w` si no se elige la `i`, o con presupuesto `w - costo_i` si se elige).
2. **Subproblemas superpuestos:** distintas combinaciones de decisiones consultan reiteradamente el mismo subproblema `(i - 1, w')`. Tabular cada celda una sola vez evita recalcular subproblemas de forma exponencial.

**Por qué NO sirve Greedy.** Una heurística voraz como ordenar por mayor razón `impacto / costo` falla porque las iniciativas no se pueden fraccionar y el presupuesto sobrante puede quedar desaprovechado. Contraejemplo con `P = 50` y tres iniciativas `(costo, impacto)`:
- `#1 = (10, 60)` (razón 6), `#2 = (20, 100)` (razón 5), `#3 = (30, 120)` (razón 4).
- Greedy elige `#1` y `#2` (costo 30, impacto **160**) y ya no puede agregar `#3` porque costaría 60.
- El óptimo real es elegir `#2` y `#3` (costo 50, impacto **220**), que es el que encuentra la tabla de programación dinámica.

**Alternativas descartadas.**

| Alternativa | Por qué se descartó |
|---|---|
| Greedy (por mayor impacto, menor costo o mejor razón `impacto/costo`) | No garantiza el óptimo en la mochila 0/1 al no poder fraccionar iniciativas. |
| Fuerza bruta / Backtracking sin memorización | Explora los `2^n` subconjuntos posibles: costo exponencial **O(2^n)**, que recalcula los mismos estados `(i, w)` una y otra vez. |

### 5.4 Dónde se ve en el código

Clases `redsocial.modelo.Iniciativa` y `redsocial.financiamiento.SeleccionIniciativas`:

| Fase de la estrategia | Dónde está en el código |
|---|---|
| Validaciones y cota de memoria | `construirTabla`: rechaza nulos, presupuestos negativos y tablas que superen `MAXIMO_CELDAS`. |
| `// CASO BASE` | `construirTabla`: `int[][] dp = new int[n + 1][presupuesto + 1]` deja la fila `0` inicializada en `0`. |
| `// RECURRENCIA` | `construirTabla`: doble ciclo `for (i = 1..n)` y `for (w = 0..presupuesto)` que asigna `dp[i][w] = dp[i - 1][w]` y, si `costo <= w`, `Math.max(dp[i - 1][w], dp[i - 1][w - costo] + impacto)`. |
| `// RECONSTRUCCIÓN` | `seleccionar`: recorre `i` desde `n` hasta `1` preguntando `if (dp[i][w] != dp[i - 1][w])`, marca `elegida[i] = true` y actualiza `w -= iniciativa.costo()`. |
| Resultado inmutable | `Resultado(List<Iniciativa> seleccionadas, int costoTotal, int impactoTotal, int presupuestoRestante)` preserva el orden original de las iniciativas. |

### 5.5 Casos de prueba

`SeleccionIniciativasTest` verifica tanto el valor óptimo como la reconstrucción y las validaciones:

| Caso de prueba | Entrada | Resultado esperado | Qué verifica |
|---|---|---|---|
| `ejemploClasicoDondeGreedyFalla` | `[(1,10,60), (2,20,100), (3,30,120)]`, `P = 50` | Seleccionadas `[2, 3]`, costo 50, impacto 220, restante 0 | Supera el resultado subóptimo de Greedy (160). |
| `presupuestoCeroYSinIniciativas` | `P = 0` con 2 iniciativas; y lista vacía con `P = 100` | Selección vacía, costo 0, impacto 0 | Casos borde de fila 0 y columna 0 de la tabla. |
| `ningunaEntraEnElPresupuesto` | `[(1,50,200)]`, `P = 49` | Selección vacía, costo 0, impacto 0, restante 49 | Iniciativas más caras que el presupuesto se descartan. |
| `iniciativaConImpactoCero` | `[(1,5,0), (2,5,7)]`, `P = 5` | Seleccionada `[2]`, costo 5, impacto 7 | Iniciativas con impacto 0 no alteran el óptimo. |
| `validaciones` | `costo <= 0`, `impacto < 0`, `P < 0`, lista `null` | `IllegalArgumentException` | Cumplimiento de precondiciones del dominio. |
| `coincideConFuerzaBrutaEn300CasosAlAzar` | 300 casos aleatorios (`Random(42)`, `n` entre 1 y 10, `P` entre 0 y 59) | Mismo impacto que `fuerzaBruta` (`2^n`), suma de costos e impactos consistente | Exactitud del óptimo y de la reconstrucción en escenarios variados. |

### 5.6 Complejidad temporal

Sea **n** la cantidad de iniciativas y **P** el presupuesto disponible:

- Crear y llenar la tabla `dp` de `(n + 1) x (P + 1)` celdas, donde cada celda se calcula en **O(1)** (una comparación y una suma): **O(n · P)**.
- Reconstruir las iniciativas seleccionadas recorriendo desde `i = n` hasta `1`: **O(n)**.

**Total: tiempo O(n · P) y espacio O(n · P).** Se trata de una complejidad **pseudopolinomial**, porque depende de la magnitud numérica del presupuesto `P` y no únicamente de la cantidad de elementos `n`.

### 5.7 Ejemplo de ejecución

Salida real de `java -jar target/red-social-universitaria-1.0.0.jar --demo` (opción 3, con presupuesto por defecto `50`):

```
=== 3. Selección de iniciativas (Programación Dinámica: mochila 0/1) ===
Iniciativas disponibles:
  #1 (costo: 10, impacto: 60)
  #2 (costo: 15, impacto: 80)
  #3 (costo: 30, impacto: 120)
  #4 (costo: 20, impacto: 90)
  #5 (costo: 25, impacto: 70)
Presupuesto [Por defecto: 50]: Iniciativas seleccionadas: #1, #2, #4
Costo total: 45 | Impacto obtenido: 230 | Presupuesto restante: 5
```

Con presupuesto `50`, combinar `#1`, `#2` y `#4` suma un costo de `10 + 15 + 20 = 45` (sobran `5`) y logra un impacto total de `60 + 80 + 90 = 230`, superando cualquier combinación que incluya `#3` o `#5`.

## 6. A1 — Consultas de difusión entre todos los usuarios (Programación Dinámica)

### 6.1 Análisis del problema

**Datos.** La red de difusión dirigida (`Grafo`): `V` usuarios y vínculos `u -> v` con un tiempo en minutos positivo. Después del
procesamiento inicial llegan muchas consultas del tipo "¿cuánto tarda la mejor cadena de X a Y y cuál es?".

**Restricciones.**

- El procesamiento inicial se hace **una sola vez** y sus resultados se **conservan** para responder todas las consultas sin recalcular.
- Hay pares **sin cadena** (por ejemplo, nadie le transmite a Hugo): hay que distinguirlos de "tiempo 0" (el caso `X -> X`).
- Un usuario inexistente es un error de uso (`IllegalArgumentException`).
- La instancia refleja la red tal como estaba al construirla.

**Objetivo.** Responder rápido el tiempo mínimo, y la cadena que lo logra, entre **cualquier** par de usuarios.

### 6.2 Estrategia elegida: Programación Dinámica (Floyd-Warshall)

Se numeran los usuarios `0..V-1` y se arma una matriz `dist[V][V]`:

- **Subproblema:** `dist[i][j]` = mejor tiempo de `i` a `j` usando **solo los usuarios `0..k` como intermediarios**.
- **Caso base (sin intermediarios):** `dist[i][i] = 0`, `dist[u][v] = minutos` para cada vínculo directo e "infinito" en el resto.
- **Recurrencia:** al habilitar al usuario `k`, la mejor cadena de `i` a `j` o no pasa por `k` (se conserva el valor) o pasa por `k`:
  `dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])`.
- Se avanza `k = 0, 1, ..., V-1`; al terminar, todos los usuarios pudieron ser intermediarios y la matriz tiene el óptimo global.
- **Reconstrucción:** una segunda matriz `siguiente[i][j]` guarda el primer usuario después de `i` en la mejor cadena hacia `j`. Cuando
  pasar por `k` mejora, `siguiente[i][j] = siguiente[i][k]`. Para armar la cadena se parte del origen y se avanza con `siguiente` hasta el destino.

**Cómo evoluciona una celda (Ana -> Gina en la red de ejemplo).** El orden de los usuarios es Ana, Bruno, Carla, Diego, Eva, Facu, Gina, Hugo.

| Intermediarios habilitados | `dist[Ana][Gina]` | Qué pasó |
|---|---|---|
| ninguno | infinito | No hay vínculo directo Ana -> Gina |
| hasta Bruno | infinito | Ana llega a Diego (17) y a Eva (35) pasando por Bruno, pero ninguno de los dos está habilitado todavía para seguir hacia Gina |
| hasta Carla | infinito | Ana mejora a Bruno (10 -> 8), a Diego (17 -> 15) y a Eva (35 -> 25); todavía no hay camino a Gina |
| hasta Diego | infinito | Ana llega a Eva en 19 y a Facu en 27, pero falta habilitar a Eva o a Facu como intermediarios |
| hasta Eva | **28** | Aparece Ana-Carla-Bruno-Diego-Eva-Gina: `dist[Ana][Eva] + dist[Eva][Gina] = 19 + 9`; además `dist[Ana][Facu]` baja de 27 a 25 |
| hasta Facu | **27** | `dist[Ana][Facu] + dist[Facu][Gina] = 25 + 2 = 27 < 28`: se cambia a la cadena que termina en Facu |
| hasta Gina y Hugo | 27 | Sin cambios: ya es el mínimo |

### 6.3 Justificación de la estrategia

El problema tiene **subestructura óptima** (un tramo de una cadena óptima también es óptimo) y **subproblemas superpuestos** (el mismo
`dist[i][k]` se reutiliza para muchos destinos `j`). La tabla guarda cada subproblema una sola vez. Además, la consigna pide
**procesamiento inicial** y **muchas consultas**: encaja con pagar un costo único y responder después en `O(1)`.

**Alternativas descartadas.**

| Alternativa | Por qué se descartó |
|---|---|
| Repetir Dijkstra (funcionalidad 2) en cada consulta | Cada consulta cuesta `O((V + E) log V)`. Con pocas consultas es mejor que Floyd-Warshall, pero con muchas se repite trabajo que la matriz guarda de una vez. |
| Dijkstra desde cada origen y guardar los resultados | Es válido y comparable en redes ralas, pero no aplica la programación dinámica que pide el enunciado para esta funcionalidad. |
| Fuerza bruta (probar todas las cadenas posibles) | Cantidad exponencial de caminos. |

**Cuándo conviene cada uno:** pocas consultas, o la red cambia seguido ⇒ Dijkstra; muchas consultas sobre una red estable ⇒ Floyd-Warshall.

### 6.4 Dónde se ve en el código

Clase `redsocial.difusion.ConsultasDifusion`:

| Fase de la estrategia | Dónde está en el código |
|---|---|
| Procesamiento inicial | Constructor `ConsultasDifusion(Grafo)`: se hace una sola vez. |
| Caso base | Constructor, comentario `// CASO BASE`: `dist[i][i] = 0`, vínculos directos e `INF = Integer.MAX_VALUE / 2` (para que sumar dos infinitos no desborde). |
| Ampliación y `// RECURRENCIA` | Constructor, comentario `// AMPLIACIÓN`: triple bucle `k, i, j`; se saltean los casos con `dist[i][k]` o `dist[k][j]` en `INF`, y la línea comentada `// RECURRENCIA` actualiza `dist[i][j]` (y `siguiente[i][j]`). |
| Consulta de tiempo | `tiempoMinimo(origen, destino)`: lectura de la matriz, `O(1)`; devuelve `OptionalInt.empty()` si es `INF`. |
| Reconstrucción de la cadena | `consultar(origen, destino)`: avanza con `siguiente` desde el origen hasta el destino. |
| Presentación | `matrizComoTexto()` y la consola `ConsultasConsola` (opción 4). |

### 6.5 Casos de prueba

`ConsultasDifusionTest` (4 pruebas). La red de prueba es `A->B 4`, `A->C 1`, `C->B 2`, `B->D 1` y un usuario `Aislado`:

| Entrada | Resultado esperado |
|---|---|
| `consultar("A", "D")` | Cadena `[A, C, B, D]`, 4 minutos |
| `consultar("D", "A")` y `consultar("A", "Aislado")` | No alcanzable |
| `consultar("B", "B")` | Alcanzable, 0 minutos |
| `tiempoMinimo("A", "D")` / `tiempoMinimo("D", "A")` | `OptionalInt.of(4)` / `OptionalInt.empty()` |
| `A->B` con 9 y luego `A->B` con 3 | `tiempoMinimo("A", "B")` vale 3 |
| Usuario inexistente | `IllegalArgumentException` |
| 100 redes al azar (semilla 2026, 2 a 10 usuarios, hasta `3n` vínculos de 1 a 20 minutos), todos los pares (i, j) | Igual `alcanzable` y mismo `tiempoTotal` que `CadenaMasRapida.calcular` (Dijkstra); la cadena empieza en el origen y termina en el destino |

### 6.6 Complejidad temporal

- **Procesamiento inicial:** tres bucles anidados de `V` iteraciones con trabajo constante adentro: `V · V · V` ⇒ **O(V³)**. Inicializar
  las matrices cuesta `O(V² + E)`, que queda absorbido.
- **Espacio:** dos matrices de `V x V` ⇒ **O(V²)**.
- **Consulta de tiempo:** una lectura de `dist` ⇒ **O(1)**.
- **Consulta de cadena:** se avanza un usuario por paso hasta el destino ⇒ **O(L)**, con `L` la cantidad de usuarios de la cadena (`L <= V`).
- Frente a repetir Dijkstra, `Q` consultas cuestan `O(V³ + Q)` contra `O(Q · (V + E) log V)`: Floyd-Warshall se amortiza cuando `Q` es grande.

### 6.7 Ejemplo de ejecución

Opción 4 del `--demo` (los valores por defecto `Ana -> Gina` y `Ana -> Hugo`):

```
Procesamiento inicial completo. Tiempos mínimos en minutos ('-' = sin cadena):
       Ana    Bruno  Carla  Diego  Eva    Facu   Gina   Hugo   
Ana    0      8      5      15     19     25     27     -      
Bruno  -      0      27     7      11     17     19     -      
Carla  -      3      0      10     14     20     22     -      
Diego  -      23     20     0      4      10     12     -      
Eva    -      19     16     26     0      6      8      -      
Facu   -      13     10     20     24     0      2      -      
Gina   -      11     8      18     22     28     0      -      
Hugo   1      9      6      16     20     26     28     0      
Origen [Por defecto: Ana]: Destino [Por defecto: Gina]: Cadena: Ana -> Carla -> Bruno -> Diego -> Eva -> Facu -> Gina | Tiempo total: 27 min
Origen [Por defecto: Ana]: Destino [Por defecto: Hugo]: No existe cadena de difusión de Ana a Hugo
```

La columna de Hugo es `-` salvo su propia diagonal, porque nadie le transmite a él. La cadena Ana -> Gina de 27 minutos coincide con la que
encuentra Dijkstra en la funcionalidad 2.

## 7. B1 — Formación de equipos con restricciones (Backtracking)

### 7.1 Análisis del problema

**Datos.** Una lista de estudiantes, cada uno con un nombre y sus habilidades declaradas (`Estudiante`); el tamaño `k` de los equipos;
el conjunto de habilidades requeridas; los integrantes obligatorios y los pares de estudiantes incompatibles.

**Restricciones.**

- Cada equipo tiene exactamente `k` integrantes y, **entre todos**, cubren todas las habilidades requeridas.
- Los obligatorios están en todos los equipos.
- Dos estudiantes incompatibles no pueden estar en el mismo equipo (la incompatibilidad es simétrica: Ana con Beto equivale a Beto con Ana).
- El mismo equipo no puede aparecer dos veces con sus integrantes en otro orden: `[Ana, Beto]` y `[Beto, Ana]` son el mismo.
- Errores de uso (tamaño `<= 0`, nombres repetidos, un obligatorio o un incompatible que no existe) ⇒ `IllegalArgumentException`.
  Si no hay ningún equipo posible (más obligatorios que `k`, obligatorios incompatibles entre sí, una habilidad que nadie tiene) se
  devuelve una lista vacía.

**Objetivo.** Encontrar y mostrar **todos** los equipos válidos, no uno solo.

### 7.2 Estrategia elegida: Backtracking (incluir / excluir)

Se arma el equipo de a un estudiante, decidiendo en orden de posición si cada candidato **se incluye o se excluye**:

- **Elegir:** se incluye al candidato (se suma al equipo y se actualiza qué habilidades quedan cubiertas).
- **Explorar:** se sigue decidiendo con el próximo candidato.
- **Deshacer:** al volver de la recursión se lo quita del equipo, y se prueba la otra rama: excluirlo.
- Cuando el equipo llega a `k` integrantes se verifica que cubra todas las habilidades requeridas; si las cubre, se guarda.
- Los obligatorios se incluyen al principio y no participan de la búsqueda.

**Árbol de decisiones de un caso chico.** Estudiantes `Ana{Java,SQL}`, `Beto{Java,UI}`, `Cami{SQL}`, `Dani{UI}`; tamaño 2; requeridas
Java, SQL y UI. Es el recorrido real del algoritmo (primero la rama "incluir"):

```
(vacío)
├── +Ana                      cubre Java, SQL
│   ├── +Beto                 [Ana, Beto]  cubre todo          -> VÁLIDO
│   └── -Beto
│       ├── +Cami             [Ana, Cami]  falta UI            -> descartado
│       └── -Cami
│           ├── +Dani         [Ana, Dani]  cubre todo          -> VÁLIDO
│           └── -Dani         PODA: cantidad (falta 1 y no quedan candidatos)
└── -Ana
    ├── +Beto                 cubre Java, UI
    │   ├── +Cami             [Beto, Cami] cubre todo          -> VÁLIDO
    │   └── -Cami             PODA: habilidades (falta SQL y Dani no la tiene)
    └── -Beto                 PODA: habilidades (falta Java y ni Cami ni Dani la tienen)
```

Resultado: `[Ana, Beto]`, `[Ana, Dani]`, `[Beto, Cami]`. Si además Ana y Beto fueran incompatibles, la rama `+Ana -> +Beto` ni se
abriría (poda por incompatibilidad) y quedarían `[Ana, Dani]` y `[Beto, Cami]`.

**Cómo se evitan los repetidos.** Las decisiones se toman siempre hacia posiciones mayores: después de decidir sobre Ana solo se decide
sobre Beto, Cami y Dani, nunca se vuelve hacia atrás. Así cada subconjunto de estudiantes corresponde a un único camino del árbol y cada
equipo se genera una sola vez, con sus integrantes en el orden de la lista original.

### 7.3 Justificación de la estrategia

La consigna pide **todas** las soluciones, y las restricciones (incompatibles, obligatorios, habilidades) se pueden ir verificando a
medida que se arma el equipo. Backtracking recorre de forma sistemática el espacio de combinaciones y permite abandonar temprano las
ramas que ya no pueden terminar bien.

**Alternativas descartadas.**

| Alternativa | Por qué se descartó |
|---|---|
| Greedy (elegir en cada paso al que más habilidades aporta) | Construye **un** equipo y ni siquiera garantiza encontrarlo: una elección local puede dejar sin salida al resto. No enumera todas las soluciones. |
| Programación Dinámica | Sirve para optimizar un valor (el mejor equipo, la cantidad de equipos), no para listar cada equipo distinto; además las incompatibilidades entre pares rompen la independencia de los subproblemas. |
| Fuerza bruta (las `2^n` combinaciones, filtrando al final) | Correcta pero recorre todo sin descartar nada. Se usa en los tests como referencia para comprobar el backtracking. |

**Podas** (en el método `buscar`). Ninguna descarta un equipo válido, porque cada una corta solo ramas en las que es imposible completarlo:

| Poda | Cuándo corta | Por qué no pierde soluciones |
|---|---|---|
| `// PODA: incompatibilidad` | El candidato es incompatible con alguien ya elegido (obligatorios incluidos) | Todo equipo de esa rama tendría a los dos juntos: ninguno es válido. Solo se corta la rama "incluir"; la rama "excluir" se sigue explorando. |
| `// PODA: cantidad` | Los candidatos que quedan por decidir son menos que los integrantes que faltan | Aunque se incluyan todos, el equipo nunca llega a `k`. |
| `// PODA: habilidades` | Una habilidad requerida que todavía no cubre nadie del equipo no la tiene ningún candidato restante | Ningún equipo de esa rama podrá cubrirla. |

Para que la poda por habilidades sea barata, se precalcula de atrás hacia adelante `sufijo[p]` (las habilidades requeridas que aportan
los candidatos desde la posición `p` hasta el final, con `BitSet`) y la cobertura del equipo se mantiene con contadores por habilidad que
se suben al elegir y se bajan al deshacer, sin recalcular desde cero.

### 7.4 Dónde se ve en el código

Clase `redsocial.equipos.FormacionEquipos`, método público `encontrarEquipos` y clase interna `Busqueda`:

| Fase de la estrategia | Dónde está en el código |
|---|---|
| Validaciones y preparación | `encontrarEquipos`: nombres repetidos o desconocidos, matriz simétrica de incompatibles, casos sin solución (`k > n`, más obligatorios que `k`). |
| Obligatorios fijos | `Busqueda.incluirObligatorios()`: los agrega al inicio; si dos son incompatibles devuelve `false` y no hay equipos. |
| Precálculo para la poda | Constructor de `Busqueda`: `aporta[i]` (habilidades requeridas de cada estudiante) y `sufijo[p]`. |
| Caso base (equipo completo) | `buscar`: si `faltan == 0`, se guarda solo si `sinCubrir == 0`. |
| Podas | `buscar`: comentarios `// PODA: cantidad`, `// PODA: habilidades` y `// PODA: incompatibilidad`. |
| `// ELEGIR`, `// EXPLORAR`, `// DESHACER (backtrack)` | `buscar`: `agregar(candidato)`, llamada `buscar(p + 1)` y `quitarUltimo()`; después, la rama "excluir". |
| Armado del resultado | `registrarEquipo()`: intercala obligatorios y elegidos para dejar los nombres en el orden de la lista original. |
| Presentación | Consola `EquiposConsola` (opción 5). |

### 7.5 Casos de prueba

`FormacionEquiposTest` (7 pruebas). Grupo base: `Ana{Java,SQL}`, `Beto{Java,UI}`, `Cami{SQL}`, `Dani{UI}`; los equipos se comparan como conjunto.

| Entrada | Resultado esperado |
|---|---|
| Tamaño 2, requeridas `{Java, SQL, UI}` | `[Ana, Beto]`, `[Ana, Dani]`, `[Beto, Cami]` |
| Lo mismo con `Cami` obligatoria | Solo `[Beto, Cami]` |
| Lo mismo con `Ana` y `Beto` incompatibles | `[Ana, Dani]`, `[Beto, Cami]` |
| Tamaño 1 pidiendo `{Java, SQL, UI}` | Lista vacía (nadie tiene las tres) |
| Obligatorios `Ana` y `Beto`, incompatibles entre sí, tamaño 3 | Lista vacía |
| Más obligatorios (`Ana`, `Beto`) que el tamaño (1) | Lista vacía |
| Requerida `Python`, que nadie tiene | Lista vacía |
| Sin habilidades requeridas, tamaño 2 | Las 6 combinaciones de C(4, 2), sin repetir |
| Tamaño 0, obligatorio inexistente, incompatibilidad con un nombre inexistente | `IllegalArgumentException` |
| 300 casos al azar (semilla 11): de 1 a 8 estudiantes con habilidades de `{Java, SQL, UI, Testing, Redes}`, tamaño de 1 a `n`, requeridas, un obligatorio y de 0 a 3 incompatibilidades al azar | El conjunto de equipos coincide con el de una **fuerza bruta** que recorre las `2^n` máscaras con `k` bits, y no hay equipos repetidos |

La comparación contra fuerza bruta es la que garantiza que las podas no pierden soluciones ni generan duplicados.

### 7.6 Complejidad temporal

- **Sin podas**, el árbol incluir/excluir tiene un nivel por candidato y dos ramas por nivel: **O(2^n)** nodos.
- **Con la poda por cantidad**, solo se abren ramas que todavía pueden llegar a `k` integrantes. En el peor caso (sin incompatibilidades ni habilidades que poden) el árbol tiene `C(n, k)` hojas con un equipo completo y `C(n, k - 1)` hojas cortadas por esta poda. Como `C(n, k) + C(n, k - 1) = C(n + 1, k)` y cada nodo interno abre dos ramas, son exactamente **`2 · C(n + 1, k) - 1` nodos**.
  Se comprobó contando las llamadas a `buscar` en una copia instrumentada del código, para `n` de 1 a 16 y todo `k` posible (136 casos). Por ejemplo, con `n = 16` y `k = 14` hay 120 equipos y 1359 nodos: contar solo los equipos subestimaría el trabajo.
  Si `k <= n / 2`, `C(n + 1, k)` es menor que `2 · C(n, k)`, así que el orden coincide con el de la cantidad de equipos.
- **Las podas por incompatibilidad y por habilidades** no cambian el peor caso, pero en la práctica eliminan subárboles enteros: en el
  ejemplo de 7.2 se cortan tres ramas antes de llegar a un equipo completo.
- **Costo por nodo: `O(h + k)`.** La poda por habilidades revisa las `h` habilidades requeridas, la de incompatibilidad revisa a lo sumo `k` elegidos, agregar o quitar un integrante actualiza sus contadores en `O(h)` y copiar un equipo completo cuesta `O(k)`.
- **Total en el peor caso: O(C(n + 1, k) · (h + k))**, más la preparación.
- **Preparación:** `O(n² + S + n · h)` para la matriz de incompatibles, `aporta` y `sufijo`, con `S` la suma de las habilidades declaradas; **espacio** `O(n · h + n²)` (incluida la matriz de incompatibles) más `O(n)` de pila.

### 7.7 Ejemplo de ejecución

Opción 5 del `--demo` (datos fijos de la consola):

```
Estudiantes:
  Lucia: [Java, SQL]
  Mateo: [Java, UI]
  Sofia: [SQL, Testing]
  Tomas: [UI, Testing]
  Valen: [Java, Testing]
  Zoe: [SQL, UI]
Tamaño del equipo: 3 | Habilidades requeridas: [Java, SQL, UI, Testing] | Obligatorios: [Lucia] | No pueden coincidir: Mateo y Sofia

Equipos válidos: 7
  [Lucia, Mateo, Tomas]
  [Lucia, Mateo, Valen]
  [Lucia, Sofia, Tomas]
  [Lucia, Sofia, Zoe]
  [Lucia, Tomas, Valen]
  [Lucia, Tomas, Zoe]
  [Lucia, Valen, Zoe]
```

Todos incluyen a Lucia (obligatoria) y ninguno junta a Mateo con Sofia. `[Lucia, Mateo, Zoe]` no aparece porque no cubre Testing, y
`[Lucia, Sofia, Valen]` no aparece porque no cubre UI.

## 8. B3 — Cadenas alternativas de difusión (Backtracking)

### 8.1 Análisis del problema

**Datos.** Una red de difusión dirigida y ponderada (`Grafo`) donde los vértices representan usuarios y las aristas dirigidas representan vínculos de transmisión con tiempos enteros positivos en minutos. Se reciben además un usuario de `origen`, un usuario de `destino`, un `tiempoMaximo` permitido en minutos, un límite de `maxIntermediarios` (cantidad máxima de usuarios entre el origen y el destino) y un conjunto de usuarios `excluidos`.

**Restricciones.**

- Los vínculos son **dirigidos**: que un usuario transmita a otro no implica la existencia del vínculo inverso.
- **Sin ciclos ni repeticiones:** una cadena de difusión no puede repetir ningún usuario (cadenas simples).
- **Usuarios excluidos:** ninguna cadena puede pasar por ninguno de los usuarios del conjunto de excluidos. Si el origen o el destino están excluidos, el resultado no contendrá cadenas.
- **Límite de tiempo:** el tiempo total acumulado de la cadena no puede superar `tiempoMaximo`.
- **Límite de intermediarios:** la cantidad de intermediarios (usuarios de la cadena sin contar origen ni destino) no puede superar `maxIntermediarios`.
- **Validaciones:** parámetros inválidos (`tiempoMaximo < 0`, `maxIntermediarios < 0`, origen igual a destino o usuarios inexistentes en la red) disparan `IllegalArgumentException`.

**Objetivo.** Encontrar y enumerar **todas** las cadenas alternativas de difusión que cumplan simultáneamente todas las restricciones anteriores, mostrando sus tiempos acumulados y destacando cuál de ellas es la más rápida.

### 8.2 Estrategia elegida: Backtracking (recorrido en profundidad con podas)

La estrategia elegida es **Backtracking**: se explora sistemáticamente el espacio de todos los caminos simples entre el origen y el destino construyendo el camino de a un vínculo por vez.

Funciona así, en palabras simples:

1. Se parte desde el usuario de origen con tiempo acumulado 0, con el origen marcado como visitado en un arreglo booleano `visitado[]` y agregado a la lista del `camino`.
2. Para el usuario actual se examinan en orden sus vínculos salientes hacia cada `vecino`:
   - Si el vecino ya está visitado o excluido, se descarta.
   - Si el nuevo tiempo acumulado superaría `tiempoMaximo`, la rama se poda.
   - Si el vecino no es el destino y agregarlo superaría `maxIntermediarios`, la rama se poda.
3. Si supera las podas, se aplica la fase de **ELEGIR**: se marca `visitado[vecino] = true` y se agrega el vecino al camino.
4. Se pasa a la fase de **EXPLORAR**: llamada recursiva desde el vecino sumando los minutos del vínculo.
5. Al retornar de la recursión se ejecuta la fase de **DESHACER (backtrack)**: se desmarca `visitado[vecino] = false` y se remueve el vecino del camino (`camino.removeLast()`), permitiendo que ese usuario pueda formar parte de otras cadenas alternativas válidas que provengan de ramificaciones distintas.
6. Al alcanzar el vértice destino se registra la cadena válida encontrada y se compara su tiempo contra la mejor hasta el momento para mantener actualizada la más rápida.

**Árbol de exploración de un caso chico con podas.** Red de prueba: `A -> B (2)`, `A -> C (5)`, `B -> C (1)`, `B -> D (7)`, `C -> D (2)`, `C -> B (1)`. Búsqueda desde `A` hasta `D` con `tiempoMaximo = 7` y `maxIntermediarios = 2` (recorriendo los vecinos en su orden de inserción):

```
A (0 min)
├── +B (2 min)
│   ├── +C (3 min)
│   │   ├── +D (5 min)  [A, B, C, D] (2 intermediarios, 5 min <= 7) -> VÁLIDA (más rápida)
│   │   └── +B          descartado: ya visitado (ciclo evitado)
│   └── +D (9 min)      PODA: tiempo (9 > 7)
└── +C (5 min)
    ├── +D (7 min)      [A, C, D] (1 intermediario, 7 min <= 7)     -> VÁLIDA
    └── +B (6 min)
        ├── +C          descartado: ya visitado (ciclo evitado)
        └── +D (13 min) PODA: tiempo (13 > 7)
```

Resultado: 2 cadenas válidas (`[A, B, C, D]` de 5 min y `[A, C, D]` de 7 min), con `[A, B, C, D]` como la más rápida. Las ramas hacia `[A, B, D]` y `[A, C, B, D]` se podaron en cuanto su tiempo superó el límite de 7 minutos. Si además se pidiera a lo sumo 1 intermediario (`maxIntermediarios = 1`), las ramas `+B -> +C` y `+C -> +B` se cortarían con `PODA: intermediarios` y quedaría únicamente `[A, C, D]` (7 min).

### 8.3 Justificación de la estrategia

**Por qué Backtracking y no Dijkstra.** El algoritmo de Dijkstra (Greedy) está diseñado para encontrar de manera eficiente *una sola* cadena óptima global. En cambio, el requerimiento de esta funcionalidad exige explícitamente **enumerar todas las cadenas alternativas** que satisfagan restricciones contextuales fijadas por el usuario (tiempo tope, intermediarios y exclusiones). Backtracking es la estrategia adecuada porque recorre exhaustivamente el espacio de soluciones posibles y permite descartar subárboles completos mediante podas tempranas.

**Diferencia clave con la Funcionalidad 2 (Dijkstra):** En la red de ejemplo institucional, Dijkstra encuentra la cadena óptima `Ana -> Carla -> Bruno -> Diego -> Eva -> Facu -> Gina` con un tiempo de **27 minutos**. Sin embargo, esa cadena tiene **5 intermediarios** (`Carla, Bruno, Diego, Eva, Facu`). Cuando el usuario consulta la opción B3 fijando un límite de hasta **4 intermediarios**, dicha cadena queda excluida y la más rápida pasa a ser `Ana -> Carla -> Bruno -> Diego -> Eva -> Gina` con **28 minutos** (4 intermediarios). Esto evidencia la utilidad práctica de contar con cadenas alternativas bajo restricciones específicas.

**Alternativas descartadas:**

| Alternativa | Por qué se descartó |
|---|---|
| Dijkstra (Greedy) | Solo devuelve una cadena óptima y no permite enumerar caminos alternativos ni incorporar restricciones globales como intermediarios máximos. |
| Programación Dinámica | No es aplicable porque se requiere enumerar la totalidad de los caminos simples (los subproblemas no se superponen para formar un único valor óptimo, y evitar ciclos requiere conocer el camino completo ya recorrido). |
| Fuerza bruta sin podas | Explora absolutamente todas las ramificaciones hasta el final, consumiendo tiempo innecesario en caminos que ya violaron el tiempo máximo o la cantidad de intermediarios. |

**Podas implementadas:**

| Poda / restricción | Condición de corte | Justificación de que no pierde soluciones válidas |
|---|---|---|
| `// PODA: tiempo` | `tiempoAcumulado + minutos > tiempoMaximo` | Como todos los vínculos del grafo tienen costos estrictamente positivos (`minutos > 0`), cualquier extensión posterior de este camino acumulará un tiempo estrictamente mayor, imposibilitando alcanzar el destino con un tiempo `<= tiempoMaximo`. |
| `// PODA: intermediarios` | `vecino != destino && camino.size() - 1 >= maxIntermediarios` | Si el vecino no es el destino final, incorporarlo al camino incrementa la cantidad de intermediarios; si ya se alcanzó la cota máxima permitida, cualquier camino que continúe por esa rama excederá irremediablemente el límite de intermediarios. |
| Sin repetidos ni excluidos (`visitado[vecino]`) | `visitado[vecino] == true` | Garantiza que no se generen ciclos infinitos y que ningún usuario de la lista de excluidos sea visitado. |

### 8.4 Dónde se ve en el código

Clase `redsocial.difusion.CadenasAlternativas` y consola `redsocial.app.CadenasAlternativasConsola`:

| Fase de la estrategia | Dónde está en el código |
|---|---|
| Inicialización y restricciones | `buscar`: carga de excluidos en `visitado[]`, rechazo si origen o destino están excluidos y validaciones de parámetros. |
| Caso base (destino alcanzado) | `explorar`: `if (actual == destino)`, invoca a `registrar(...)`. |
| Podas de búsqueda | `explorar`: comentarios `// PODA: tiempo` y `// PODA: intermediarios` antes de recursar. |
| `// ELEGIR` | `explorar`: `visitado[vecino] = true; camino.add(vecino);` |
| `// EXPLORAR` | `explorar`: llamada recursiva con el tiempo acumulado actualizado. |
| `// DESHACER (backtrack)` | `explorar`: `camino.removeLast(); visitado[vecino] = false;` |
| Registro y mejor cadena | `registrar`: encapsula la cadena en `CadenaAlternativa` inmutable y actualiza `mejor[0]` si mejora el tiempo estricto. |
| Interfaz de usuario | `CadenasAlternativasConsola.ejecutar`: lectura con `Entrada` de origen, destino, tiempo, intermediarios y excluidos; presentación en consola. |

### 8.5 Casos de prueba

`CadenasAlternativasTest` (6 pruebas automatizadas):

| Caso de prueba | Entrada | Resultado esperado | Qué verifica |
|---|---|---|---|
| `enumeraTodasLasCadenasSimplesYMasRapida` | Red con ciclo `B <-> C`, `A -> D`, máx 100 min, máx 10 intermediarios | 4 cadenas simples: `[A,B,D]` (9), `[A,B,C,D]` (5), `[A,C,D]` (7), `[A,C,B,D]` (13); más rápida: `[A,B,C,D]` con 5 min | Enumera exhaustivamente sin ciclos y halla la óptima. |
| `filtraPorTiempoMaximo` | Misma red, tiempo máximo 7 | 2 cadenas: `[A,B,C,D]` (5) y `[A,C,D]` (7) | La poda y filtro por tiempo descartan caminos lentos. |
| `filtraPorMaximoDeIntermediarios` | Misma red, máx 1 intermediario; y con máx 0 intermediarios | Con 1: `[A,B,D]` y `[A,C,D]` (más rápida: `[A,C,D]`, 7 min); con 0: lista vacía | Respeta la cota de intermediarios sin contar origen ni destino. |
| `respetaUsuariosExcluidos` | Excluyendo `B`, excluyendo `B` y `C`, y excluyendo el destino `D` | Con `B` excluido: solo `[A,C,D]`; con ambos: vacía; con destino excluido: vacía | Ninguna cadena pasa por usuarios excluidos. |
| `validacionesLanzanExcepcion` | Origen igual a destino, tiempos negativos, intermediarios negativos, usuarios inexistentes | `IllegalArgumentException` | Manejo robusto de errores de precondición. |
| `coincideConEnumeracionExhaustivaEn100RedesAlAzar` | 100 redes dirigidas generadas al azar (semilla 99, 3 a 7 nodos, vínculos con 1-9 min, límites y exclusiones aleatorias) | Coincidencia idéntica en cantidad y contenido frente a una búsqueda exhaustiva sin podas | Garantía empírica formal de que las podas no pierden ninguna solución válida ni generan duplicados. |

### 8.6 Complejidad temporal

Sea **V** la cantidad de usuarios (vértices) y **E** la cantidad de vínculos (aristas):

- **Sin podas en el peor caso:** En un grafo completo donde todo par de vértices está conectado, la cantidad de caminos simples distintos entre dos nodos es del orden de `(V - 2)!` (sumando sobre todas las longitudes posibles de `0` a `V - 2` intermediarios). Por lo tanto, la complejidad temporal en el peor caso teórico es **O(V!)**.
- **Impacto de las podas:** En redes reales, las podas por tiempo máximo y cantidad máxima de intermediarios truncan drásticamente el árbol de exploración:
  - Limitar la cantidad de intermediarios a `m` restringe la profundidad máxima del árbol de recursión a `m + 1`, reduciendo las combinaciones a `O(V^m)` en lugar de factorial.
  - La poda por tiempo corta inmediatamente ramas tan pronto la suma de pesos sobrepasa el umbral prefijado.
- **Espacio auxiliar:** Requiere **O(V)** memoria de pila de llamadas (la profundidad máxima es a lo sumo `V`) más `O(V)` para la lista del camino y el arreglo `visitado[]`, más el espacio necesario para almacenar las cadenas válidas que se devuelven al usuario.

### 8.7 Ejemplo de ejecución

Salida real de `java -jar target/red-social-universitaria-1.0.0.jar --demo` (opción 6, con los valores por defecto: `Ana -> Gina`, tiempo máximo `40`, máx intermediarios `4`, sin excluidos):

```
=== 6. B3 · Cadenas alternativas de difusión (Backtracking) ===
Usuarios: [Ana, Bruno, Carla, Diego, Eva, Facu, Gina, Hugo]
Origen [Por defecto: Ana]: Destino [Por defecto: Gina]: Tiempo máximo (min) [Por defecto: 40]: Máximo de intermediarios [Por defecto: 4]: Usuarios excluidos (separados por coma): Cadenas válidas: 10
  Ana -> Bruno -> Diego -> Eva -> Facu -> Gina | 29 min
  Ana -> Bruno -> Diego -> Eva -> Gina | 30 min
  Ana -> Bruno -> Diego -> Facu -> Gina | 31 min
  Ana -> Carla -> Bruno -> Diego -> Eva -> Gina | 28 min
  Ana -> Carla -> Bruno -> Diego -> Facu -> Gina | 29 min
  Ana -> Carla -> Diego -> Eva -> Facu -> Gina | 32 min
  Ana -> Carla -> Diego -> Eva -> Gina | 33 min
  Ana -> Carla -> Diego -> Facu -> Gina | 34 min
  Ana -> Carla -> Eva -> Facu -> Gina | 33 min
  Ana -> Carla -> Eva -> Gina | 34 min
Más rápida: Ana -> Carla -> Bruno -> Diego -> Eva -> Gina | 28 min
```

## 9. Verificación y pruebas

### 9.1 Cómo se verifica el proyecto

El proyecto cuenta con una batería de pruebas automatizadas con **JUnit Jupiter 5** y un modo de demostración integral (`--demo`) que ejecuta las 6 funcionalidades de punta a punta con sus valores por defecto. Los comandos de verificación son:

```bash
mvn test                                                      # ejecuta las 44 pruebas automatizadas (0 fallas)
mvn package                                                   # compila, corre los tests y empaqueta el JAR ejecutable
java -jar target/red-social-universitaria-1.0.0.jar --demo    # ejecuta las 6 funcionalidades con datos por defecto
```

Al ejecutar `mvn test` con las 6 funcionalidades integradas se obtienen **44 tests ejecutados, 0 fallas y 0 errores** distribuidos en 10 clases de prueba.

### 9.2 Cobertura por clase de prueba

| Clase de test | Cantidad de tests | Qué cubre |
|---|---|---|
| `EntradaTest` | 3 | Lectura por consola con valores por defecto ante líneas en blanco, recorte de espacios y validación de números enteros (`IllegalArgumentException`). |
| `PublicacionTest` | 2 | Cálculo de relevancia (`reacciones + 2 x comentarios`) y validaciones de creación del modelo `Publicacion`. |
| `MuroOrdenadorTest` | 5 | Ordenamiento Merge Sort propio por recencia y por relevancia, reglas de desempate, inmutabilidad de la lista de entrada y prueba de volumen con 5000 publicaciones al azar (`Random(42)`). |
| `GrafoTest` | 4 | Alta idempotente de usuarios, vínculos dirigidos, conservación del menor tiempo por par ordenado y validaciones de argumentos. |
| `CadenaMasRapidaTest` | 6 | Algoritmo de Dijkstra (Greedy): preferencia del menor tiempo sobre menos saltos, destinos inalcanzables, sentido de los vínculos, caso origen igual a destino, registro de decisiones en la traza y validaciones. |
| `SeleccionIniciativasTest` | 6 | Mochila 0/1 con Programación Dinámica: contraejemplo clásico donde Greedy falla, casos borde (presupuesto 0, lista vacía, costos mayores al presupuesto), validaciones y comparación contra fuerza bruta en 300 casos al azar (`Random(42)`). |
| `ConsultasDifusionTest` | 4 | Algoritmo de Floyd-Warshall (A1, Programación Dinámica): tiempos mínimos y reconstrucción de cadenas entre pares, pares inalcanzables y comparación exhaustiva contra Dijkstra en 100 redes al azar (`Random(2026)`). |
| `FormacionEquiposTest` | 7 | Backtracking para equipos (B1): cobertura de habilidades, obligatorios, incompatibilidades simétricas, casos sin solución, validaciones y comparación contra fuerza bruta (`2^n` máscaras) en 300 casos al azar (`Random(11)`). |
| `CadenasAlternativasTest` | 6 | Backtracking para cadenas alternativas (B3): enumeración simple en grafos con ciclos, podas por tiempo máximo e intermediarios, usuarios excluidos, validaciones y comparación contra enumeración exhaustiva sin podas en 100 redes al azar (`Random(99)`). |
| `MainDemoTest` | 1 | Test de integración de punta a punta: invoca `Main.demo()` y verifica que la salida contenga literalmente todas las líneas clave de contrato de las 6 funcionalidades. |
| **Total** | **44** | **Cobertura unitaria, de propiedades contra fuerza bruta y de integración completa.** |

### 9.3 Comprobación de que no se usan ordenamientos del JDK y de presencia de estrategias

La consigna prohíbe el uso de rutinas de ordenamiento provistas por el lenguaje para ordenar el muro, y el equipo extendió esa regla a todo `src/main`. Se verificó con `git grep`:

```bash
git grep -n -E "Arrays\.(parallelSort|sort)|Collections\.sort|\.sort\(|\.sorted\(|TreeMap|TreeSet" -- src/main
```

**Resultado:** salida vacía (`0` coincidencias en `src/main`).

También se auditó cualquier aparición de `Comparator`:

```bash
git grep -n "Comparator" -- src/main
```

**Resultado:** en código ejecutable aparece únicamente en `CadenaMasRapida.java` para configurar la prioridad del *heap* binario (`PriorityQueue`) de Dijkstra (extraer el candidato no fijado de menor tiempo acumulado), nunca para ordenar una lista o colección; la única otra mención está en el Javadoc de `CriterioMuro.java` aclarando justamente que el muro define su propia interfaz y no usa `java.util.Comparator`.

Asimismo, cada una de las 4 estrategias exigidas está explícitamente identificada en el código mediante su estructura y comentarios de fase:
- **Divide y Conquista:** `// DIVIDIR`, `// CONQUISTAR`, `// COMBINAR` en `MuroOrdenador.java`.
- **Greedy:** `// DECISIÓN GREEDY` y `// RELAJACIÓN` en `CadenaMasRapida.java`.
- **Programación Dinámica:** `// CASO BASE`, `// RECURRENCIA`, `// RECONSTRUCCIÓN` en `SeleccionIniciativas.java` y `// CASO BASE`, `// AMPLIACIÓN`, `// RECURRENCIA` en `ConsultasDifusion.java`.
- **Backtracking:** `// PODA: ...`, `// ELEGIR`, `// EXPLORAR`, `// DESHACER (backtrack)` en `FormacionEquipos.java` y `CadenasAlternativas.java`.

### 9.4 Comparación de resultados del modo `--demo` contra los contratos

| Opción / Funcionalidad | Valor esperado (`docs/CONTRATOS.md` §4 y §5) | Resultado obtenido en `--demo` | Estado |
|---|---|---|---|
| **1 · Muro de publicaciones** | Recencia: `6, 5, 4, 2, 3, 7, 1`<br>Relevancia: `5, 2, 3, 7, 1, 4, 6` | `Orden por recencia (ids): 6, 5, 4, 2, 3, 7, 1`<br>`Orden por relevancia (ids): 5, 2, 3, 7, 1, 4, 6` | Coincide |
| **2 · Cadena más rápida (Dijkstra)** | `Ana -> Carla -> Bruno -> Diego -> Eva -> Facu -> Gina`, 27 min, con traza de decisiones | `Decisiones tomadas:` (21 líneas)<br>`Cadena: Ana -> Carla -> Bruno -> Diego -> Eva -> Facu -> Gina \| Tiempo total: 27 min` | Coincide |
| **3 · Selección de iniciativas (Mochila 0/1)** | Presupuesto 50: `#1, #2, #4`, costo 45, impacto 230, restante 5 | `Iniciativas seleccionadas: #1, #2, #4`<br>`Costo total: 45 \| Impacto obtenido: 230 \| Presupuesto restante: 5` | Coincide |
| **4 · A1 Consultas entre todos los pares (Floyd-Warshall)** | Matriz 8x8; `Ana -> Gina` en 27 min; `Ana -> Hugo` sin cadena | Matriz idéntica;<br>`Cadena: Ana -> Carla -> Bruno -> Diego -> Eva -> Facu -> Gina \| Tiempo total: 27 min`<br>`No existe cadena de difusión de Ana a Hugo` | Coincide |
| **5 · B1 Formación de equipos (Backtracking)** | 7 equipos válidos con `Lucia` fija y sin juntar a `Mateo` con `Sofia` | `Equipos válidos: 7` (los 7 equipos esperados en el mismo orden) | Coincide |
| **6 · B3 Cadenas alternativas (Backtracking)** | `Ana -> Gina` (máx 40 min, 4 interm.): 10 cadenas; más rápida de 28 min (`Ana -> Carla -> Bruno -> Diego -> Eva -> Gina`) | `Cadenas válidas: 10`<br>`Más rápida: Ana -> Carla -> Bruno -> Diego -> Eva -> Gina \| 28 min` | Coincide |

### 9.5 Limitaciones conocidas

1. **Datos de ejemplo en memoria:** Las publicaciones, iniciativas, estudiantes y la red de difusión están precargados en código dentro de las clases de consola y `DatosRed` para facilitar la prueba inmediata y la reproducibilidad del modo `--demo`, sin persistencia en base de datos ni carga desde archivos externos.
2. **Complejidad pseudopolinomial en la selección de iniciativas (Funcionalidad 3):** La tabla de programación dinámica tiene dimensión `(n + 1) x (P + 1)` y está acotada por `MAXIMO_CELDAS = 100_000_000`. Para presupuestos de millones de unidades convendría escalar los montos por su máximo común divisor.
3. **Costo exponencial/factorial de peor caso en B1 y B3 (Backtracking):** Como ambas funcionalidades exigen enumerar *todas* las soluciones válidas, en instancias densas con restricciones laxas la cantidad de soluciones crece como `C(n, k)` en B1 (con un costo `O(k)` para procesar cada una, dando un tiempo total `O(C(n, k) · k)`) y como `O(V!)` en B3. Las podas implementadas reducen drásticamente las ramas exploradas cuando existen restricciones activas, pero no alteran la cota asintótica del peor caso.
4. **Inmutabilidad de la instantánea en A1 (Floyd-Warshall):** `ConsultasDifusion` precalcula las matrices `dist` y `siguiente` sobre el estado del `Grafo` al momento de su construcción (`O(V³)`). Si la red cambiara con alta frecuencia, sería necesario reconstruir la instancia o preferir consultas puntuales con Dijkstra.
