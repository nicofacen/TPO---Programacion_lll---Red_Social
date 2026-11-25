package redsocial.app;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import redsocial.actividades.SeleccionActividades;
import redsocial.actividades.SeleccionActividades.Resultado;
import redsocial.modelo.Actividad;

/**
 * Opción 7 del menú: A3, selección de actividades compatibles con máximo interés total
 * (Programación Dinámica: intervalos ponderados). Usa datos de ejemplo fijos.
 */
public final class ActividadesConsola implements Opcion {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String titulo() {
        return "A3 · Selección de actividades compatibles (Programación Dinámica: intervalos ponderados)";
    }

    @Override
    public void ejecutar(Scanner in) {
        List<Actividad> actividades = actividadesDeEjemplo();
        System.out.println("Actividades disponibles (una actividad puede comenzar cuando finaliza otra):");
        imprimirTabla(actividades);

        Resultado resultado = SeleccionActividades.seleccionar(actividades);
        System.out.println();
        System.out.println("Actividades elegidas (en orden cronológico):");
        imprimirTabla(resultado.seleccionadas());
        System.out.println("Actividades seleccionadas: " + formatearIds(resultado.seleccionadas())
                + " | Interés total obtenido: " + resultado.interesTotal());
    }

    private static List<Actividad> actividadesDeEjemplo() {
        return List.of(
                new Actividad(1, "Hackatón Express", LocalTime.of(12, 0), LocalTime.of(15, 0), 95),
                new Actividad(2, "Charla de IA", LocalTime.of(9, 0), LocalTime.of(11, 0), 30),
                new Actividad(3, "Feria de Becas", LocalTime.of(14, 0), LocalTime.of(16, 0), 45),
                new Actividad(4, "Taller de Git", LocalTime.of(10, 0), LocalTime.of(13, 0), 80),
                new Actividad(5, "Cierre Musical", LocalTime.of(15, 0), LocalTime.of(17, 0), 40),
                new Actividad(6, "Club de Algoritmos", LocalTime.of(11, 0), LocalTime.of(14, 0), 40),
                new Actividad(7, "Mesa de Proyectos", LocalTime.of(13, 0), LocalTime.of(15, 0), 55)
        );
    }

    private static void imprimirTabla(List<Actividad> actividades) {
        System.out.printf("  %-4s %-20s %-13s %8s%n", "ID", "Actividad", "Horario", "Interés");
        for (Actividad a : actividades) {
            String horario = a.inicio().format(HORA) + " - " + a.fin().format(HORA);
            System.out.printf("  #%-3d %-20s %-13s %8d%n", a.id(), a.nombre(), horario, a.interes());
        }
    }

    private static String formatearIds(List<Actividad> seleccionadas) {
        if (seleccionadas.isEmpty()) {
            return "ninguna";
        }
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < seleccionadas.size(); i++) {
            if (i > 0) {
                texto.append(", ");
            }
            texto.append("#").append(seleccionadas.get(i).id());
        }
        return texto.toString();
    }
}
