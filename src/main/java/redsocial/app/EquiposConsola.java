package redsocial.app;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import redsocial.equipos.FormacionEquipos;
import redsocial.equipos.FormacionEquipos.Incompatibilidad;
import redsocial.modelo.Estudiante;

/**
 * Opción 5 del menú: B1, formación de equipos con restricciones (Backtracking). Usa datos de ejemplo fijos.
 */
public class EquiposConsola implements Opcion {

    private static final int TAMANO = 3;

    @Override
    public String titulo() {
        return "B1 · Formación de equipos con restricciones (Backtracking)";
    }

    @Override
    public void ejecutar(Scanner in) {
        List<Estudiante> estudiantes = estudiantesDeEjemplo();
        Set<String> requeridas = conjunto("Java", "SQL", "UI", "Testing");
        Set<String> obligatorios = conjunto("Lucia");
        List<Incompatibilidad> incompatibles = List.of(new Incompatibilidad("Mateo", "Sofia"));

        System.out.println("Estudiantes:");
        for (Estudiante e : estudiantes) {
            System.out.println("  " + e.nombre() + ": " + e.habilidades());
        }
        System.out.println("Tamaño del equipo: " + TAMANO
                + " | Habilidades requeridas: " + requeridas
                + " | Obligatorios: " + obligatorios
                + " | No pueden coincidir: " + describir(incompatibles));

        List<List<String>> equipos =
                FormacionEquipos.encontrarEquipos(estudiantes, TAMANO, requeridas, obligatorios, incompatibles);
        System.out.println();
        System.out.println("Equipos válidos: " + equipos.size());
        for (List<String> equipo : equipos) {
            System.out.println("  " + equipo);
        }
    }

    private static List<Estudiante> estudiantesDeEjemplo() {
        List<Estudiante> estudiantes = new ArrayList<>();
        estudiantes.add(new Estudiante("Lucia", conjunto("Java", "SQL")));
        estudiantes.add(new Estudiante("Mateo", conjunto("Java", "UI")));
        estudiantes.add(new Estudiante("Sofia", conjunto("SQL", "Testing")));
        estudiantes.add(new Estudiante("Tomas", conjunto("UI", "Testing")));
        estudiantes.add(new Estudiante("Valen", conjunto("Java", "Testing")));
        estudiantes.add(new Estudiante("Zoe", conjunto("SQL", "UI")));
        return estudiantes;
    }

    /** Conjunto que conserva el orden de inserción, para que la salida sea siempre la misma. */
    private static Set<String> conjunto(String... elementos) {
        Set<String> resultado = new LinkedHashSet<>();
        for (String e : elementos) {
            resultado.add(e);
        }
        return resultado;
    }

    private static String describir(List<Incompatibilidad> incompatibles) {
        if (incompatibles.isEmpty()) {
            return "ninguno";
        }
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < incompatibles.size(); i++) {
            if (i > 0) {
                texto.append(", ");
            }
            texto.append(incompatibles.get(i).a()).append(" y ").append(incompatibles.get(i).b());
        }
        return texto.toString();
    }
}
