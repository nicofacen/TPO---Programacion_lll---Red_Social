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

_Pendiente — Entrega 1 — responsable: Nicolas_

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

Resultado de la corrida completa: **13 pruebas, 0 fallas** (incluye las 3 de `EntradaTest`).

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
Origen [Ana]: Destino [Gina]: Decisiones tomadas:
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

_Pendiente — Entrega 1 — responsable: Fadi_

## 6. A1 — Consultas de difusión entre todos los usuarios (Programación Dinámica)

_Pendiente — Entrega 2 — responsable: Lucas_

## 7. B1 — Formación de equipos con restricciones (Backtracking)

_Pendiente — Entrega 2 — responsable: Nicolas_

## 8. B3 — Cadenas alternativas de difusión (Backtracking)

_Pendiente — Entrega 2 — responsable: Fadi_

## 9. Verificación y pruebas

_Pendiente — Entrega 2 — responsable: Fadi_
