package redsocial.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import redsocial.modelo.Publicacion;
import redsocial.muro.MuroOrdenador;

/**
 * Opción 1 del menú: muro de publicaciones ordenado por recencia y por relevancia.
 */
public class MuroConsola implements Opcion {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public String titulo() {
        return "Muro de publicaciones (Divide y Conquista: Merge Sort)";
    }

    @Override
    public void ejecutar(Scanner in) {
        List<Publicacion> muro = publicacionesDeEjemplo();
        System.out.println("Relevancia = reacciones + " + Publicacion.PESO_COMENTARIO
                + " x comentarios. Desempate: más reciente y luego id menor.");

        List<Publicacion> recientes = MuroOrdenador.masRecientesPrimero(muro);
        System.out.println();
        System.out.println("Más recientes primero:");
        imprimirTabla(recientes);
        System.out.println("Orden por recencia (ids): " + ids(recientes));

        List<Publicacion> relevantes = MuroOrdenador.masRelevantesPrimero(muro);
        System.out.println();
        System.out.println("Más relevantes primero:");
        imprimirTabla(relevantes);
        System.out.println("Orden por relevancia (ids): " + ids(relevantes));
    }

    private static List<Publicacion> publicacionesDeEjemplo() {
        LocalDateTime base = LocalDateTime.of(2026, 10, 1, 9, 0);
        List<Publicacion> muro = new ArrayList<>();
        muro.add(new Publicacion(1, "Ana", base, 12, 3));
        muro.add(new Publicacion(2, "Bruno", base.plusHours(5), 40, 10));
        muro.add(new Publicacion(3, "Carla", base.plusHours(5), 25, 5));
        muro.add(new Publicacion(4, "Diego", base.plusDays(1), 8, 1));
        muro.add(new Publicacion(5, "Eva", base.plusDays(1).plusHours(3), 30, 15));
        muro.add(new Publicacion(6, "Facu", base.plusDays(2), 3, 0));
        muro.add(new Publicacion(7, "Gina", base.plusHours(5), 25, 5));
        return muro;
    }

    private static void imprimirTabla(List<Publicacion> publicaciones) {
        System.out.printf("%-4s %-8s %-17s %10s %11s %11s%n",
                "ID", "Autor", "Fecha", "Reacciones", "Comentarios", "Relevancia");
        for (Publicacion p : publicaciones) {
            System.out.printf("%-4d %-8s %-17s %10d %11d %11d%n", p.id(), p.autor(),
                    p.fechaHora().format(FORMATO), p.reacciones(), p.comentarios(), p.relevancia());
        }
    }

    private static String ids(List<Publicacion> publicaciones) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < publicaciones.size(); i++) {
            if (i > 0) {
                texto.append(", ");
            }
            texto.append(publicaciones.get(i).id());
        }
        return texto.toString();
    }
}
