package domain;

import java.util.Random;

public class PlantsStrategic {
    private Tablero tablero;
    private EntidadesActualizables entidadesActualizables;

    public PlantsStrategic(Tablero tablero, EntidadesActualizables entidadesActualizables) {
        this.tablero = tablero;
        this.entidadesActualizables = entidadesActualizables;
    }


    public void ejecutarEstrategiaLimitada() {
        Random random = new Random();

        // Generar una fila aleatoria
        int filaAleatoria = random.nextInt(5);



        // ECIPlant o PotatoMine en columna 3
        if (!tablero.celdaOcupada(filaAleatoria, 3)) {
            if (random.nextBoolean()) {
                ECIPlant eciPlant = new ECIPlant(3, filaAleatoria, 150, 75);
                tablero.agregarEntidad(eciPlant);
                System.out.println("ECIPlant colocada en fila: " + filaAleatoria + ", columna: 3");
            } else {
                PotatoMine potatoMine = new PotatoMine(3, filaAleatoria, 80, 25);
                tablero.agregarEntidad(potatoMine);
                System.out.println("PotatoMine colocada en fila: " + filaAleatoria + ", columna: 3");
            }
            entidadesActualizables.actualizarEntidades(tablero.getEntidades());
        }

        // WallNuts en columna 4
        if (!tablero.celdaOcupada(filaAleatoria, 4)) {
            WallNut wallNut = new WallNut(4, filaAleatoria, 4000, 50);
            tablero.agregarEntidad(wallNut);
            entidadesActualizables.actualizarEntidades(tablero.getEntidades());
            System.out.println("WallNut colocada en fila: " + filaAleatoria + ", columna: 4");
        }
    }
}