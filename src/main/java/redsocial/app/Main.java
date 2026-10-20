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
        OPCIONES[2] = new CadenaRapidaConsola();

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
