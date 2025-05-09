package domain;

import java.util.Random;

public class PlantsIntelligent {
    private Tablero tablero;
    private EntidadesActualizables entidadesActualizables;
    private EntidadesAccesibles entidadesAccesibles;

    public PlantsIntelligent(Tablero tablero, EntidadesActualizables entidadesActualizables,
                             EntidadesAccesibles entidadesAccesibles) {
        this.tablero = tablero;
        this.entidadesActualizables = entidadesActualizables;
        this.entidadesAccesibles = entidadesAccesibles;
    }

    public void ejecutarDefensa(int fila) {
        for (int columna = 0; columna < tablero.getColumnas(); columna++) {
            // Si hay zombies en esta fila, coloca WallNuts en la última columna
            if (hayZombiesEnFila(fila)) {
                if (!tablero.celdaOcupada( fila,columna)) {
                    WallNut wallnut = new WallNut(tablero.getColumnas() - 1, fila, 4000, 50);
                    tablero.agregarEntidad(wallnut);
                    System.out.println("Wallnut colocada en (" + (tablero.getColumnas() - 1) + ", " + fila + ")");
                }
            }

            // Si no hay Peashooter, coloca uno en la segunda columna
            if (!tablero.celdaOcupada( 2,columna)) {
                Peashooter peashooter = new Peashooter(2, fila, 300, 100, 20, tablero,
                        entidadesActualizables, entidadesAccesibles);
                tablero.agregarEntidad(peashooter);
                // Aquí actualizamos la lista de entidades
                entidadesActualizables.actualizarEntidades(tablero.getEntidades());
                System.out.println("Peashooter colocado en (1, " + fila + ")");
            }

            // Si no hay generadores de soles, coloca Girasoles en la primera columna
            if (!tablero.celdaOcupada( 1,columna)) {
                Girasol girasol = new Girasol(1, fila, 100, 50);
                tablero.agregarEntidad(girasol);
                // Actualizamos la lista de entidades después de agregar el girasol
                entidadesActualizables.actualizarEntidades(tablero.getEntidades());
                System.out.println("Girasol colocado en (0, " + fila + ")");
            }
        }
    }

    private boolean hayZombiesEnFila(int fila) {
        return tablero.getEntidades().stream()
                .anyMatch(entidad -> entidad instanceof Zombie && entidad.getyPos() == fila);
    }



    public void ejecutarDefensaEnFila(int fila) {
        Random random = new Random();

        // Girasoles en columna 1
        if (!tablero.celdaOcupada(fila, 1)) { // Verifica si la celda está libre en la columna 1
            Girasol girasol = new Girasol(1, fila, 100, 50);
            tablero.agregarEntidad(girasol);
            entidadesActualizables.actualizarEntidades(tablero.getEntidades());
            System.out.println("Girasol colocado en fila: " + fila + ", columna: 1");
        }

        // Peashooters en columna 2
        if (!tablero.celdaOcupada(fila, 2)) { // Verifica si la celda está libre en la columna 2
            Peashooter peashooter = new Peashooter(2, fila, 300, 100, 20, tablero,
                    entidadesActualizables, entidadesAccesibles);
            tablero.agregarEntidad(peashooter);
            entidadesActualizables.actualizarEntidades(tablero.getEntidades());
            System.out.println("Peashooter colocado en fila: " + fila + ", columna: 2");
        }
    }
}
