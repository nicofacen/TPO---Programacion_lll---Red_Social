# Contratos entre módulos

Cada integrante genera su parte con Claude Code **sin código de referencia**. Para que las partes encajen al integrarse,
todo lo que cruza la frontera entre dos personas está fijado en este archivo. **Si necesitás cambiar algo de acá, ver §7.**

## 1. Estructura de archivos y responsables

| Archivo (bajo `src/main/java/redsocial/`) | Responsable | Entrega |
|---|---|---|
| `app/Opcion`, `app/Entrada`, `app/Main` | Nicolas | 1 |
| `app/DatosRed` | Lucas | 1 |
| `app/MuroConsola` | Nicolas | 1 |
| `app/CadenaRapidaConsola` | Lucas | 1 |
| `app/IniciativasConsola` | Fadi | 1 |
| `app/ConsultasConsola` (A1) | Lucas | 2 |
| `app/EquiposConsola` (B1) | Nicolas | 2 |
| `app/CadenasAlternativasConsola` (B3) | Fadi | 2 |
| `modelo/Publicacion`, `muro/CriterioMuro`, `muro/MuroOrdenador` | Nicolas | 1 |
| `modelo/Estudiante`, `equipos/FormacionEquipos` | Nicolas | 2 |
| `grafo/Grafo`, `difusion/ResultadoCamino`, `difusion/CadenaMasRapida` | Lucas | 1 |
| `difusion/ConsultasDifusion` | Lucas | 2 |
| `modelo/Iniciativa`, `financiamiento/SeleccionIniciativas` | Fadi | 1 |
| `difusion/CadenasAlternativas` | Fadi | 2 |

Los tests viven en `src/test/java/redsocial/` con los mismos paquetes (`XTest` por cada clase `X`). Nadie edita archivos de otra persona,
salvo descomentar **su propia línea** de registro en `Main.java` (ver §2).

## 2. Menú, entrada de datos y registro de opciones (Nicolas, paso N3)

Cada funcionalidad es una clase `*Consola` que implementa `Opcion` y se registra en `Main`. Las líneas de registro están
**separadas a propósito** para que Git combine los cambios de los tres sin conflictos. Copiá estos tres archivos tal cual.

`app/Opcion.java`

```java
package redsocial.app;

import java.util.Scanner;

/**
 * Una opción del menú principal (una funcionalidad del sistema).
 *
 * <p>Cada funcionalidad tiene su propia clase {@code *Consola} que implementa esta interfaz
 * y se registra en {@link Main}.</p>
 */
public interface Opcion {

    /** Texto de una línea para el menú. Incluye la estrategia, por ejemplo "(Greedy: Dijkstra)". */
    String titulo();

    /**
     * Ejecuta la funcionalidad con datos de ejemplo. Toda lectura de datos del usuario debe hacerse con
     * {@link Entrada}, ofreciendo siempre un valor por defecto, para que el modo {@code --demo} funcione solo.
     */
    void ejecutar(Scanner in);
}
```

`app/Entrada.java`

```java
package redsocial.app;

import java.util.Scanner;

/**
 * Lectura de datos por consola con valor por defecto.
 *
 * <p>Si el usuario presiona Enter sin escribir nada se usa el valor por defecto. Así el modo
 * {@code --demo} puede ejecutar todo el sistema alimentándolo con líneas vacías.</p>
 */
public final class Entrada {

    private Entrada() { }

    /** Muestra "mensaje [defecto]: " y devuelve la línea leída (sin espacios extremos) o el valor por defecto. */
    public static String leer(Scanner in, String mensaje, String defecto) {
        System.out.print(mensaje + (defecto.isEmpty() ? "" : " [" + defecto + "]") + ": ");
        String linea = in.nextLine();
        return linea.isBlank() ? defecto : linea.trim();
    }

    /** Igual que {@link #leer} pero devuelve un entero; si no es un número lanza {@link IllegalArgumentException}. */
    public static int leerEntero(Scanner in, String mensaje, int defecto) {
        String texto = leer(in, mensaje, String.valueOf(defecto));
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + texto + "' no es un número entero");
        }
    }
}
```

`app/Main.java` (las seis líneas `OPCIONES[n] = …` están comentadas: cada integrante descomenta SOLO la suya)

```java
package redsocial.app;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Menú principal de consola de la Red Social Universitaria.
 *
 * <ul>
 *   <li>{@code java -jar red-social-universitaria-1.0.0.jar}: menú interactivo.</li>
 *   <li>{@code java -jar red-social-universitaria-1.0.0.jar --demo}: ejecuta todas las opciones
 *       registradas con sus valores por defecto.</li>
 * </ul>
 */
public final class Main {

    /** La posición es el número de opción (1 a 6). La posición 0 no se usa. */
    static final Opcion[] OPCIONES = new Opcion[7];

    static {
        // ===== REGISTRO DE OPCIONES =====
        // Cada integrante descomenta SOLO su línea cuando su clase *Consola ya existe.
        // Las líneas están separadas a propósito para que Git combine los cambios sin conflictos.

        // 1 · Muro de publicaciones (Nicolas)
        // OPCIONES[1] = new MuroConsola();

        // 2 · Cadena de difusión más rápida (Lucas)
        // OPCIONES[2] = new CadenaRapidaConsola();

        // 3 · Selección de iniciativas (Fadi)
        // OPCIONES[3] = new IniciativasConsola();

        // 4 · A1 Consultas entre todos los usuarios (Lucas)
        // OPCIONES[4] = new ConsultasConsola();

        // 5 · B1 Formación de equipos (Nicolas)
        // OPCIONES[5] = new EquiposConsola();

        // 6 · B3 Cadenas alternativas (Fadi)
        // OPCIONES[6] = new CadenasAlternativasConsola();
    }

    private Main() { }

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        if (args.length > 0 && args[0].equals("--demo")) {
            demo();
            return;
        }
        Scanner in = new Scanner(System.in);
        try {
            boolean salir = false;
            while (!salir) {
                mostrarMenu();
                String eleccion = Entrada.leer(in, "Opción", "");
                if (eleccion.equals("0")) {
                    salir = true;
                } else {
                    ejecutar(eleccion, in);
                }
            }
        } catch (NoSuchElementException finDeEntrada) {
            // la entrada se cerró: se termina el programa
        }
        System.out.println("¡Hasta luego!");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("=== RED SOCIAL UNIVERSITARIA ===");
        for (int i = 1; i < OPCIONES.length; i++) {
            if (OPCIONES[i] != null) {
                System.out.println(" " + i + ". " + OPCIONES[i].titulo());
            }
        }
        System.out.println(" 0. Salir");
    }

    private static void ejecutar(String eleccion, Scanner in) {
        int numero = -1;
        try {
            numero = Integer.parseInt(eleccion);
        } catch (NumberFormatException e) {
            // queda -1: se informa como opción inválida
        }
        if (numero < 1 || numero >= OPCIONES.length || OPCIONES[numero] == null) {
            System.out.println("Opción inválida.");
            return;
        }
        System.out.println();
        System.out.println("--- " + OPCIONES[numero].titulo() + " ---");
        OPCIONES[numero].ejecutar(in);
    }

    /** Ejecuta cada opción registrada alimentándola con líneas vacías (todos los valores por defecto). */
    static void demo() {
        Scanner respuestas = new Scanner("\n".repeat(200));
        for (int i = 1; i < OPCIONES.length; i++) {
            if (OPCIONES[i] != null) {
                System.out.println();
                System.out.println("=== " + i + ". " + OPCIONES[i].titulo() + " ===");
                OPCIONES[i].ejecutar(respuestas);
            }
        }
    }
}
```

Reglas para toda clase `*Consola`:
- Toda lectura del usuario pasa por `Entrada.leer` / `Entrada.leerEntero` **con valor por defecto** (así `--demo` corre solo).
- Los errores de uso (`IllegalArgumentException`) se capturan y se muestran como `Error: ` + mensaje; el programa no se cae.
- Los datos de ejemplo están fijos dentro de cada consola (o en `DatosRed`, para la red compartida).

## 3. Red de difusión compartida (Lucas: `Grafo`, paso L1–L2; `DatosRed`, paso L8)

Usan `Grafo` Lucas (Dijkstra, A1) y Fadi (B3). **API pública exacta:**

```java
package redsocial.grafo;

public final class Grafo {
    /** Vínculo saliente: usuario destino (por índice) y tiempo en minutos. */
    public record Arista(int destino, int minutos) { }

    public int agregarUsuario(String nombre);
    public void agregarVinculo(String origen, String destino, int minutos);
    public int cantidadUsuarios();
    public boolean contiene(String nombre);
    public int indiceDe(String nombre);
    public String nombreDe(int indice);
    public List<Arista> vecinos(int indice);
    public List<String> usuarios();
}
```

| Método | Comportamiento |
|---|---|
| `agregarUsuario` | Idempotente. Devuelve el índice (0, 1, 2… en orden de alta). Nombre null o en blanco ⇒ `IllegalArgumentException`. |
| `agregarVinculo` | Vínculo **dirigido** `origen → destino`. Crea los usuarios que falten. `minutos <= 0` o `origen` igual a `destino` ⇒ `IllegalArgumentException`. Existe **un solo vínculo por par ordenado**: si ya había, se conserva el de menor tiempo. |
| `indiceDe` | Índice del usuario. Si no existe ⇒ `IllegalArgumentException("El usuario 'X' no existe en la red")`. |
| `vecinos(i)` | Vínculos salientes del usuario `i`, en orden de inserción, como lista de solo lectura. |
| `usuarios()` | Nombres en orden de índice, de solo lectura. |

`app/DatosRed.java` (la red de ejemplo de las opciones 2, 4 y 6; el orden de carga determina los índices y los resultados esperados):

```java
package redsocial.app;

import redsocial.grafo.Grafo;

/**
 * Red de difusión de ejemplo compartida por las opciones 2, 4 y 6.
 */
public final class DatosRed {

    private DatosRed() { }

    /** Red dirigida de 8 usuarios. Hugo solo transmite (nadie le transmite a él). */
    public static Grafo red() {
        Grafo g = new Grafo();
        g.agregarVinculo("Ana", "Bruno", 10);
        g.agregarVinculo("Ana", "Carla", 5);
        g.agregarVinculo("Carla", "Bruno", 3);
        g.agregarVinculo("Bruno", "Diego", 7);
        g.agregarVinculo("Carla", "Diego", 15);
        g.agregarVinculo("Carla", "Eva", 20);
        g.agregarVinculo("Diego", "Eva", 4);
        g.agregarVinculo("Eva", "Facu", 6);
        g.agregarVinculo("Diego", "Facu", 12);
        g.agregarVinculo("Facu", "Gina", 2);
        g.agregarVinculo("Eva", "Gina", 9);
        g.agregarVinculo("Bruno", "Eva", 25);
        g.agregarVinculo("Gina", "Carla", 8);
        g.agregarVinculo("Hugo", "Ana", 1);
        return g;
    }
}
```

## 4. Líneas clave de salida (contrato del test de integración `MainDemoTest`)

Estas líneas deben aparecer **literalmente** en la salida de `--demo`. Las clases `*Consola` pueden imprimir más cosas alrededor.

| Opción | Línea (literal) |
|---|---|
| 1 Muro | `Orden por recencia (ids): 6, 5, 4, 2, 3, 7, 1` |
| 1 Muro | `Orden por relevancia (ids): 5, 2, 3, 7, 1, 4, 6` |
| 2 Cadena más rápida | `Decisiones tomadas:` |
| 2 Cadena más rápida | `Cadena: Ana -> Carla -> Bruno -> Diego -> Eva -> Facu -> Gina \| Tiempo total: 27 min` (en la salida real el separador es `|`) |
| 3 Iniciativas | `Iniciativas seleccionadas: #1, #2, #4` |
| 3 Iniciativas | `Costo total: 45 \| Impacto obtenido: 230 \| Presupuesto restante: 5` (separadores `|`) |
| 4 A1 | `No existe cadena de difusión de Ana a Hugo` |
| 5 B1 | `Equipos válidos: 7` |
| 6 B3 | `Cadenas válidas: 10` |
| 6 B3 | `Más rápida: Ana -> Carla -> Bruno -> Diego -> Eva -> Gina \| 28 min` (separador `|`) |

## 5. Resultados esperados con los datos de ejemplo

Fueron obtenidos ejecutando una implementación de control del equipo. Sirven para verificar cada funcionalidad.

**Muro (opción 1).** Recientes: `6, 5, 4, 2, 3, 7, 1`. Relevantes: `5, 2, 3, 7, 1, 4, 6`
(la 5 y la 2 empatan en relevancia 60 y gana la más reciente; la 3 y la 7 son idénticas y gana el id menor).

**Cadena más rápida (opción 2), Ana → Gina.** `Ana -> Carla -> Bruno -> Diego -> Eva -> Facu -> Gina`, 27 min. Traza (21 líneas; la consola
imprime cada una con 2 espacios de sangría extra):

```
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
```

**Iniciativas (opción 3), presupuesto 50.** Seleccionadas `#1, #2, #4`; costo 45; impacto 230; restante 5.
Con presupuesto 0: ninguna, `Costo total: 0 | Impacto obtenido: 0 | Presupuesto restante: 0`.

**A1 (opción 4).** Matriz de tiempos mínimos (`-` = sin cadena):

```
       Ana    Bruno  Carla  Diego  Eva    Facu   Gina   Hugo   
Ana    0      8      5      15     19     25     27     -      
Bruno  -      0      27     7      11     17     19     -      
Carla  -      3      0      10     14     20     22     -      
Diego  -      23     20     0      4      10     12     -      
Eva    -      19     16     26     0      6      8      -      
Facu   -      13     10     20     24     0      2      -      
Gina   -      11     8      18     22     28     0      -      
Hugo   1      9      6      16     20     26     28     0      
```

Ana → Gina: 27 min por la misma cadena de la opción 2. Ana → Hugo: sin cadena (nadie le transmite a Hugo).

**B1 (opción 5).** Estudiantes `Lucia{Java,SQL}`, `Mateo{Java,UI}`, `Sofia{SQL,Testing}`, `Tomas{UI,Testing}`, `Valen{Java,Testing}`, `Zoe{SQL,UI}`;
tamaño 3; requeridas Java, SQL, UI y Testing; obligatorio Lucia; Mateo y Sofia incompatibles. 7 equipos, en este orden de hallazgo:

```
  [Lucia, Mateo, Tomas]
  [Lucia, Mateo, Valen]
  [Lucia, Sofia, Tomas]
  [Lucia, Sofia, Zoe]
  [Lucia, Tomas, Valen]
  [Lucia, Tomas, Zoe]
  [Lucia, Valen, Zoe]
```

**B3 (opción 6), Ana → Gina, máximo 40 min, hasta 4 intermediarios, sin excluidos.** 10 cadenas, en este orden de hallazgo
(DFS que recorre los vecinos en orden de inserción); la más rápida es `Ana -> Carla -> Bruno -> Diego -> Eva -> Gina | 28 min`:

```
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
```

(Dijkstra da 27 min porque esa cadena tiene 5 intermediarios; B3 está limitada a 4. Esa diferencia va al informe.)

## 6. Convenciones del proyecto

- Java 21, Maven, JUnit Jupiter 5.10.2. **Sin otras dependencias.** Paquete raíz `redsocial`.
- Todo en **español**: nombres, Javadoc, comentarios y mensajes al usuario.
- Cada clase de algoritmo lleva Javadoc con la **estrategia**, una justificación breve y la **complejidad temporal**; el código lleva
  comentarios de fase (`// DIVIDIR`, `// DECISIÓN GREEDY`, `// RECURRENCIA`, `// ELEGIR`, `// DESHACER (backtrack)`, `// PODA: …`).
  La consigna exige que la estrategia **se reconozca en el algoritmo implementado**, no solo en el informe.
- **Prohibido en `src/main`:** `Arrays.sort`, `Arrays.parallelSort`, `Collections.sort`, `List.sort`, `Stream.sorted`, `TreeMap`, `TreeSet`
  y cualquier `Comparator` usado para ordenar. (La consigna lo exige para el muro; el equipo lo cumple en todo el proyecto.)
- Errores de uso ⇒ `IllegalArgumentException` con mensaje claro.
- La salida usa solo caracteres ASCII para símbolos (`->`, `>=`, `infinito`); las tildes son válidas.
- Tests: JUnit 5, `@DisplayName` en español, una clase `XTest` por clase `X`, azar con semilla fija, y comparación contra fuerza bruta
  donde se indique. Los tests de una persona no dependen de clases de otra, salvo `Grafo` (Lucas).
- Commits y ramas: ver `docs/DIVISION_TAREAS.md` §5.

## 7. Cómo cambiar un contrato

Un contrato solo se cambia si hay una razón de peso. Pasos: (1) avisar a los tres; (2) un commit `docs(contratos): …` en una rama propia que
actualice este archivo, aprobado por las otras dos personas; (3) recién después se adapta el código que lo usa. Mientras tanto, nadie
cambia la firma de una clase que otra persona usa.
