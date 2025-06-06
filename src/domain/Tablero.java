package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Tablero implements Serializable {
    private int filas;
    private int columnas;
    private List<Entidad> entidades; // Lista única de todas las entidades
    private boolean[][] ocupacion; // Matriz para saber qué espacios están ocupados
    private boolean juegoTerminado;

    public Tablero(int filas, int columnas, List<Entidad> entidades) {
        this.filas = filas;
        this.columnas = columnas;
        this.entidades = new CopyOnWriteArrayList<>();
        this.ocupacion = new boolean[filas][columnas];
        this.juegoTerminado = false;

    }

    public void agregarEntidad(Entidad entidad) {
        if (entidad != null) {
            entidades.add(entidad);
        } else {
            System.out.println("Error: entidad es null");
        }
    }

    public void actualizarEstado() {
        if (juegoTerminado) return;

        for (Entidad entidad : entidades) {
            entidad.actualizar();

            // Interacciones entre entidades
            for (Entidad otra : entidades) {
                if (!entidad.equals(otra) && colisionan(entidad, otra)) {
                    entidad.interactuar(otra);
                }
            }

            if (entidad instanceof Zombie && ((Zombie) entidad).gameOver()) {
                juegoTerminado = true;
                break;
            }
        }

        // Eliminar entidades muertas
        entidades.removeIf(Entidad::estaMuerta);
        limpiarEspaciosOcupados();
    }

    private boolean colisionan(Entidad e1, Entidad e2) {
        return e1.getxPos() == e2.getxPos() && e1.getyPos() == e2.getyPos();
    }

    private void ocuparEspacio(int x, int y) {
        ocupacion[x][y] = true;
    }

    private void limpiarEspaciosOcupados() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                ocupacion[i][j] = false;
            }
        }
        for (Entidad entidad : entidades) {
            ocuparEspacio(entidad.getxPos(), entidad.getyPos());
        }
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }



    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public List<Entidad> getEntidades() {
        return entidades != null ? entidades : new ArrayList<>();
    }


    public boolean celdaOcupada(int fila, int columna) {
        boolean ocupada = entidades.stream()
                .anyMatch(entidad -> entidad.getyPos() == fila && entidad.getxPos() == columna);
        System.out.println("Verificando celda (" + fila + ", " + columna + "): " + (ocupada ? "Ocupada" : "Libre"));
        return ocupada;
    }
    public Planta obtenerPlantaEnCelda(int fila, int columna) {
        // Filtrar la lista de entidades para buscar solo plantas en la posición dada
        return entidades.stream()
                .filter(entidad -> entidad instanceof Planta) // Verifica que sea una planta
                .filter(planta -> planta.getyPos() == fila && planta.getxPos() == columna) // Coinciden las coordenadas
                .map(planta -> (Planta) planta) // Convierte la entidad a Planta
                .findFirst() // Toma la primera planta encontrada
                .orElse(null); // Si no se encuentra ninguna, retorna null
    }

}
