# División de tareas — dos entregas

Este documento sirve para dos cosas: que cada integrante **entienda y pueda explicar** su parte, y que cada uno pueda indicarle a
**Claude Code** exactamente qué hacer. Cada integrante tiene un prompt en `docs/prompts/` y el contexto común está en `CLAUDE.md`
(Claude Code lo lee solo al abrir el proyecto). **No hay código de referencia:** el código se genera paso a paso a partir de las
especificaciones de esos prompts, un commit por paso.

---

## 1. Qué se entrega y cuándo

| Entrega | Fecha (a confirmar con la cátedra) | Contenido | Se marca con |
|---|---|---|---|
| **1** | esta semana (sugerido: vie 23/10) | Proyecto base + **etapa 1** (muro, cadena más rápida, iniciativas) + menú con las opciones 1 a 3 + README + informe con las secciones 1 a 5 | tag `entrega-1` |
| **2** | en dos semanas (sugerido: mar 3/11) | **A1, B1 y B3** + opciones 4 a 6 + test de integración + README e informe completos | tag `entrega-2` |

> **Supuesto:** la Entrega 1 corresponde a la primera etapa de la consigna y la Entrega 2 a la segunda. Si la cátedra definió otro alcance,
> se mueven los pasos de una entrega a otra (los pasos son independientes; solo hay que respetar las puertas de la sección 4).

En cada entrega `main` compila, pasa los tests y el demo muestra las opciones que corresponden.

---

## 2. Vista rápida

| | **Nicolas** | **Lucas** | **Fadi** |
|---|---|---|---|
| **Entrega 1** | Base del proyecto y del menú · Muro (**Divide y Conquista**, Merge Sort) | `Grafo` · Cadena más rápida (**Greedy**, Dijkstra) | Iniciativas (**Prog. Dinámica**, mochila 0/1) |
| **Entrega 2** | B1 · Equipos con restricciones (**Backtracking**) | A1 · Consultas entre todos (**Prog. Dinámica**, Floyd-Warshall) · test de integración | B3 · Cadenas alternativas (**Backtracking**) |
| **Informe** | Secciones 3 y 7 · revisión de 1 y 2 | Secciones 4 y 6 | Secciones 5, 8 y 9 |
| **README** | Versión inicial | Versión final | |
| **Commits** | 18 | 16 | 16 |
| **Su PR lo revisa** | Fadi | Nicolas | Lucas |
| **Su prompt** | `docs/prompts/PROMPT_NICOLAS.md` | `docs/prompts/PROMPT_LUCAS.md` | `docs/prompts/PROMPT_FADI.md` |

Entre los tres se cubren las 4 estrategias que exige la consigna: **Divide y Conquista** (Nicolas), **Greedy** (Lucas),
**Programación Dinámica** (Lucas y Fadi) y **Backtracking** (Nicolas y Fadi).

---

## 3. Cronograma sugerido

| Día | Nicolas | Lucas | Fadi |
|---|---|---|---|
| Mar 20/10 | N1, N2, N3, N4, N5 | L1, L2, L3 | F1, F2 |
| Mié 21/10 | N6, N7, N8 | L4, L5, L6, L7 | F3, F4, F5, F6 |
| Jue 22/10 | N9, N10 | L8, L9 | F7 |
| Vie 23/10 | **Cierre de la Entrega 1:** revisar e integrar PR pendientes; verificación final y tag `entrega-1` | Verificar en su máquina y corregir lo suyo | Verificar en su máquina y corregir lo suyo |
| Lun 26/10 | N11, N12 | L10 | F8 |
| Mar 27/10 | N13 | L11 | F9 |
| Mié 28/10 | N14, N15 | L12 | F10 |
| Jue 29/10 | N16 | L13 | F11 |
| Vie 30/10 |  |  | F12, F13, F14 |
| Lun 2/11 | N17 | L14, L15 | F15 |
| Mar 3/11 | N18 · **Cierre de la Entrega 2:** verificación final y tag `entrega-2` | L16 · verificar y corregir lo suyo | F16 · verificar y corregir lo suyo |

Los días son una guía: lo importante es que los commits **salgan a medida que se avanza**, no todos juntos al final. Si la fecha real de
una entrega cambia, se corren los días manteniendo el orden y las puertas.

---

## 4. Puertas (dependencias entre personas)

| Puerta | Qué tiene que estar en `main` | Qué habilita |
|---|---|---|
| **P1** | N1 y N2 de Nicolas (proyecto Maven + `CLAUDE.md`, contratos y prompts) | Todo lo de Lucas y Fadi (necesitan el `pom.xml` y los contratos) |
| **P2** | N3 de Nicolas (menú con registro de opciones) | Los pasos de consola: N8, L8, F6, y después N16, L13, F14 |
| **P3** | PR `feature/grafo` de Lucas (L1–L3) | Dijkstra (L4 en adelante) y, en la Entrega 2, B3 de Fadi (F8 en adelante) |
| **P4** | `DatosRed` de Lucas (L8) | La consola de B3 de Fadi (F14) |
| **P5** | Las seis consolas registradas (N16, L13 y F14) | El test de integración de Lucas (L15) |

**Prioridad del primer día:** Nicolas sube N1, N2 y N3 en la primera hora, y el PR del `Grafo` se revisa en pocas horas. Mientras se espera
una puerta se avanza con pasos que no dependen de ella (por ejemplo Fadi puede hacer toda la mochila). **Revisión de PR: dentro de 4 horas en
la Entrega 1 y de 24 horas en la Entrega 2.**

---

## 5. Reglas de Git

1. **Cuenta propia:** antes del primer commit, `git config user.name "…"` y `git config user.email "…"` con la cuenta de cada uno en el repositorio.
2. **Una rama por tema**, desde `main` actualizada. Nada va directo a `main`, salvo N1, N2 y N3.
3. **Commits chicos y coherentes**, con mensaje `tipo(ámbito): qué se hizo` (`feat`, `fix`, `test`, `docs`, `perf`, `chore`, `refactor`).
4. **Un PR por rama** con revisión cruzada (tabla de la sección 2). El cuerpo del PR dice qué agrega, cómo probarlo y, si corresponde, la
   estrategia y su complejidad. El revisor deja al menos un comentario real y **es quien integra**.
5. **Integrar con "Merge commit" o "Rebase and merge". NUNCA "Squash and merge"**: aplasta los commits y se pierde la evidencia de
   trabajo individual que pide la consigna.
6. **Historial real:** nada de fechas falsas (`--date`) ni subir todo junto al final.
7. **Antes de cada push:** `mvn -q test` en verde.
8. **Tags:** al cerrar cada entrega, Nicolas ejecuta (sobre `main` actualizada y verificada)
   `git tag -a entrega-1 -m "Entrega 1: etapa 1 (muro, cadena más rápida, iniciativas)"` y `git push origin entrega-1`
   (y lo mismo con `entrega-2`). Si aparece un error antes de entregar, se corrige con un commit `fix:` y se vuelve a verificar antes de etiquetar.
9. Las líneas de registro de `Main.java` están separadas para evitar conflictos; si igual hay uno, se queda con las dos líneas.

---

## 6. Cómo trabajar con Claude Code, paso a paso

**Preparación (una sola vez)**
1. **Nicolas** crea el repositorio vacío en GitHub/GitLab e invita a Lucas y a Fadi como colaboradores (opcional: proteger `main` para exigir PR,
   pero habilitando los push directos de Nicolas para N1 a N3 o desactivando la protección hasta que estén).
2. **Nicolas** clona el repositorio y **descomprime el zip del kit en la raíz del clon** (quedan `pom.xml`, `CLAUDE.md` y `docs/`).
   Es lo único que se copia desde afuera; todo el código lo escribe Claude Code. Lucas y Fadi **no necesitan el zip**: reciben los archivos con `git pull`.
3. Cada uno configura su identidad de Git en su clon.

**Cada vez que se trabaja**
4. Abrir una terminal en el clon y ejecutar `claude`.
5. Pegar el contenido del prompt propio (lo que está debajo de la línea `---` en `docs/prompts/PROMPT_<NOMBRE>.md`). Alternativa corta una vez
   hecho el `git pull`: *"Soy Lucas. Leé CLAUDE.md, docs/CONSIGNA.txt, docs/CONTRATOS.md y docs/prompts/PROMPT_LUCAS.md y hacé mis pasos de la Entrega 1."*
6. Claude Code hace **un commit por paso** con el mensaje exacto del plan y, después de cada uno, **explica en 3 a 5 líneas** qué se agregó y por qué.
   Hay que leer esa explicación y preguntar lo que no se entienda: es lo que después se defiende.
7. Si un paso falla (no compila o un test no pasa), se corrige **antes** de commitear.
8. Cuando un paso dice **PR**, Claude Code hace push y abre el PR. Lo integra el **revisor**, no quien lo abrió.
9. Si un paso tiene **puerta** y todavía no se cumplió, Claude Code avisa y se pasa a otro paso que no la tenga.
10. Para la Entrega 2: `git switch main && git pull`, abrir `claude` y decirle *"Seguí con la Entrega 2 de mi prompt"*.
11. Para revisar un PR de un compañero: *"Revisá el PR #N de <compañero> según las reglas de revisión de CLAUDE.md"*.

---

## 7. Tarea de cada integrante

### Nicolas

**En una frase:** armás la base del proyecto (Maven, menú y modo demo) y resolvés dos problemas: **ordenar el muro** (Divide y Conquista) y **formar equipos con restricciones** (Backtracking).

**Entrega 1:** base + muro. **Entrega 2:** equipos (B1).

#### Funcionalidad 1 — Muro de publicaciones (Divide y Conquista: Merge Sort) · Entrega 1

- **En palabras simples:** para ordenar miles de publicaciones, parto la lista a la mitad, ordeno cada mitad por separado (repitiendo lo mismo hasta que quedan listas de 0 o 1 elemento) y después las mezclo: comparo la primera de cada mitad y paso primero la que deba ir antes.
- **Por qué esta estrategia:** la consigna pide un ordenamiento propio que aguante volúmenes crecientes. Merge Sort garantiza O(n log n) *siempre*; Quick Sort puede degradar a O(n²) con malos pivotes.
- **Dónde se ve en el código:** `MuroOrdenador.mergeSort` calcula el punto medio (**dividir**), se llama a sí mismo para cada mitad (**conquistar**) y llama a `combinar` (**combinar**). Los dos criterios están en `CriterioMuro`:
  - *más recientes*: fecha descendente y, si empatan, id menor;
  - *más relevantes*: relevancia descendente (`reacciones + 2 × comentarios`), luego más reciente, luego id menor.
- **Complejidad:** `T(n) = 2·T(n/2) + O(n)` ⇒ **O(n log n)**; espacio extra O(n); es estable.
- **Ejemplo del demo:** las publicaciones 2 y 5 tienen relevancia 60, pero la 5 es más nueva y va primero; la 3 y la 7 son idénticas, así que gana el id menor (la 3).

#### B1 — Formación de equipos con restricciones (Backtracking) · Entrega 2

- **En palabras simples:** voy armando el equipo estudiante por estudiante y, para cada uno, decido si lo incluyo o no. Si en algún momento el equipo ya no puede salir válido, vuelvo atrás y no sigo por esa rama.
- **Por qué esta estrategia:** se piden **todos** los equipos válidos y las restricciones se pueden chequear de a una; Backtracking permite descartar ramas enteras en cuanto se rompen.
- **Cómo evito equipos repetidos:** los candidatos se consideran siempre en orden creciente de posición, así cada combinación se genera una sola vez (no importa el orden de los integrantes).
- **Podas:** (1) no se incluye a quien sea incompatible con alguien ya elegido; (2) si los candidatos que quedan no alcanzan para completar el equipo, se abandona; (3) si una habilidad requerida todavía no cubierta no la tiene ningún candidato restante (arreglo `sufijo`), se abandona.
- **Complejidad:** peor caso **O(C(n, k) · k)**; las podas lo reducen mucho en la práctica.
- **Ejemplo del demo:** equipos de 3, habilidades Java/SQL/UI/Testing, Lucia obligatoria y Mateo–Sofia incompatibles ⇒ **7 equipos válidos**.

**Preguntas típicas de defensa**
1. ¿Por qué Merge Sort y no Quick Sort o un ordenamiento simple?
2. ¿Qué pasa si dos publicaciones tienen la misma fecha y la misma relevancia?
3. ¿Dónde está exactamente "dividir", "conquistar" y "combinar" en tu código?
4. ¿Cómo evitás que el mismo equipo salga dos veces con los integrantes en otro orden?
5. ¿Qué podas aplicaste y por qué no descartan ningún equipo válido?

#### Commits de Nicolas — Entrega 1

| # | Día sugerido | Rama | Mensaje de commit | Archivos | PR |
|---|---|---|---|---|---|
| N1 | Mar 20/10 | `main` (directo) | `chore: crear proyecto Maven con Java 21 y JUnit 5` | `pom.xml`, `.gitignore` |  |
| N2 | Mar 20/10 | `main` (directo) | `docs: agregar consigna, plan de trabajo, contratos y prompts del equipo` | `CLAUDE.md`, `docs/CONSIGNA.txt`, `docs/CONTRATOS.md`, `docs/DIVISION_TAREAS.md`, `docs/INFORME.md`, `docs/prompts/PROMPT_NICOLAS.md`, `docs/prompts/PROMPT_LUCAS.md`, `docs/prompts/PROMPT_FADI.md` |  |
| N3 | Mar 20/10 | `main` (directo) | `feat(app): crear el menú principal con registro de opciones y modo demo` | `app/Opcion.java`, `app/Entrada.java`, `app/Main.java`, `test/app/EntradaTest.java` |  |
| N4 | Mar 20/10 | `feature/muro-merge-sort` | `feat(modelo): agregar record Publicacion con puntaje de relevancia` | `modelo/Publicacion.java` |  |
| N5 | Mar 20/10 | `feature/muro-merge-sort` | `feat(muro): definir criterios de orden por recencia y relevancia con desempates` | `muro/CriterioMuro.java` |  |
| N6 | Mié 21/10 | `feature/muro-merge-sort` | `feat(muro): implementar Merge Sort propio (dividir, conquistar y combinar)` | `muro/MuroOrdenador.java` |  |
| N7 | Mié 21/10 | `feature/muro-merge-sort` | `test(muro): probar orden, desempates, 5000 publicaciones y validaciones` | `test/muro/MuroOrdenadorTest.java`, `test/modelo/PublicacionTest.java` |  |
| N8 | Mié 21/10 | `feature/muro-merge-sort` | `feat(app): agregar la opción 1 del menú para consultar el muro` | `app/MuroConsola.java`, `app/Main.java` | sí |
| N9 | Jue 22/10 | `docs/readme-inicial` | `docs(readme): agregar descripción, requisitos y uso del proyecto` | `README.md` | sí |
| N10 | Jue 22/10 | `docs/informe-muro` | `docs(informe): completar sección 3 (muro de publicaciones)` | `docs/INFORME.md` | sí |

#### Commits de Nicolas — Entrega 2

| # | Día sugerido | Rama | Mensaje de commit | Archivos | PR |
|---|---|---|---|---|---|
| N11 | Lun 26/10 | `feature/equipos-backtracking` | `feat(modelo): agregar record Estudiante con habilidades` | `modelo/Estudiante.java` |  |
| N12 | Lun 26/10 | `feature/equipos-backtracking` | `feat(equipos): enumerar equipos que cubren las habilidades con backtracking` | `equipos/FormacionEquipos.java` |  |
| N13 | Mar 27/10 | `feature/equipos-backtracking` | `feat(equipos): respetar integrantes obligatorios y pares incompatibles` | `equipos/FormacionEquipos.java` |  |
| N14 | Mié 28/10 | `feature/equipos-backtracking` | `test(equipos): comparar contra fuerza bruta y probar casos sin solución` | `test/equipos/FormacionEquiposTest.java` |  |
| N15 | Mié 28/10 | `feature/equipos-backtracking` | `perf(equipos): podar por cantidad de candidatos y por habilidades alcanzables` | `equipos/FormacionEquipos.java` |  |
| N16 | Jue 29/10 | `feature/equipos-backtracking` | `feat(app): agregar la opción 5 del menú para formar equipos` | `app/EquiposConsola.java`, `app/Main.java` | sí |
| N17 | Lun 2/11 | `docs/informe-equipos` | `docs(informe): completar sección 7 (formación de equipos)` | `docs/INFORME.md` | sí |
| N18 | Mar 3/11 | `docs/informe-final` | `docs(informe): revisar resumen y supuestos con el proyecto terminado` | `docs/INFORME.md` | sí |

Revisa los PR de: **Lucas**. La especificación de cada paso (qué construir, tests exigidos y cómo verificar) está en `docs/prompts/PROMPT_NICOLAS.md`.

**Listo cuando:**

- [ ] Entrega 1: N1 a N3 en `main`, PR del muro integrado, README inicial e informe sección 3; tag `entrega-1`.
- [ ] Entrega 2: PR de equipos integrado, informe secciones 7 y revisión final; tag `entrega-2`.
- [ ] `mvn -q test` pasa y el demo muestra el muro (dos órdenes) y los equipos.
- [ ] No hay ningún `sort` de Java en `src/main` (solo en comentarios de Javadoc).
- [ ] Podés explicar sin mirar el código el Merge Sort y las podas de B1.

---

### Lucas

**En una frase:** armás la **red de usuarios** (el grafo) y resolvés dos problemas sobre ella: la **cadena de difusión más rápida** (Greedy) y las **consultas entre todos los pares** (Programación Dinámica).

**Entrega 1:** `Grafo` + Dijkstra. **Entrega 2:** Floyd-Warshall (A1) + test de integración + README final.

#### Grafo (soporte de las demás funcionalidades) · Entrega 1

- Es una red **dirigida y ponderada**: cada vínculo va de un usuario a otro con un tiempo positivo en minutos. Se guarda con **lista de adyacencia** porque las redes sociales tienen pocos vínculos comparados con todos los pares posibles (espacio O(V + E)). Si se carga dos veces el mismo vínculo, se queda el de menor tiempo.

#### Funcionalidad 2 — Cadena de difusión más rápida (Greedy: Dijkstra) · Entrega 1

- **En palabras simples:** parto del origen con tiempo 0. Siempre **fijo** al usuario aún no fijado que tenga el menor tiempo acumulado y miro sus vínculos: si pasando por él llego antes a un vecino, le actualizo el tiempo (*relajación*) y anoto por quién llegué. Cuando fijo el destino, terminé; la cadena sale siguiendo los "por quién llegué" hacia atrás.
- **Por qué es Greedy y por qué es correcto:** la decisión local ("el de menor tiempo entre los no fijados") es definitiva porque todos los tiempos son positivos: ninguna otra cadena puede llegar a ese usuario más rápido.
- **Dónde se ve en el código:** `PriorityQueue` + `fijado[u] = true` es la elección voraz; el bloque de relajación actualiza `dist` y `predecesor`; la **traza** guarda cada decisión (es lo que pide "visualizar las principales decisiones").
- **Complejidad:** **O((V + E) log V)**.
- **Ejemplo del demo:** Ana → Gina = **27 min** por `Ana → Carla → Bruno → Diego → Eva → Facu → Gina`. Bruno primero recibe 10 (directo desde Ana) y después se mejora a 8 pasando por Carla: eso es una relajación. Ana → Hugo: no alcanzable.

#### A1 — Consultas entre todos los usuarios (Programación Dinámica: Floyd-Warshall) · Entrega 2

- **En palabras simples:** antes de contestar consultas, calculo **una sola vez** el tiempo mínimo entre todos los pares. Voy habilitando de a un usuario `k` como posible intermediario y, para cada par `(i, j)`, me pregunto si pasar por `k` mejora el tiempo que ya tengo. También guardo el "siguiente paso" de cada par para poder armar la cadena.
- **Por qué es Programación Dinámica:** el subproblema es "mejor cadena de `i` a `j` usando solo los usuarios `0..k` como intermediarios" y cada resultado se reutiliza para el siguiente `k`.
- **Por qué sirve para este caso:** la consigna pide un procesamiento inicial y consultas sin repetir el cálculo. Se paga O(V³) una vez; después cada consulta es inmediata. Repetir Dijkstra por cada consulta sería mucho más caro.
- **Complejidad:** preprocesamiento **O(V³)**, espacio O(V²); consulta **O(1)** para el tiempo (más O(L) para armar una cadena de L usuarios).
- **Ejemplo del demo:** la matriz da Ana → Gina = 27, igual que Dijkstra; la columna de Hugo está en "-" porque nadie le transmite a él.

**Preguntas típicas de defensa**
1. ¿Por qué la elección de Dijkstra es correcta? ¿Qué pasaría con tiempos negativos?
2. ¿Qué es una relajación? Mostrá una del demo.
3. ¿Para qué sirve la traza y qué registra?
4. ¿Cuándo conviene Floyd-Warshall y cuándo Dijkstra?
5. ¿Qué guarda la matriz `siguiente` y cómo se reconstruye una cadena?
6. ¿Cómo se informa que no hay cadena entre dos usuarios?

#### Commits de Lucas — Entrega 1

| # | Día sugerido | Rama | Mensaje de commit | Archivos | PR |
|---|---|---|---|---|---|
| L1 | Mar 20/10 | `feature/grafo` | `feat(grafo): agregar red dirigida y ponderada con lista de adyacencia` | `grafo/Grafo.java` |  |
| L2 | Mar 20/10 | `feature/grafo` | `feat(grafo): conservar un solo vínculo por par de usuarios (el de menor tiempo)` | `grafo/Grafo.java` |  |
| L3 | Mar 20/10 | `feature/grafo` | `test(grafo): validar vínculos dirigidos, repetidos y entradas inválidas` | `test/grafo/GrafoTest.java` | sí |
| L4 | Mié 21/10 | `feature/dijkstra` | `feat(difusion): agregar ResultadoCamino con cadena, tiempo y traza` | `difusion/ResultadoCamino.java` |  |
| L5 | Mié 21/10 | `feature/dijkstra` | `feat(difusion): implementar Dijkstra para la cadena más rápida` | `difusion/CadenaMasRapida.java` |  |
| L6 | Mié 21/10 | `feature/dijkstra` | `feat(difusion): registrar las decisiones del cálculo en una traza` | `difusion/CadenaMasRapida.java` |  |
| L7 | Mié 21/10 | `feature/dijkstra` | `test(difusion): probar camino mínimo, inalcanzable y sentido de los vínculos` | `test/difusion/CadenaMasRapidaTest.java` |  |
| L8 | Jue 22/10 | `feature/dijkstra` | `feat(app): agregar la red de ejemplo y la opción 2 del menú (cadena más rápida)` | `app/DatosRed.java`, `app/CadenaRapidaConsola.java`, `app/Main.java` | sí |
| L9 | Jue 22/10 | `docs/informe-dijkstra` | `docs(informe): completar sección 4 (cadena de difusión más rápida)` | `docs/INFORME.md` | sí |

#### Commits de Lucas — Entrega 2

| # | Día sugerido | Rama | Mensaje de commit | Archivos | PR |
|---|---|---|---|---|---|
| L10 | Lun 26/10 | `feature/floyd-warshall` | `feat(difusion): precalcular tiempos mínimos entre todos los pares con Floyd-Warshall (A1)` | `difusion/ConsultasDifusion.java` |  |
| L11 | Mar 27/10 | `feature/floyd-warshall` | `feat(difusion): reconstruir la cadena de cada par y mostrar la matriz de tiempos` | `difusion/ConsultasDifusion.java` |  |
| L12 | Mié 28/10 | `feature/floyd-warshall` | `test(difusion): comparar Floyd-Warshall contra Dijkstra en redes al azar` | `test/difusion/ConsultasDifusionTest.java` |  |
| L13 | Jue 29/10 | `feature/floyd-warshall` | `feat(app): agregar la opción 4 del menú para consultar entre todos los usuarios` | `app/ConsultasConsola.java`, `app/Main.java` | sí |
| L14 | Lun 2/11 | `docs/informe-floyd` | `docs(informe): completar sección 6 (consultas entre todos los usuarios)` | `docs/INFORME.md` | sí |
| L15 | Lun 2/11 | `test/demo-integral` | `test(app): probar el modo demo de punta a punta con las 6 funcionalidades` | `test/app/MainDemoTest.java` | sí |
| L16 | Mar 3/11 | `docs/readme-final` | `docs(readme): documentar las 6 funcionalidades y el uso final del menú` | `README.md` | sí |

Revisa los PR de: **Fadi**. La especificación de cada paso (qué construir, tests exigidos y cómo verificar) está en `docs/prompts/PROMPT_LUCAS.md`.

**Listo cuando:**

- [ ] Entrega 1: PR de `Grafo` y de Dijkstra integrados; informe sección 4.
- [ ] Entrega 2: PR de Floyd-Warshall integrado, informe sección 6, test de integración y README final.
- [ ] El demo muestra Ana → Gina = 27 min con la traza y la matriz de Floyd-Warshall.
- [ ] Podés explicar sin mirar el código una relajación de Dijkstra y la recurrencia de Floyd-Warshall.

---

### Fadi

**En una frase:** resolvés dos problemas: **qué iniciativas financiar con presupuesto limitado** (Programación Dinámica) y **buscar todas las cadenas de difusión alternativas** con condiciones (Backtracking).

**Entrega 1:** iniciativas (mochila 0/1). **Entrega 2:** cadenas alternativas (B3) + sección de verificación del informe.

#### Funcionalidad 3 — Selección de iniciativas (Programación Dinámica: mochila 0/1) · Entrega 1

- **En palabras simples:** armo una tabla: cada fila es "cuántas iniciativas consideré" y cada columna es "cuánto presupuesto tengo" (de 0 a P). En cada celda guardo el máximo impacto posible. Para cada iniciativa decido: **no financiarla** (copio la celda de arriba) o **financiarla** (su impacto + lo mejor que pude hacer con el presupuesto que me queda). Me quedo con la mayor. Al final recorro la tabla hacia atrás para saber cuáles se eligieron.
- **Por qué Programación Dinámica y no Greedy:** una iniciativa se financia completa o no se financia. Con presupuesto 50 y las iniciativas (costo, impacto) = (10, 60), (20, 100), (30, 120), un Greedy por razón impacto/costo toma las dos primeras y logra 160; el óptimo es 220 (la segunda y la tercera).
- **Dónde se ve en el código:** la tabla `dp` y su recurrencia en los dos ciclos anidados; la reconstrucción recorre desde `dp[n][P]` hacia atrás (si `dp[i][w] != dp[i-1][w]`, la iniciativa `i` fue elegida).
- **Complejidad:** **O(n · P)** (pseudopolinomial); espacio O(n · P).
- **Ejemplo del demo:** presupuesto 50 ⇒ iniciativas #1, #2 y #4, costo 45, impacto **230**, presupuesto restante 5.

#### B3 — Cadenas alternativas de difusión (Backtracking) · Entrega 2

- **En palabras simples:** salgo del origen y recorro todos los caminos posibles sin repetir usuarios. Cada vez que llego al destino anoto la cadena; al volver atrás desmarco al usuario para probar otro camino. Corto un camino apenas se pasa del tiempo máximo o de los intermediarios permitidos, o si pasa por un usuario excluido.
- **Por qué Backtracking y no Dijkstra:** se piden **todas** las cadenas que cumplan las condiciones, no solo la mejor.
- **Dónde se ve en el código:** `Exploracion.explorar` marca `visitado`, agrega al `camino`, explora y luego deshace (**backtrack**). Podas: no repetir ni pasar por excluidos; tiempo acumulado mayor al máximo (correcto porque los tiempos son positivos y la suma solo crece); límite de intermediarios.
- **Complejidad:** peor caso **O(V!)**, acotado en la práctica por el tiempo máximo y el límite de intermediarios.
- **Ejemplo del demo:** Ana → Gina con 40 min y hasta 4 intermediarios ⇒ **10 cadenas**; la más rápida es de **28 min**, más lenta que los 27 min de Dijkstra porque esa cadena usa 5 intermediarios y el límite lo impide.

**Preguntas típicas de defensa**
1. ¿Por qué un Greedy falla en la mochila 0/1? Mostrá el contraejemplo.
2. ¿Qué significa cada celda de la tabla y cómo se reconstruye la solución?
3. ¿Por qué se dice que la mochila es pseudopolinomial?
4. ¿En qué se diferencia B3 de la funcionalidad 2 (Dijkstra)?
5. ¿Por qué podar por tiempo máximo es correcto?
6. ¿Dónde está el "deshacer" del backtracking en tu código?

#### Commits de Fadi — Entrega 1

| # | Día sugerido | Rama | Mensaje de commit | Archivos | PR |
|---|---|---|---|---|---|
| F1 | Mar 20/10 | `feature/iniciativas-dp` | `feat(modelo): agregar record Iniciativa con validaciones` | `modelo/Iniciativa.java` |  |
| F2 | Mar 20/10 | `feature/iniciativas-dp` | `feat(financiamiento): llenar la tabla de programación dinámica de la mochila 0/1` | `financiamiento/SeleccionIniciativas.java` |  |
| F3 | Mié 21/10 | `feature/iniciativas-dp` | `feat(financiamiento): reconstruir las iniciativas elegidas y el presupuesto restante` | `financiamiento/SeleccionIniciativas.java` |  |
| F4 | Mié 21/10 | `feature/iniciativas-dp` | `test(financiamiento): probar el caso donde Greedy falla y los casos borde` | `test/financiamiento/SeleccionIniciativasTest.java` |  |
| F5 | Mié 21/10 | `feature/iniciativas-dp` | `test(financiamiento): comparar contra fuerza bruta en 300 casos al azar` | `test/financiamiento/SeleccionIniciativasTest.java` |  |
| F6 | Mié 21/10 | `feature/iniciativas-dp` | `feat(app): agregar la opción 3 del menú para seleccionar iniciativas` | `app/IniciativasConsola.java`, `app/Main.java` | sí |
| F7 | Jue 22/10 | `docs/informe-iniciativas` | `docs(informe): completar sección 5 (selección de iniciativas)` | `docs/INFORME.md` | sí |

#### Commits de Fadi — Entrega 2

| # | Día sugerido | Rama | Mensaje de commit | Archivos | PR |
|---|---|---|---|---|---|
| F8 | Lun 26/10 | `feature/cadenas-alternativas` | `feat(difusion): enumerar todas las cadenas sin repetir usuarios con backtracking (B3)` | `difusion/CadenasAlternativas.java` |  |
| F9 | Mar 27/10 | `feature/cadenas-alternativas` | `feat(difusion): respetar usuarios excluidos y validar los parámetros` | `difusion/CadenasAlternativas.java` |  |
| F10 | Mié 28/10 | `feature/cadenas-alternativas` | `feat(difusion): filtrar cadenas por tiempo máximo y cantidad de intermediarios` | `difusion/CadenasAlternativas.java` |  |
| F11 | Jue 29/10 | `feature/cadenas-alternativas` | `test(difusion): probar cadenas, límites, excluidos y validaciones` | `test/difusion/CadenasAlternativasTest.java` |  |
| F12 | Vie 30/10 | `feature/cadenas-alternativas` | `test(difusion): comparar B3 contra una enumeración exhaustiva de caminos` | `test/difusion/CadenasAlternativasTest.java` |  |
| F13 | Vie 30/10 | `feature/cadenas-alternativas` | `perf(difusion): podar por tiempo máximo e intermediarios antes de seguir explorando` | `difusion/CadenasAlternativas.java` |  |
| F14 | Vie 30/10 | `feature/cadenas-alternativas` | `feat(app): agregar la opción 6 del menú para buscar cadenas alternativas` | `app/CadenasAlternativasConsola.java`, `app/Main.java` | sí |
| F15 | Lun 2/11 | `docs/informe-cadenas` | `docs(informe): completar sección 8 (cadenas alternativas)` | `docs/INFORME.md` | sí |
| F16 | Mar 3/11 | `docs/informe-verificacion` | `docs(informe): completar sección 9 (verificación y pruebas)` | `docs/INFORME.md` | sí |

Revisa los PR de: **Nicolas**. La especificación de cada paso (qué construir, tests exigidos y cómo verificar) está en `docs/prompts/PROMPT_FADI.md`.

**Listo cuando:**

- [ ] Entrega 1: PR de iniciativas integrado; informe sección 5.
- [ ] Entrega 2: PR de cadenas alternativas integrado; informe secciones 8 y 9.
- [ ] El demo muestra la selección con impacto 230 y las 10 cadenas alternativas.
- [ ] Podés explicar sin mirar el código la tabla de la mochila y el "deshacer" del backtracking.

---

## 8. Cierre de cada entrega

**Cierre de la Entrega 1 (vie 23/10)**
- [ ] Todos los PR de la Entrega 1 están integrados (sin squash) y `main` está actualizada en la máquina de cada uno.
- [ ] `mvn -q test` y `mvn -q package` pasan; `java -jar target/red-social-universitaria-1.0.0.jar --demo` muestra las opciones 1, 2 y 3 con los
      resultados de `docs/CONTRATOS.md` §5.
- [ ] El informe tiene completas las secciones 1 a 5 (las 6 a 9 quedan marcadas como pendientes de la Entrega 2).
- [ ] Nicolas crea y sube el tag `entrega-1`.

**Cierre de la Entrega 2 (mar 3/11)**
- [ ] Todos los PR están integrados, incluidos informe, test de integración y README final.
- [ ] `mvn -q test`, `mvn -q package` y el demo muestran las 6 opciones con los resultados esperados; `MainDemoTest` en verde.
- [ ] `grep -n 'Pendiente' docs/INFORME.md` no devuelve nada.
- [ ] Nicolas crea y sube el tag `entrega-2`.

## 9. Checklist de la consigna

- [ ] Las 3 funcionalidades obligatorias (muro, cadena más rápida, iniciativas).
- [ ] 3 adicionales: **una del bloque A (A1)** y **dos del bloque B (B1, B3)**.
- [ ] Aparecen las 4 estrategias y **se reconocen en el código**, no solo en el informe.
- [ ] El muro se ordena **sin** `Arrays.sort`, `Collections.sort`, `List.sort` ni `sorted()` (y no hay `TreeMap`/`TreeSet` en `src/main`).
- [ ] Para cada funcionalidad: análisis, estrategia, justificación, implementación, casos de prueba y complejidad (en `docs/INFORME.md`).
- [ ] Cadena más rápida: muestra la cadena, los usuarios, el tiempo total, avisa si no es alcanzable y permite ver las decisiones.
- [ ] Iniciativas: muestra las seleccionadas, el costo total, el impacto y el presupuesto restante.
- [ ] Los tres tienen commits propios, repartidos en el tiempo, con mensajes descriptivos.
- [ ] Ningún commit masivo cerca de las entregas; PR integrados **sin squash**.

## 10. Para la defensa

Cada integrante debería poder, sin mirar el código: explicar su estrategia y por qué es la adecuada, dibujar la recurrencia o el árbol de
decisiones de su algoritmo, justificar su complejidad y explicar qué casos de prueba la cubren. Además conviene entender a grandes rasgos las
partes de los otros dos (son las que revisó).
