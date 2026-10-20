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
