# CLAUDE.md — contexto del proyecto para Claude Code

Proyecto: **TP Obligatorio de Programación III (UADE)** — Propuesta 1, *Red Social Universitaria para la Colaboración*.
Equipo de 3: **Nicolas**, **Lucas** y **Fadi**. Java 21 · Maven · JUnit 5. Sin otras dependencias.

## Fuentes de verdad (en este orden)

1. `docs/CONSIGNA.txt` — la consigna oficial. Si algo la contradice, gana la consigna.
2. `docs/CONTRATOS.md` — interfaces compartidas entre los tres (menú, `Grafo`, líneas de salida, resultados esperados).
3. `docs/DIVISION_TAREAS.md` y `docs/prompts/PROMPT_<NOMBRE>.md` — qué hace cada persona, en qué orden y con qué commits.

## Cómo trabajar

1. Cada integrante corre Claude Code en **su propio clon** y le pasa su prompt. Hacés **solo los pasos de esa persona**, en orden.
2. **No hay código de referencia.** Escribís el código vos, a partir de la especificación de cada paso, respetando la consigna y los
   contratos. Si algo es ambiguo, contradice la consigna o los contratos, o no se puede cumplir: **preguntá antes de inventar**.
3. **Un commit por paso**, con el mensaje exacto del plan y solo los archivos que lista el paso (`git add <archivo>`, nunca `git add .`).
   Los commits salen con la identidad Git de esa persona (`git config user.name` / `user.email`). Nunca uses otra identidad ni `--date`.
4. Antes de cada commit: `mvn -q test` en verde. Si falla, corregilo primero.
5. **Después de cada commit explicale a la persona, en 3 a 5 líneas y en lenguaje simple:** qué se agregó, por qué se hizo así y qué
   parte de la consigna cubre. La persona tiene que poder explicarlo en la defensa; si no entiende algo, explicalo mejor.
6. Calidad: código claro y en español; Javadoc con estrategia, justificación y complejidad en cada clase de algoritmo; comentarios de
   fase en el código (ver `docs/CONTRATOS.md` §6). La estrategia tiene que **reconocerse en el código**.
7. No edites archivos de otra persona. Excepción: descomentar tu propia línea `OPCIONES[n] = …;` en `Main.java`.

## Reglas de la consigna (no negociables)

- 3 funcionalidades obligatorias + **A1** + **B1** + **B3**.
- Las 4 estrategias reconocibles en el código: Divide y Conquista, Greedy, Programación Dinámica y Backtracking.
- El muro se ordena con un ordenamiento propio: **prohibido** `Arrays.sort`, `Collections.sort`, `List.sort`, `stream().sorted()`,
  `TreeMap`, `TreeSet` y `Comparator` para ordenar, en todo `src/main`.
- Historial de Git con aportes significativos y distribuidos de los tres, cada uno desde su cuenta. Sin carga masiva final.

## Dos entregas

| Entrega | Contenido | Cuándo | Cómo se marca |
|---|---|---|---|
| **1** | Etapa 1: muro, cadena más rápida e iniciativas + menú + README + informe (secciones 1 a 5) | esta semana | tag `entrega-1` |
| **2** | A1, B1 y B3 + test de integración + README e informe completos | en dos semanas | tag `entrega-2` |

`main` siempre tiene que compilar y pasar los tests. Los tags los pone Nicolas al cierre de cada entrega.

## Mapa del proyecto

| Funcionalidad | Estrategia | Clase principal (`src/main/java/redsocial/…`) | Responsable |
|---|---|---|---|
| 1 Muro de publicaciones | Divide y Conquista (Merge Sort) | `muro/MuroOrdenador` | Nicolas |
| 2 Cadena de difusión más rápida | Greedy (Dijkstra) | `difusion/CadenaMasRapida` | Lucas |
| 3 Selección de iniciativas | Programación Dinámica (mochila 0/1) | `financiamiento/SeleccionIniciativas` | Fadi |
| A1 Consultas entre todos los usuarios | Programación Dinámica (Floyd-Warshall) | `difusion/ConsultasDifusion` | Lucas |
| B1 Equipos con restricciones | Backtracking | `equipos/FormacionEquipos` | Nicolas |
| B3 Cadenas alternativas | Backtracking | `difusion/CadenasAlternativas` | Fadi |

## Decisiones ya tomadas (no cambiarlas sin hablarlo con el equipo)

- **Relevancia** = `reacciones + 2 × comentarios` (`Publicacion.PESO_COMENTARIO`). Desempate: más reciente y luego id menor.
- `Grafo` guarda **un solo vínculo por par ordenado** de usuarios (el de menor tiempo).
- B3: intermediarios = usuarios de la cadena sin contar origen ni destino; origen y destino deben ser distintos.
- Datos de ejemplo fijos dentro de cada consola; el menú pide solo parámetros simples, siempre con valor por defecto.

## Git

- Una rama por tema, desde `main` actualizada. Nada va directo a `main`, salvo N1, N2 y N3 de Nicolas.
- Mensaje de commit: `tipo(ámbito): qué se hizo`, en imperativo y en español. Tipos: `feat`, `fix`, `test`, `docs`, `perf`, `chore`, `refactor`.
- Un PR por rama (`gh pr create` si está `gh`; si no, indicá cómo abrirlo). El PR lo integra **quien lo revisa**, con *Merge commit* o
  *Rebase and merge*, **nunca *Squash*** (se perdería la evidencia de trabajo individual que pide la consigna).
- **Cuando te pidan revisar un PR:** compilá y corré `mvn -q test`; contrastá con la consigna, los contratos y la especificación del paso;
  verificá que la estrategia se reconozca en el código, que no haya ordenamientos del JDK y que cada commit sea coherente y esté bien
  nombrado; dejá al menos un comentario real y recién entonces integralo.

## Comandos

```bash
mvn -q compile                      # compila
mvn -q test                         # todos los tests
mvn -q test -Dtest=NombreTest       # un test
mvn -q package                      # genera el jar
java -jar target/red-social-universitaria-1.0.0.jar          # menú interactivo
java -jar target/red-social-universitaria-1.0.0.jar --demo   # corre todas las opciones con valores por defecto
```
