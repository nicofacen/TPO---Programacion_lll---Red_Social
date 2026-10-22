package redsocial.app;

import java.util.List;
import java.util.Scanner;
import redsocial.financiamiento.SeleccionIniciativas;
import redsocial.financiamiento.SeleccionIniciativas.Resultado;
import redsocial.modelo.Iniciativa;

/**
 * Opción 3 del menú: selección de iniciativas para financiar con presupuesto limitado
 * (Programación Dinámica: mochila 0/1).
 */
public final class IniciativasConsola implements Opcion {

    @Override
    public String titulo() {
        return "Selección de iniciativas (Programación Dinámica: mochila 0/1)";
    }

    @Override
    public void ejecutar(Scanner in) {
        List<Iniciativa> iniciativas = iniciativasDeEjemplo();
        System.out.println("Iniciativas disponibles:");
        for (Iniciativa iniciativa : iniciativas) {
            System.out.println("  #" + iniciativa.id()
                    + " (costo: " + iniciativa.costo()
                    + ", impacto: " + iniciativa.impacto() + ")");
        }

        try {
            int presupuesto = Entrada.leerEntero(in, "Inserte Presupuesto, por defecto:", 50);
            Resultado resultado = SeleccionIniciativas.seleccionar(iniciativas, presupuesto);
            System.out.println("Iniciativas seleccionadas: " + formatearSeleccionadas(resultado.seleccionadas()));
            System.out.println("Costo total: " + resultado.costoTotal()
                    + " | Impacto obtenido: " + resultado.impactoTotal()
                    + " | Presupuesto restante: " + resultado.presupuestoRestante());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static List<Iniciativa> iniciativasDeEjemplo() {
        return List.of(
                new Iniciativa(1, 10, 60),
                new Iniciativa(2, 15, 80),
                new Iniciativa(3, 30, 120),
                new Iniciativa(4, 20, 90),
                new Iniciativa(5, 25, 70)
        );
    }

    private static String formatearSeleccionadas(List<Iniciativa> seleccionadas) {
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
