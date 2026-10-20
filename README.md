# Red Social Universitaria para la Colaboración

TP Obligatorio de **Programación III** (UADE) — Propuesta 1.
Integrantes: **Nicolas**, **Lucas** y **Fadi**.

Aplicación de consola en Java que resuelve problemas de una red social universitaria aplicando distintas
estrategias algorítmicas, cada una reconocible en el código.

## Funcionalidades

| # | Funcionalidad | Estrategia | Entrega |
|---|---|---|---|
| 1 | Muro de publicaciones | Divide y Conquista (Merge Sort) | 1 — disponible |
| 2 | Cadena de difusión más rápida | Greedy (Dijkstra) | 1 — disponible |
| 3 | Selección de iniciativas | Programación Dinámica (mochila 0/1) | 1 — disponible |
| 4 | A1: consultas entre todos los usuarios | Programación Dinámica (Floyd-Warshall) | Entrega 2 |
| 5 | B1: equipos con restricciones | Backtracking (equipos) | Entrega 2 |
| 6 | B3: cadenas alternativas | Backtracking (cadenas alternativas) | Entrega 2 |

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

## Notas

- No se usa ningún método de ordenamiento del JDK; el muro se ordena con un Merge Sort propio.
- Si la consola muestra mal las tildes: `java -Dstdout.encoding=UTF-8 -jar target/red-social-universitaria-1.0.0.jar`
  (en Windows, ejecutar antes `chcp 65001`).
