# Red Social Universitaria

TP Obligatorio de **Programación III** (UADE) — Propuesta 1.
Integrantes: **Nicolas Facendini**, **Lucas Felice** y **Fadi Abdala**.

Aplicación de consola en Java que resuelve problemas de una red social universitaria aplicando distintas
estrategias algorítmicas, cada una reconocible en el código.
El **informe** se encuentra en la carpeta docs

## Funcionalidades

| # | Funcionalidad | Estrategia | Entrega |
|---|---|---|---|
| 1 | Muro de publicaciones | Divide y Conquista (Merge Sort) | 1 |
| 2 | Cadena de difusión más rápida | Greedy (Dijkstra) | 1 |
| 3 | Selección de iniciativas | Programación Dinámica (mochila 0/1) | 1 |
| 4 | A1: consultas entre todos los usuarios | Programación Dinámica (Floyd-Warshall) | 2 |
| 5 | B1: equipos con restricciones | Backtracking (equipos) | 2 |
| 6 | B3: cadenas alternativas | Backtracking (cadenas alternativas) | 2 |

## Requisitos

- Java 21
- Maven 3.9+

## Uso

```bash
mvn test                                                      # corre los tests
mvn package                                                   # genera el jar
java -jar target/red-social-universitaria-1.0.0.jar          # menú interactivo
java -jar target/red-social-universitaria-1.0.0.jar --demo   # corre todas las opciones con valores por defecto
```

## Uso del menú

Al ejecutar el jar sin argumentos aparece el menú; se elige una opción escribiendo su número y `0` sale.
Cada opción usa datos de ejemplo fijos y pide solo parámetros simples: **con Enter se usa el valor por defecto** que figura entre corchetes (`[Por defecto: …]`).

| Opción | Qué hace | Parámetros (por defecto) |
|---|---|---|
| 1 | Muro de publicaciones ordenado por recencia y por relevancia | — |
| 2 | Cadena de difusión más rápida, mostrando las decisiones de Dijkstra | Origen (`Ana`), destino (`Gina`) |
| 3 | Iniciativas a financiar con un presupuesto | Presupuesto (`50`) |
| 4 | Tiempos mínimos entre todos los usuarios y dos consultas | Origen/destino (`Ana`→`Gina` y `Ana`→`Hugo`) |
| 5 | Equipos válidos con restricciones | Datos de ejemplo de la consola |
| 6 | Todas las cadenas de difusión que cumplen las restricciones, destacando la más rápida | Origen (`Ana`), destino (`Gina`), tiempo máximo (`40` min), máximo de intermediarios (`4`) y usuarios excluidos (ninguno) |

El modo `--demo` ejecuta todas las opciones registradas con sus valores por defecto, sin pedir nada.

Ejemplo de sesión corta (opción 4, salida real recortada; se presionó Enter en cada pregunta):

```
--- A1 · Consultas de difusión entre todos los usuarios (Programación Dinámica: Floyd-Warshall) ---
Procesamiento inicial completo. Tiempos mínimos en minutos ('-' = sin cadena):
       Ana    Bruno  Carla  Diego  Eva    Facu   Gina   Hugo
Ana    0      8      5      15     19     25     27     -
...
Origen [Por defecto: Ana]: Destino [Por defecto: Gina]: Cadena: Ana -> Carla -> Bruno -> Diego -> Eva -> Facu -> Gina | Tiempo total: 27 min
Origen [Por defecto: Ana]: Destino [Por defecto: Hugo]: No existe cadena de difusión de Ana a Hugo
```

## Pruebas

`mvn test` ejecuta los tests con JUnit 5. Resultado verificado: **44 tests, 0 fallas, 0 errores**, por clase:

| Clase de test | Tests |
|---|---|
| `EntradaTest` | 3 |
| `PublicacionTest` | 2 |
| `MuroOrdenadorTest` | 5 |
| `GrafoTest` | 4 |
| `CadenaMasRapidaTest` | 6 |
| `ConsultasDifusionTest` | 4 |
| `SeleccionIniciativasTest` | 6 |
| `FormacionEquiposTest` | 7 |
| `CadenasAlternativasTest` | 6 |
| `MainDemoTest` | 1 |
| **Total** | **44** |

`MainDemoTest` es el test de integración: corre el `--demo` completo y verifica una línea clave por funcionalidad.

## Estrategias, clases y complejidad

| # | Estrategia | Clase principal (`redsocial/…`) | Complejidad temporal |
|---|---|---|---|
| 1 | Divide y Conquista (Merge Sort) | `muro/MuroOrdenador` | O(n log n) |
| 2 | Greedy (Dijkstra) | `difusion/CadenaMasRapida` | O((V + E) log V) |
| 3 | Programación Dinámica (mochila 0/1) | `financiamiento/SeleccionIniciativas` | O(n · P) |
| 4 | Programación Dinámica (Floyd-Warshall) | `difusion/ConsultasDifusion` | O(V³) de preproceso; consulta O(1) |
| 5 | Backtracking | `equipos/FormacionEquipos` | Exponencial en el peor caso, reducido por las podas |
| 6 | Backtracking | `difusion/CadenasAlternativas` | Exponencial en el peor caso, reducido por las podas |

## Estructura de paquetes

Bajo `src/main/java/redsocial/`:

| Paquete | Contenido |
|---|---|
| `app` | Menú (`Main`, `Opcion`, `Entrada`), `DatosRed` y las consolas de cada funcionalidad |
| `modelo` | Datos del dominio: `Publicacion`, `Iniciativa`, `Estudiante` |
| `muro` | `CriterioMuro`, `MuroOrdenador` (Merge Sort) |
| `grafo` | `Grafo` de la red de difusión |
| `difusion` | `CadenaMasRapida`, `ConsultasDifusion`, `CadenasAlternativas`, `ResultadoCamino` |
| `financiamiento` | `SeleccionIniciativas` |
| `equipos` | `FormacionEquipos` |

Los tests están en `src/test/java/redsocial/` con los mismos paquetes.

