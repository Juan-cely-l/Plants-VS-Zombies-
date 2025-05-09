package domain;

public class Shovel {

    /**
     * Remueve una planta del tablero.
     *
     * @param xPos Posición X de la planta a remover.
     * @param yPos Posición Y de la planta a remover.
     * @param tablero Referencia al tablero donde se removerá la planta.
     */
    public void removerPlanta(int xPos, int yPos, Tablero tablero) {
        boolean eliminada = tablero.getEntidades().removeIf(entidad ->
                entidad instanceof Planta && entidad.getxPos() == xPos && entidad.getyPos() == yPos);

        if (eliminada) {
            System.out.println("Planta removida de la posición (" + xPos + ", " + yPos + ").");
        } else {
            System.out.println("No se encontró ninguna planta en la posición (" + xPos + ", " + yPos + ").");
        }
    }
}
